package com.mycompany.arenabatalhaclient;

import java.awt.CardLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

// 1. Esta classe "é" o TCPClientForm que as classes de rede esperam
public class GameUI extends JFrame {

    // Nossos painéis (as "telas")
    private JPanel cardPanel;
    private CardLayout cardLayout;
    private SelectionPanel selectionPanel;
    private BattlePanel battlePanel;
    
    // O "Cérebro" de Rede
    private TCPClientMain clientNet;

    public GameUI() {
        initComponents(); 
        
        // 1. Configura o CardLayout para trocar de tela
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        
        // 2. Cria AMBAS as telas
        selectionPanel = new SelectionPanel(this);
        battlePanel = new BattlePanel(this); // <-- ATIVADO
        
        // 3. Adiciona as telas ao "baralho"
        cardPanel.add(selectionPanel, "SELECAO");
        cardPanel.add(battlePanel, "BATALHA"); // <-- ATIVADO
        
        // Adiciona o painel principal à janela
        this.add(cardPanel);
        
        // Configurações da Janela
        this.setTitle("Arena de Batalha");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack(); // Ajusta o tamanho da janela ao painel
        this.setLocationRelativeTo(null); // Centraliza
        
        // Mostra a tela de seleção primeiroconect
        cardLayout.show(cardPanel, "SELECAO");
        
        // 4. Inicia a conexão com o servidor
        conectarAoServidor();
    }

    private void conectarAoServidor() {
        try {
            String ipServidor = "127.0.0.1"; // O IP do seu Ubuntu
            int porta = 6789;                   // A porta do servidor
            
            this.clientNet = new TCPClientMain(ipServidor, porta, this); 
            System.out.println("Conectado ao servidor!");
            
        } catch (Exception e) {
            System.err.println("Erro ao conectar: " + e.getMessage());
            selectionPanel.atualizarLog("ERRO: Não foi possível conectar ao servidor.");
        }
    }
    
    public void escreverMensagem(String rawMessage) {
        SwingUtilities.invokeLater(() -> {
            System.out.println("[SERVIDOR]: " + rawMessage);
            
            String[] partes = rawMessage.split(":", 2);
            String tipo = partes[0].toUpperCase();
            String dados = (partes.length > 1) ? partes[1] : "";
            
            // --- ROTEADOR DE MENSAGENS ---
            // Decide qual painel deve receber a informação
            
            switch (tipo) {
                // Mensagens da TELA DE SELEÇÃO
                case "OPONENTE_ENCONTRADO":
                    selectionPanel.atualizarLog(dados);
                    break;
                    
                // Mensagem CRÍTICA: Troca da Seleção para a Batalha
                case "INICIAR_PARTIDA":
                    // 1. Envia todos os dados da partida para o BattlePanel
                    battlePanel.iniciarBatalha(dados);
                    // 2. Vira a "carta" para a tela de batalha
                    cardLayout.show(cardPanel, "BATALHA");
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
                    battlePanel.mostrarFimDeJogo(dados);
                    // (Aqui você pode adicionar um botão no battlePanel
                    // que, ao ser clicado, chama um método
                    // "parentUI.voltarParaSelecao()")
                    break;

                // --- Mensagem de LOG (pode ser para qualquer tela) ---
                case "LOG":
                    // Vê qual tela está ativa e manda o log para ela
                    if (battlePanel.isShowing()) {
                        battlePanel.adicionarLog(dados);
                    } else {
                        selectionPanel.atualizarLog(dados);
                    }
                    break;
                    
                default:
                    System.out.println("Tipo de msg não tratada: " + tipo);
            }
        });
    }
    
    /**
     * Método que o SelectionPanel ou BattlePanel vão chamar
     * para enviar dados ao servidor.
     */
    public void enviarParaServidor(String mensagem) {
        if (clientNet != null) {
            clientNet.writeMessage(mensagem);
        }
    }
    
    /**
     * (Opcional) Método para o BattlePanel nos chamar quando
     * a batalha acabar e o jogador quiser voltar ao menu.
     */
    public void voltarParaSelecao() {
        // Reseta ambos os painéis
        selectionPanel = new SelectionPanel(this); // Cria um novo painel de seleção
        battlePanel = new BattlePanel(this);       // Cria um novo painel de batalha
        
        cardPanel.add(selectionPanel, "SELECAO");
        cardPanel.add(battlePanel, "BATALHA");
        
        cardLayout.show(cardPanel, "SELECAO");
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
//        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
//        getContentPane().setLayout(layout);
//        layout.setHorizontalGroup(
//            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
//            .addGap(0, 400, Short.MAX_VALUE)
//        );
//        layout.setVerticalGroup(
//            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
//            .addGap(0, 300, Short.MAX_VALUE)
//        );
        pack();
    }
}