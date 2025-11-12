package com.mycompany.arenabatalhaclient;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * ESSENCIAL: O painel (JPanel) que aparece no fim do jogo.
 * Esta classe é responsável por mostrar o resultado (quem venceu)
 * e fornecer o botão "Novo Jogo", que chama o 'voltarParaSelecao'
 * no GameUI para reiniciar o ciclo do jogo.
 */
public class GameOverPanel extends JPanel {

    private GameUI parentUI;

    private JLabel lblBackground;
    private JLabel lblMessage; // Label para a mensagem de vitória/derrota
    private JButton btnNovoJogo;

    public GameOverPanel(GameUI parent) {
        this.parentUI = parent;

        // --- Configurações Básicas ---
        this.setLayout(null); // Layout nulo para posicionar componentes manualmente
        this.setPreferredSize(new Dimension(800, 600)); 
        this.setBackground(Color.BLACK); 

        // 1. Background
        criarBackground();

        // 2. Mensagem Central
        lblMessage = new JLabel("FIM DE JOGO");
        lblMessage.setFont(new Font("Segoe UI", Font.BOLD, 48));
        lblMessage.setForeground(Color.WHITE);
        lblMessage.setHorizontalAlignment(SwingConstants.CENTER);
        lblMessage.setBounds(0, 150, 800, 50); 
        this.add(lblMessage);

        // 3. Botão de Novo Jogo
        btnNovoJogo = new JButton("Novo Jogo");
        btnNovoJogo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        btnNovoJogo.setBounds(275, 450, 250, 60); 
        
        /**
         * ESSENCIAL: Evento de clique do botão "Novo Jogo".
         * Esta é a ação que diz ao controlador principal (GameUI)
         * para fechar a conexão, limpar todos os painéis (com resetState)
         * e voltar para a tela de seleção (SELECAO).
         */
        btnNovoJogo.addActionListener(e -> {
            parentUI.voltarParaSelecao(); // Chama o método central de reset no GameUI
        });
        this.add(btnNovoJogo);
        
        // Inicia o painel com os componentes escondidos.
        // Eles só aparecerão quando 'mostrarVencedor' for chamado.
        resetState();
    }
    
    private void criarBackground() {
        lblBackground = new JLabel();
        try {
            java.net.URL imgUrl = getClass().getResource("/assets/backgrounds/end_battle_bg.jpg"); 

            if (imgUrl != null) {
                ImageIcon originalIcon = new ImageIcon(imgUrl);
                Image image = originalIcon.getImage().getScaledInstance(
                        800, 600, java.awt.Image.SCALE_SMOOTH);
                lblBackground.setIcon(new ImageIcon(image));
            } else {
                 lblBackground.setOpaque(true);
                 lblBackground.setBackground(Color.BLACK);
            }
        } catch (Exception e) {
             lblBackground.setOpaque(true);
             lblBackground.setBackground(Color.BLACK);
        }

        lblBackground.setBounds(0, 0, 800, 600);
        this.add(lblBackground, 0); // Adiciona na posição 0 (fundo)
    }

    /**
     * ESSENCIAL: Chamado pelo GameUI quando a msg FIM_DE_JOGO é recebida.
     * Ele atualiza a UI com o vencedor e, crucialmente,
     * TORNA OS COMPONENTES VISÍVEIS (o botão e a mensagem).
     */
    public void mostrarVencedor(String vencedorNome, boolean isShiny, String jogadorID) {
        // Atualiza a mensagem para ser o nome do jogador e o status
        lblMessage.setText(jogadorID); 
        
        // Torna os componentes visíveis para esta partida
        lblMessage.setVisible(true);
        btnNovoJogo.setVisible(true);
    }
    
    // =========================================================================
    // FUNÇÃO CRÍTICA DA CORREÇÃO (PARA O RELATÓRIO)
    // =========================================================================
    
    /**
     * ESSENCIAL (NOVO): Reseta o painel de fim de jogo.
     * Esta função é chamada pelo GameUI (no 'voltarParaSelecao')
     * para ESCONDER os componentes (botão, texto) deste painel.
     */
    public void resetState() {
        lblMessage.setVisible(false);
        btnNovoJogo.setVisible(false);
    }
}