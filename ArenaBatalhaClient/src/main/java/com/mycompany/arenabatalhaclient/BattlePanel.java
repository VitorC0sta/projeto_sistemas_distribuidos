package com.mycompany.arenabatalhaclient;

/**
 * Este é um "esqueleto" do BattlePanel, apenas para o GameUI compilar.
 * Mais tarde, vamos adicionar JLabels, GIFs e botões aqui.
 */
public class BattlePanel extends javax.swing.JPanel {

    private GameUI parentUI;

    public BattlePanel(GameUI parent) {
        this.parentUI = parent;
        initComponents();
        
        // (Opcional) Adiciona um label só para sabermos que é a tela certa
        lblTitulo.setText("TELA DE BATALHA - (Em construção)");
    }

    // --- Métodos que o GameUI vai chamar ---
    // (Por enquanto, eles só imprimem no console)
    
    public void iniciarBatalha(String dadosPartida) {
        System.out.println("BattlePanel: Recebi dados da partida: " + dadosPartida);
        // Ex: INICIAR_PARTIDA:Treecko:false:Charmander:true:80:3
        String[] partes = dadosPartida.split(":");
        String seuPoke = partes[0];
        String oponentePoke = partes[2];
        lblTitulo.setText(seuPoke + " vs. " + oponentePoke);
    }
    
    public void definirTurno(boolean meuTurno) {
        System.out.println("BattlePanel: Meu turno? " + meuTurno);
    }
    
    public void atualizarHpJogador(int hp) {
        System.out.println("BattlePanel: HP Jogador: " + hp);
    }
    
    public void atualizarHpOponente(int hp) {
        System.out.println("BattlePanel: HP Oponente: " + hp);
    }
    
    public void atualizarCuras(int curas) {
        System.out.println("BattlePanel: Curas restantes: " + curas);
    }
    
    public void mostrarFimDeJogo(String vencedor) {
        System.out.println("BattlePanel: Fim de Jogo! Vencedor: " + vencedor);
    }
    
    public void adicionarLog(String msg) {
        System.out.println("BattlePanel LOG: " + msg);
    }

    
    /**
     * This method is called from within the constructor to initialize the form.
     * (Código gerado pelo NetBeans)
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblTitulo = new javax.swing.JLabel();

        lblTitulo.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblTitulo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTitulo.setText("jLabel1");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblTitulo, javax.swing.GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblTitulo)
                .addContainerGap(262, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblTitulo;
    // End of variables declaration//GEN-END:variables
}