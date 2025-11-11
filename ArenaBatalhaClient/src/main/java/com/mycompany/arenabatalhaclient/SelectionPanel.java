package com.mycompany.arenabatalhaclient;

import java.awt.Dimension;
import javax.swing.JButton;
import javax.swing.ImageIcon;

public class SelectionPanel extends javax.swing.JPanel {

    // Referência ao "Pai" (o GameUI)
    private GameUI parentUI;
    // Array para facilitar o acesso a todos os botões
    private JButton[] allButtons;

    /**
     * Creates new form SelectionPanel
     */
    public SelectionPanel(GameUI parent) {
        this.parentUI = parent;
        initComponents(); // Este método desenha os componentes do NetBeans
        
        // 1. Agrupa todos os botões em um array para fácil manipulação
        allButtons = new JButton[]{
            btnCharmander, btnVulpix, btnSquirtle, btnStaryu,
            btnTreecko, btnElekid, btnPikachu, btnDiglett,
            btnWooper, btnNumel, btnRowlet, btnEmolga
        };
        
        // 2. Carrega os ícones nos botões
        loadButtonIcons();
    }
    
    /**
     * Carrega os ícones da sua pasta /assets/icons/ nos botões.
     * Os botões devem ter os ícones com os nomes exatos.
     */
    private void loadButtonIcons() {
        // Bloco try-catch para evitar que o programa quebre se um ícone faltar
        try {
            // Linha 1: Fogo e Água
            btnCharmander.setIcon(new ImageIcon(getClass().getResource("/assets/icons/charmander.png")));
            btnVulpix.setIcon(new ImageIcon(getClass().getResource("/assets/icons/vulpix.png")));
            btnSquirtle.setIcon(new ImageIcon(getClass().getResource("/assets/icons/squirtle.png")));
            btnStaryu.setIcon(new ImageIcon(getClass().getResource("/assets/icons/staryu.png")));
            
            // Linha 2: Planta e Elétrico
            btnTreecko.setIcon(new ImageIcon(getClass().getResource("/assets/icons/treecko.png")));
            btnElekid.setIcon(new ImageIcon(getClass().getResource("/assets/icons/elekid.png")));
            btnPikachu.setIcon(new ImageIcon(getClass().getResource("/assets/icons/pikachu.png")));
            btnDiglett.setIcon(new ImageIcon(getClass().getResource("/assets/icons/diglett.png")));
            
            // Linha 3: Duplos
            btnWooper.setIcon(new ImageIcon(getClass().getResource("/assets/icons/wooper.png")));
            btnNumel.setIcon(new ImageIcon(getClass().getResource("/assets/icons/numel.png")));
            btnRowlet.setIcon(new ImageIcon(getClass().getResource("/assets/icons/rowlet.png")));
            btnEmolga.setIcon(new ImageIcon(getClass().getResource("/assets/icons/emolga.png")));
            
            // Remove o texto de todos os botões para mostrar só o ícone
            for (JButton btn : allButtons) {
                btn.setText("");
                btn.setPreferredSize(new Dimension(100, 100)); // Define um tamanho padrão
            }
            
        } catch (Exception e) {
            System.err.println("Erro ao carregar ícones: " + e.getMessage());
            lblLog.setText("Erro ao carregar ícones. Verifique os nomes.");
            // e.printStackTrace(); // Descomente para ver o erro completo
        }
    }
    
    /**
     * Método que o GameUI vai chamar para nos dar feedback do servidor.
     */
    public void atualizarLog(String mensagem) {
        lblLog.setText(mensagem); // 'lblLog' é o nome do seu JLabel
    }
    
    /**
     * O método-chave que é chamado por qualquer botão.
     */
    private void enviarEscolha(String nomePokemon) {
        // 1. Cria a mensagem no formato STRING
        String mensagem = "ESCOLHEU:" + nomePokemon;
        
        // 2. Pede ao "Pai" (GameUI) para enviar
        parentUI.enviarParaServidor(mensagem);
        
        // 3. Desativa os botões
        desativarTodosOsBotoes();
        lblLog.setText("Você escolheu " + nomePokemon + "! Aguardando oponente...");
    }
    
    /**
     * Passa por todos os botões no array e os desativa.
     */
    private void desativarTodosOsBotoes() {
        for (JButton btn : allButtons) {
            if (btn != null) {
                btn.setEnabled(false);
            }
        }
    }

    /**
     * Este código é gerado pelo NetBeans GUI Builder.
     * Você pode copiar/colar isso na sua "Source View" ou
     * recriar a grade 3x4 no "Design View".
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnCharmander = new javax.swing.JButton();
        btnVulpix = new javax.swing.JButton();
        btnSquirtle = new javax.swing.JButton();
        btnStaryu = new javax.swing.JButton();
        btnTreecko = new javax.swing.JButton();
        btnElekid = new javax.swing.JButton();
        btnPikachu = new javax.swing.JButton();
        btnDiglett = new javax.swing.JButton();
        btnWooper = new javax.swing.JButton();
        btnNumel = new javax.swing.JButton();
        btnRowlet = new javax.swing.JButton();
        btnEmolga = new javax.swing.JButton();
        lblLog = new javax.swing.JLabel();

        btnCharmander.setText("Charmander");
        btnCharmander.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCharmanderActionPerformed(evt);
            }
        });

        btnVulpix.setText("Vulpix");
        btnVulpix.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVulpixActionPerformed(evt);
            }
        });

        btnSquirtle.setText("Squirtle");
        btnSquirtle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSquirtleActionPerformed(evt);
            }
        });

        btnStaryu.setText("Staryu");
        btnStaryu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStaryuActionPerformed(evt);
            }
        });

        btnTreecko.setText("Treecko");
        btnTreecko.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTreeckoActionPerformed(evt);
            }
        });

        btnElekid.setText("Elekid");
        btnElekid.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnElekidActionPerformed(evt);
            }
        });

        btnPikachu.setText("Pikachu");
        btnPikachu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPikachuActionPerformed(evt);
            }
        });

        btnDiglett.setText("Diglett");
        btnDiglett.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDiglettActionPerformed(evt);
            }
        });

        btnWooper.setText("Wooper");
        btnWooper.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnWooperActionPerformed(evt);
            }
        });

        btnNumel.setText("Numel");
        btnNumel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNumelActionPerformed(evt);
            }
        });

        btnRowlet.setText("Rowlet");
        btnRowlet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRowletActionPerformed(evt);
            }
        });

        btnEmolga.setText("Emolga");
        btnEmolga.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEmolgaActionPerformed(evt);
            }
        });

        lblLog.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblLog.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblLog.setText("Escolha seu Pokémon...");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnTreecko, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnElekid, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnPikachu, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnDiglett, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnCharmander, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnVulpix, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSquirtle, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnStaryu, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnWooper, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnNumel, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnRowlet, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnEmolga, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(20, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblLog, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCharmander, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnVulpix, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSquirtle, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnStaryu, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnTreecko, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnElekid, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnPikachu, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnDiglett, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnWooper, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnNumel, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnRowlet, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnEmolga, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(lblLog, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(20, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    
    // --- Eventos de Clique ---
    // Cada botão chama o "enviarEscolha" com seu respectivo nome.
    
    private void btnCharmanderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCharmanderActionPerformed
        enviarEscolha("Charmander");
    }//GEN-LAST:event_btnCharmanderActionPerformed

    private void btnVulpixActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVulpixActionPerformed
        enviarEscolha("Vulpix");
    }//GEN-LAST:event_btnVulpixActionPerformed

    private void btnSquirtleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSquirtleActionPerformed
        enviarEscolha("Squirtle");
    }//GEN-LAST:event_btnSquirtleActionPerformed

    private void btnStaryuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStaryuActionPerformed
        enviarEscolha("Staryu");
    }//GEN-LAST:event_btnStaryuActionPerformed

    private void btnTreeckoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTreeckoActionPerformed
        enviarEscolha("Treecko");
    }//GEN-LAST:event_btnTreeckoActionPerformed

    private void btnElekidActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnElekidActionPerformed
        enviarEscolha("Elekid");
    }//GEN-LAST:event_btnElekidActionPerformed

    private void btnPikachuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPikachuActionPerformed
        enviarEscolha("Pikachu");
    }//GEN-LAST:event_btnPikachuActionPerformed

    private void btnDiglettActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDiglettActionPerformed
        enviarEscolha("Diglett");
    }//GEN-LAST:event_btnDiglettActionPerformed

    private void btnWooperActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnWooperActionPerformed
        enviarEscolha("Wooper");
    }//GEN-LAST:event_btnWooperActionPerformed

    private void btnNumelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNumelActionPerformed
        enviarEscolha("Numel");
    }//GEN-LAST:event_btnNumelActionPerformed

    private void btnRowletActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRowletActionPerformed
        enviarEscolha("Rowlet");
    }//GEN-LAST:event_btnRowletActionPerformed

    private void btnEmolgaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEmolgaActionPerformed
        enviarEscolha("Emolga");
    }//GEN-LAST:event_btnEmolgaActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCharmander;
    private javax.swing.JButton btnDiglett;
    private javax.swing.JButton btnElekid;
    private javax.swing.JButton btnEmolga;
    private javax.swing.JButton btnNumel;
    private javax.swing.JButton btnPikachu;
    private javax.swing.JButton btnRowlet;
    private javax.swing.JButton btnSquirtle;
    private javax.swing.JButton btnStaryu;
    private javax.swing.JButton btnTreecko;
    private javax.swing.JButton btnVulpix;
    private javax.swing.JButton btnWooper;
    private javax.swing.JLabel lblLog;
    // End of variables declaration//GEN-END:variables
}