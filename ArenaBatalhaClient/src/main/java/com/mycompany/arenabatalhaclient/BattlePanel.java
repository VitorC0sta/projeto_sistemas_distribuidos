package com.mycompany.arenabatalhaclient;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.Dimension;
import java.awt.Image;

public class BattlePanel extends JPanel {

    private GameUI parentUI;

    private JPanel painelCena;          
    private JLabel lblBackground;
    private JLabel lblPlayerSprite;
    private JLabel lblOpponentSprite;
    
    private JPanel panelPlayerInfo;
    private JLabel lblPlayerName;
    private JProgressBar barPlayerHP;
    private JLabel lblPlayerHPText;
    
    private JPanel panelOpponentInfo;
    private JLabel lblOpponentName;
    private JProgressBar barOpponentHP;
    private JLabel lblOpponentHPText;
    
    private JPanel panelActions;
    private JButton btnAtacar;
    private JButton btnCurar;
    private JButton btnEvasiva;
    private JButton btnFugir;
    
    private JTextArea logArea;
    private JScrollPane logScrollPane;
    
    private String seuPokemon;
    private String oponentePokemon;
    private boolean seuShiny;
    private boolean oponenteShiny;
    private int maxHP;

    public BattlePanel(GameUI parent) {
        this.parentUI = parent;
        
        this.setLayout(null);
        this.setPreferredSize(new Dimension(800, 600)); 
        this.setBackground(Color.decode("#F8F8F8")); 
          
        criarPainelCena();      
        this.add(painelCena);   

        criarInfoOponente();    
        criarInfoJogador();
        criarLogBatalha();
        criarBotoesAcao();
        

        // --- 🔧 Correção de camadas (Z-Order) para exibir tudo sobre o fundo ---
        this.setComponentZOrder(panelOpponentInfo, 0);
        this.setComponentZOrder(panelPlayerInfo, 0);
        this.setComponentZOrder(panelActions, 0);
        this.setComponentZOrder(logScrollPane, 0);
        // ------------------------------------------------------------
        
            Color corBotao = new Color(255, 255, 255, 230); // branco semi-opaco
            Color corTexto = Color.BLACK;

            for (JButton btn : new JButton[]{btnAtacar, btnCurar, btnEvasiva, btnFugir}) {
                btn.setBackground(corBotao);
                btn.setForeground(corTexto);
                btn.setOpaque(true);
                btn.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
            }

            barPlayerHP.setBackground(Color.WHITE);
            barOpponentHP.setBackground(Color.WHITE);
    }
    
    public String getSeuPokemon() {
        return seuPokemon;
    }
    
    private void criarPainelCena() {
        painelCena = new JPanel(null);
        painelCena.setBounds(0, 0, 800, 450);
        painelCena.setOpaque(true);

        lblBackground = new JLabel();
        try {
            java.net.URL imgUrl = getClass().getResource("/assets/backgrounds/battle_bg.jpg");
            if (imgUrl != null) {
                ImageIcon originalIcon = new ImageIcon(imgUrl);
                Image image = originalIcon.getImage().getScaledInstance(800, 450, Image.SCALE_SMOOTH);
                lblBackground.setIcon(new ImageIcon(image));
            } else {
                System.err.println("Atenção| Imagem de fundo não encontrada! Usando cor cinza.");
                lblBackground.setOpaque(true);
                lblBackground.setBackground(Color.DARK_GRAY);
            }
        } catch (Exception e) {
            System.err.println("Erro ao carregar background| " + e.getMessage());
            lblBackground.setOpaque(true);
            lblBackground.setBackground(Color.DARK_GRAY);
        }
        lblBackground.setBounds(0, 0, 800, 450);
        painelCena.add(lblBackground, 0);

        lblOpponentSprite = new JLabel();
        lblOpponentSprite.setBounds(550, 70, 200, 200);
        lblOpponentSprite.setOpaque(false);
        painelCena.add(lblOpponentSprite, 1);

        lblPlayerSprite = new JLabel();
        lblPlayerSprite.setBounds(230, 200, 250, 250);
        lblPlayerSprite.setOpaque(false);
        painelCena.add(lblPlayerSprite, 2);
        
        painelCena.setComponentZOrder(lblBackground, painelCena.getComponentCount() - 1);
    }

    private void criarInfoOponente() {
        panelOpponentInfo = new JPanel(null);
        panelOpponentInfo.setBounds(50, 50, 250, 70);
        panelOpponentInfo.setBorder(BorderFactory.createEtchedBorder());
        panelOpponentInfo.setOpaque(true);

        lblOpponentName = new JLabel("Oponente");
        lblOpponentName.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblOpponentName.setBounds(10, 5, 230, 20);
        
        barOpponentHP = new JProgressBar(0, 100);
        barOpponentHP.setValue(100);
        barOpponentHP.setBounds(10, 30, 230, 20);
        barOpponentHP.setForeground(Color.decode("#78C850"));
        
        lblOpponentHPText = new JLabel("100/100");
        lblOpponentHPText.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblOpponentHPText.setBounds(10, 50, 100, 15);

        panelOpponentInfo.add(lblOpponentName);
        panelOpponentInfo.add(barOpponentHP);
        panelOpponentInfo.add(lblOpponentHPText);
        this.add(panelOpponentInfo);
    }

    private void criarInfoJogador() {
        panelPlayerInfo = new JPanel(null);
        panelPlayerInfo.setBounds(500, 350, 250, 70);
        panelPlayerInfo.setBorder(BorderFactory.createEtchedBorder());
        panelPlayerInfo.setOpaque(true);
        
        lblPlayerName = new JLabel("Jogador");
        lblPlayerName.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblPlayerName.setBounds(10, 5, 230, 20);
        
        barPlayerHP = new JProgressBar(0, 100);
        barPlayerHP.setValue(100);
        barPlayerHP.setBounds(10, 30, 230, 20);
        barPlayerHP.setForeground(Color.decode("#78C850"));
        
        lblPlayerHPText = new JLabel("100/100");
        lblPlayerHPText.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblPlayerHPText.setBounds(10, 50, 100, 15);

        panelPlayerInfo.add(lblPlayerName);
        panelPlayerInfo.add(barPlayerHP);
        panelPlayerInfo.add(lblPlayerHPText);
        this.add(panelPlayerInfo);
    }

    private void criarLogBatalha() {
        logArea = new JTextArea("A batalha começou!");
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);
        logArea.setOpaque(false);
        
        logScrollPane = new JScrollPane(logArea);
        logScrollPane.setOpaque(false);
        logScrollPane.getViewport().setOpaque(false);
        logScrollPane.setBounds(10, 450, 780, 140);
        this.add(logScrollPane);
    }

    private void criarBotoesAcao() {
        panelActions = new JPanel(new GridLayout(2, 2, 10, 10));
        panelActions.setBounds(450, 270, 300, 70);
        panelActions.setOpaque(false);
        
        btnAtacar = new JButton("Atacar");
        btnAtacar.setOpaque(true);
        btnCurar = new JButton("Curar (3)");
        btnCurar.setOpaque(true);
        btnEvasiva = new JButton("Protect");
        btnEvasiva.setOpaque(true);
        btnFugir = new JButton("Fugir");
        btnFugir.setOpaque(true);
        
        btnAtacar.addActionListener(e -> parentUI.enviarParaServidor("ATACAR"));
        btnCurar.addActionListener(e -> parentUI.enviarParaServidor("CURAR"));
        btnEvasiva.addActionListener(e -> parentUI.enviarParaServidor("EVASIVA"));
        btnFugir.addActionListener(e -> parentUI.enviarParaServidor("FUGIR"));
        
        panelActions.add(btnAtacar);
        panelActions.add(btnCurar);
        panelActions.add(btnEvasiva);
        panelActions.add(btnFugir);
        this.add(panelActions);
    }

    // ------------------------------------------------------------
    // MÉTODOS DE CONTROLE DA LÓGICA
    // ------------------------------------------------------------
    public void iniciarBatalha(String dadosPartida) {
        String[] partes = dadosPartida.split("\\|");
        this.seuPokemon = partes[0];
        this.seuShiny = Boolean.parseBoolean(partes[1]);
        this.oponentePokemon = partes[2];
        this.oponenteShiny = Boolean.parseBoolean(partes[3]);
        this.maxHP = Integer.parseInt(partes[4]);
        int curas = Integer.parseInt(partes[5]);

        barPlayerHP.setMaximum(maxHP);
        barPlayerHP.setValue(maxHP);
        lblPlayerHPText.setText(maxHP + "/" + maxHP);
        
        barOpponentHP.setMaximum(maxHP);
        barOpponentHP.setValue(maxHP);
        lblOpponentHPText.setText(maxHP + "/" + maxHP);
        
        lblPlayerName.setText(seuPokemon);
        lblOpponentName.setText(oponentePokemon);
        btnCurar.setText("Curar (" + curas + ")");

        String spriteJogador = (seuShiny ? seuPokemon.toLowerCase() + "-shiny.gif" : seuPokemon.toLowerCase() + ".gif");
        String spriteOponente = (oponenteShiny ? oponentePokemon.toLowerCase() + "-shiny.gif" : oponentePokemon.toLowerCase() + ".gif");

        try {
            lblPlayerSprite.setIcon(new ImageIcon(getClass().getResource("/assets/sprites/back/" + spriteJogador)));
        } catch (Exception e) {
            System.err.println("Sprite do jogador não encontrado| " + spriteJogador);
        }

        try {
            lblOpponentSprite.setIcon(new ImageIcon(getClass().getResource("/assets/sprites/front/" + spriteOponente)));
        } catch (Exception e) {
            System.err.println("Sprite do oponente não encontrado| " + spriteOponente);
        }

        logArea.setText("Batalha iniciada| " + seuPokemon + " vs. " + oponentePokemon + "!");
        repaint();
    }

    public void definirTurno(boolean meuTurno) {
        btnAtacar.setEnabled(meuTurno);
        btnCurar.setEnabled(meuTurno);
        btnEvasiva.setEnabled(meuTurno);
        btnFugir.setEnabled(meuTurno);
        if (meuTurno) adicionarLog("--- É a sua vez! ---");
        else adicionarLog("Aguarde o oponente...");
    }

    public void atualizarHpJogador(int hp) {
        barPlayerHP.setValue(hp);
        lblPlayerHPText.setText(hp + "/" + maxHP);
        atualizarCorHP(barPlayerHP, hp);
    }

    public void atualizarHpOponente(int hp) {
        barOpponentHP.setValue(hp);
        lblOpponentHPText.setText(hp + "/" + maxHP);
        atualizarCorHP(barOpponentHP, hp);
    }

    private void atualizarCorHP(JProgressBar barra, int hp) {
        if (hp < maxHP * 0.2) barra.setForeground(Color.RED);
        else if (hp < maxHP * 0.5) barra.setForeground(Color.ORANGE);
        else barra.setForeground(Color.decode("#78C850"));
    }

    public void atualizarCuras(int curas) {
        btnCurar.setText("Curar (" + curas + ")");
        btnCurar.setEnabled(curas > 0);
    }

    public void mostrarFimDeJogo(String vencedor) {
        adicionarLog("--- FIM DE JOGO ---");
        adicionarLog("O " + vencedor + " venceu!");
        btnAtacar.setEnabled(false);
        btnCurar.setEnabled(false);
        btnEvasiva.setEnabled(false);
        btnFugir.setEnabled(false);
    }

    public void adicionarLog(String msg) {
        logArea.append("\n" + msg);
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }
}
