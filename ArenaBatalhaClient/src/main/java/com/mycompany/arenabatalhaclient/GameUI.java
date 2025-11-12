package com.mycompany.arenabatalhaclient;

import java.awt.CardLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * ESSENCIAL: A Janela Principal (JFrame) e o Controlador da UI.
 * Esta classe gere o CardLayout (a troca de telas) e
 * faz a ponte entre a lógica de Rede (TCPClientMain) e os painéis visuais.
 */
public class GameUI extends JFrame {

    // Nossos painéis (as "telas")
    private JPanel cardPanel;
    private CardLayout cardLayout;
    private SelectionPanel selectionPanel;
    private TransitionPanel transitionPanel;
    private BattlePanel battlePanel;
    private GameOverPanel gameOverPanel;
    // O "Cérebro" de Rede
    private TCPClientMain clientNet;

    /**
     * Construtor principal. Cria o CardLayout, instancia TODOS os painéis
     * de uma vez e inicia a conexão de rede numa thread separada.
     */
    public GameUI() {
        initComponents(); 
        
        // 1. Configura o CardLayout para trocar de tela
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        
        // 2. Cria TODAS as telas (elas ficam na memória)
        selectionPanel = new SelectionPanel(this);
        transitionPanel = new TransitionPanel(this);
        battlePanel = new BattlePanel(this); 
        gameOverPanel = new GameOverPanel(this);
        
        // 3. Adiciona as telas ao "baralho"
        cardPanel.add(selectionPanel, "SELECAO");
        cardPanel.add(transitionPanel, "TRANSICAO"); 
        cardPanel.add(battlePanel, "BATALHA");
        cardPanel.add(gameOverPanel, "FIM_DE_JOGO");
        
        this.add(cardPanel);
        
        // Configurações da Janela
        this.setTitle("Arena de Batalha");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setLocationRelativeTo(null);
        
        cardLayout.show(cardPanel, "SELECAO");
        
        // 4. Inicia a conexão com o servidor
        iniciarConexaoEmThread();
    }
    
    public void showBattlePanel() {
        cardLayout.show(cardPanel, "BATALHA");
        cardPanel.revalidate();
        cardPanel.repaint();
    }
    
    /**
     * Tenta estabelecer a conexão com o servidor.
     * É chamado pela iniciarConexaoEmThread().
     */
    private void conectarAoServidor() {
        try {
            // =============================================================
            //String ipServidor = "127.0.0.1"; // (Localhost)
            String ipServidor = "192.168.56.100"; // (IP da VM Servidor)
            // =============================================================
            int porta = 6789;
            
            this.clientNet = new TCPClientMain(ipServidor, porta, this); 
            System.out.println("Conectado ao servidor!");
            
        } catch (Exception e) {
            System.err.println("Erro ao conectar| " + e.getMessage());
            // Atualiza a UI na thread do Swing
            SwingUtilities.invokeLater(() -> {
                 selectionPanel.atualizarLog("ERRO| Não foi possível conectar ao servidor.");
            });
        }
    }
    
    /**
     * Inicia a conexão de rede numa thread separada para não travar a UI.
     */
    private void iniciarConexaoEmThread() {
        new Thread(() -> {
            conectarAoServidor();
        }).start();
    }
    
    /**
     * ESSENCIAL: O Roteador de Mensagens do Cliente.
     * Este método é chamado pelo TCPClientMain (de uma thread de rede).
     * Ele usa SwingUtilities.invokeLater para garantir que qualquer
     * atualização da UI (ex: mudar um log, trocar de tela) aconteça
     * de forma segura na Thread de Eventos do Swing.
     */
    public void escreverMensagem(String rawMessage) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                System.out.println("[SERVIDOR]| " + rawMessage);
                
                String[] partes = rawMessage.split("\\|", 2);
                String tipo = partes[0].toUpperCase();
                String dados = (partes.length > 1) ? partes[1] : "";
                
                // --- ROTEADOR DE MENSAGENS ---
                // Decide qual painel deve receber a informação
                switch (tipo) {
                    case "OPONENTE_ENCONTRADO":
                        selectionPanel.setOponenteConectado(true);
                        break;
                        
                    case "INICIAR_PARTIDA":
                        battlePanel.iniciarBatalha(dados);
                        
                        // 2. Inicia a animação de transição
                        transitionPanel.startTransition();
                        
                        // 3. Vira a "carta" para a tela de transição
                        cardLayout.show(cardPanel, "TRANSICAO");
                        break;
                        
                    // --- Mensagens da TELA DE BATALHA ---
                    case "SEU_TURNO":
                        battlePanel.definirTurno(true);
                        break;
                        
                    case "ESPERE_TURNO":
                        battlePanel.definirTurno(false);
                        break;
                        
                    case "STATUS_JOGADOR":
                        battlePanel.atualizarHpJogador(Integer.parseInt(dados));
                        break;
                        
                    case "STATUS_OPONENTE":
                        battlePanel.atualizarHpOponente(Integer.parseInt(dados));
                        break;
                        
                    case "STATUS_CURAS":
                        battlePanel.atualizarCuras(Integer.parseInt(dados));
                        break;
                        
                    case "FIM_DE_JOGO":
                        String[] subDados = dados.split("\\|");
                        
                        if (subDados.length >= 2) {
                            String vencedorNome = subDados[0];
                            boolean vencedorShiny = Boolean.parseBoolean(subDados[1]);
                            
                            String seuPokemon = battlePanel.getSeuPokemon();
                            String jogadorID = vencedorNome.equals(seuPokemon) ? "VOCÊ VENCEU!" : "O OPONENTE VENCEU!";
                            
                            battlePanel.mostrarFimDeJogo(vencedorNome);
                            gameOverPanel.mostrarVencedor(vencedorNome, vencedorShiny, jogadorID);
                            
                            cardLayout.show(cardPanel, "FIM_DE_JOGO");
                        }
                        break;

                    case "LOG":
                        // Vê qual tela está ativa e manda o log para ela
                        if (battlePanel.isShowing()) {
                            battlePanel.adicionarLog(dados);
                        } else {
                            selectionPanel.atualizarLog(dados);
                            selectionPanel.setOponenteConectado(false);
                        }
                        break;
                        
                    default:
                        System.out.println("Tipo de msg não tratada| " + tipo);
                }
            }
        });
    }
    
    public void showGameOverPanel() {
        cardLayout.show(cardPanel, "FIM_DE_JOGO");
        gameOverPanel.revalidate();
        gameOverPanel.repaint();
        cardPanel.revalidate();
        cardPanel.repaint();
    }
    
    /**
     * ESSENCIAL: Método que permite que os painéis (Selection, Battle)
     * enviem dados para o servidor através da conexão principal.
     */
    public void enviarParaServidor(String mensagem) {
        if (clientNet != null) {
            clientNet.writeMessage(mensagem);
        }
    }
        
    /**
     * ESSENCIAL: Chamado pelo GameOverPanel para reiniciar o jogo.
     * Esta função corrige o bug do "freeze" do Swing.
     * Ela NÃO destrói os painéis (evita o removeAll()).
     * Em vez disso, ela chama os métodos 'resetState()' dos painéis filhos
     * para que eles reativem seus botões, e então tenta reconectar.
     */
    public void voltarParaSelecao() {
        // 1. DESLIGA A CONEXÃO ANTIGA
        try {
            if (clientNet != null) {
                clientNet.closeConnection(); // Fecha o socket e interrompe o handler
                Thread.sleep(50); // Dá um tempo para a porta fechar
                clientNet = null; // Zera a referência
            }
        } catch (Exception e) {
            System.err.println("Erro ao fechar conexão antiga durante o reset| " + e.getMessage());
        }

        // 2. RESETA O ESTADO DOS PAINÉIS
        if (selectionPanel != null) {
            selectionPanel.resetState(); 
        }
        if (gameOverPanel != null) {
            gameOverPanel.resetState(); // Esconde o painel de game over
        }
        
        cardLayout.show(cardPanel, "SELECAO");
        

        cardPanel.revalidate();
        cardPanel.repaint();

        iniciarConexaoEmThread();
    }
        
    // O método main para iniciar o jogo
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            new GameUI().setVisible(true);
        });
    }
    
    // (Cole o initComponents() do NetBeans aqui)
    @SuppressWarnings("unchecked")
    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        pack();
    }
}