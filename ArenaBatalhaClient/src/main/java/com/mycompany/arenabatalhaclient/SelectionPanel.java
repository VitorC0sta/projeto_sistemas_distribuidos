package com.mycompany.arenabatalhaclient;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import javax.swing.BorderFactory;
import javax.swing.border.Border;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;

/**
 * ESSENCIAL: O painel (JPanel) que mostra a grelha de 12 Pokémon.
 * Esta classe gere a seleção visual (bordas verdes/vermelhas),
 * desativa os botões após a escolha e envia a mensagem "ESCOLHEU"
 * para o GameUI (que a passa ao servidor).
 */
public class SelectionPanel extends JPanel { 

    // Referência ao "Pai" (o GameUI) para enviar mensagens
    private GameUI parentUI;
    
    // --- Array para os 12 botões ---
    private JButton[] allButtons;
    
    // --- Lógica de Seleção ---
    private String pokemonSelecionado = null;
    private JButton botaoSelecionado = null;
    private Border bordaPadrao;
    private Border bordaSelecionada;
    
     private boolean oponenteConectado = false;
    
    // --- Componentes Visuais ---
    private JLabel lblLog;
    private JButton btnPronto;
    private JPanel painelDaGrade;

    public SelectionPanel(GameUI parent) {
        this.parentUI = parent;
        
        // --- 1. Configurações do Painel Principal (este JPanel) ---
        this.setLayout(new BorderLayout(10, 10)); 
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- 2. Cria o Painel da Grade (para os 12 botões) ---
        painelDaGrade = new JPanel(new GridLayout(3, 4, 10, 10)); // 3 linhas, 4 col, 10px de espaço
        
        // --- 3. Cria os Nomes dos Pokémon (na ordem) ---
        String[] nomesPokemon = {
            "Charmander", "Vulpix", "Squirtle", "Staryu",
            "Treecko", "Elekid", "Pikachu", "Diglett",
            "Wooper", "Numel", "Rowlet", "Emolga"
        };
        
        allButtons = new JButton[12];
        
        // --- 4. Cria os 12 botões em um loop ---
        for (int i = 0; i < 12; i++) {
            String nome = nomesPokemon[i];
            JButton btn = new JButton(); 
            
            // Adiciona o "ActionListener" (evento de clique)
            btn.addActionListener(e -> {
                selecionarPokemon(nome, btn); // Chama nossa lógica de seleção
            });
            
            allButtons[i] = btn; // Guarda no array
            painelDaGrade.add(btn); // Adiciona na grade
        }
        
        // --- 5. Cria o Log e o Botão Pronto ---
        lblLog = new JLabel("Escolha seu Pokémon...");
        lblLog.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblLog.setHorizontalAlignment(SwingConstants.CENTER);

        btnPronto = new JButton("Pronto");
        btnPronto.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnPronto.setEnabled(false); // Começa desativado
        btnPronto.addActionListener(e -> {
            if (this.pokemonSelecionado != null) {
                enviarEscolha();
            }
        });
        
        // --- 6. Adiciona os componentes ao Painel Principal ---
        this.add(painelDaGrade, BorderLayout.CENTER);
        
        JPanel painelSul = new JPanel(new BorderLayout());
        painelSul.add(lblLog, BorderLayout.NORTH);
        painelSul.add(btnPronto, BorderLayout.SOUTH);
        
        this.add(painelSul, BorderLayout.SOUTH);
        
        // --- 7. Define as Bordas e Carrega os Ícones ---
        this.bordaPadrao = allButtons[0].getBorder(); // Pega uma borda padrão
        this.bordaSelecionada = BorderFactory.createLineBorder(Color.GREEN, 3);
        
        loadButtonIcons();
    }
    
    /**
     * Carrega os ícones E FORÇA O TAMANHO DA IMAGEM
     */
    private void loadButtonIcons() {
        Dimension buttonSize = new Dimension(100, 100);
        int iconWidth = 90;
        int iconHeight = 90;
        int specialWidth = 50;
        int specialHeight = 50;
        String iconsPath = "/assets/icons/";

        try {
            String[] nomesPokemon = {
                "charmander.png", "vulpix.png", "squirtle.png", "staryu.png",
                "treecko.png", "elekid.png", "pikachu.png", "diglett.png",
                "wooper.png", "numel.png", "rowlet.png", "emolga.png"
            };

            for (int i = 0; i < 12; i++) {
                JButton btn = allButtons[i];
                String iconFile = nomesPokemon[i];
                
                ImageIcon icon;
                
                if (iconFile.equals("treecko.png") || iconFile.equals("numel.png")) {
                    icon = loadAndResizeIcon(iconsPath + iconFile, specialWidth, specialHeight);
                } else {
                    icon = loadAndResizeIcon(iconsPath + iconFile, iconWidth, iconHeight);
                }
                if (icon != null) {
                    btn.setIcon(icon);
                }
                
                btn.setPreferredSize(buttonSize);
                btn.setMinimumSize(buttonSize);
                btn.setMaximumSize(buttonSize);
            }
            
        } catch (Exception e) {
            System.err.println("Erro ao carregar ícones| " + e.getMessage());
            lblLog.setText("Erro ao carregar ícones. Verifique os nomes.");
            e.printStackTrace(); 
        }
    }
    
    /**
     * Função de ajuda para carregar e redimensionar um ícone.
     */
    private ImageIcon loadAndResizeIcon(String path, int width, int height) {
        java.net.URL imgUrl = getClass().getResource(path);
        if (imgUrl == null) {
             System.err.println("Não foi possível encontrar| " + path);
             return null;
        }
        ImageIcon originalIcon = new ImageIcon(imgUrl);
        Image image = originalIcon.getImage();
        Image resizedImage = image.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH);
        return new ImageIcon(resizedImage);
    }
    
    /**
     * Método que o GameUI vai chamar para nos dar feedback do servidor.
     */
    public void atualizarLog(String mensagem) {
        lblLog.setText(mensagem); 
    }
    
    // --- LÓGICA DE SELEÇÃO E ENVIO ---
    
    /**
     * ESSENCIAL: Chamado quando um botão de Pokémon é clicado.
     * Atualiza a seleção visual (bordas) e ativa o botão "Pronto".
     */
    private void selecionarPokemon(String nome, JButton botaoClicado) {
        this.pokemonSelecionado = nome;
        this.botaoSelecionado = botaoClicado;
        
        // Limpa todas as bordas
        for (JButton btn : allButtons) {
            btn.setBorder(bordaPadrao);
        }
        
        // Destaca o selecionado
        botaoClicado.setBorder(bordaSelecionada);
        lblLog.setText("Você selecionou [" + nome + "]. Clique em 'Pronto'!");
        btnPronto.setEnabled(this.oponenteConectado);
    }

    /**
     * ESSENCIAL: Chamado pelo botão "Pronto".
     * Envia a escolha para o servidor (via GameUI) e desativa
     * todos os botões para impedir uma segunda escolha.
     */
    private void enviarEscolha() {
        // O servidor espera o nome com a primeira letra maiúscula (ex: "Charmander")
        String nomePokemonServidor = pokemonSelecionado.substring(0, 1).toUpperCase() + pokemonSelecionado.substring(1);
        
        String mensagem = "ESCOLHEU|" + nomePokemonServidor;
            
        parentUI.enviarParaServidor(mensagem);
        
        // Define a borda como "confirmada" (vermelha)
        Border bordaConfirmada = BorderFactory.createLineBorder(Color.RED, 3);
        botaoSelecionado.setBorder(bordaConfirmada);
        
        desativarTodosOsBotoes();
        lblLog.setText("Você escolheu " + nomePokemonServidor + "! Aguardando oponente...");
    }
    
    private void desativarTodosOsBotoes() {
        for (JButton btn : allButtons) {
            if (btn != null) {
                btn.setEnabled(false);
            }
        }
        btnPronto.setEnabled(false);
    }
    
    // =========================================================================
    // FUNÇÃO CRÍTICA DA CORREÇÃO (PARA O RELATÓRIO)
    // =========================================================================
    
    /**
     * ESSENCIAL (NOVO): Define o estado de conexão do oponente.
     * Chamado pelo GameUI quando recebe a mensagem OPONENTE_ENCONTRADO.
     * @param conectado True se o oponente estiver no slot P2.
     */
        public void setOponenteConectado(boolean conectado) {
            this.oponenteConectado = conectado;

            if (conectado) {
                lblLog.setText("Oponente encontrado! Escolha seu Pokémon e clique em 'Pronto'.");

                // Se um Pokémon já foi selecionado, ativamos o botão Pronto agora
                if (this.pokemonSelecionado != null) {
                    btnPronto.setEnabled(true);
                }
            } else {
                // Oponente desconectou
                lblLog.setText("Aguardando oponente...");
                btnPronto.setEnabled(false);

                // Limpa o log local se estiver esperando a escolha
                if (this.pokemonSelecionado != null) {
                    this.pokemonSelecionado = null;
                    this.botaoSelecionado.setBorder(bordaPadrao);
                    this.botaoSelecionado = null;
                }
            }
        }

    /**
     * ESSENCIAL (NOVO): Reseta o painel de seleção para o estado inicial.
     * Esta função é chamada pelo GameUI (no 'voltarParaSelecao')
     * quando o jogador volta do GameOverPanel ou se reconecta.
     * Ela REATIVA os botões, permitindo que o jogador faça uma
     * nova escolha e envie a mensagem "ESCOLHEU" que o servidor está à espera.
     */
    public void resetState() {
        // 1. Limpa o log
        lblLog.setText("Oponente saiu ou jogo terminou. Reconectando...");
        
        // 2. Reseta a seleção
        pokemonSelecionado = null;
        if (botaoSelecionado != null) {
            botaoSelecionado.setBorder(bordaPadrao);
            botaoSelecionado = null;
        }
        
        // 3. Reativa TODOS os botões
        for (JButton btn : allButtons) {
            if (btn != null) {
                btn.setEnabled(true);
            }
        }
        
        // 4. Garante que o botão 'Pronto' comece desativado
        btnPronto.setEnabled(false); 
    }
    
}