package com.mycompany.arenabatalhaclient;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JPanel;
import javax.swing.Timer; 

/**
 * Um painel que pisca em preto e branco por 3 segundos
 * e depois chama a próxima tela.
 */
public class TransitionPanel extends JPanel implements ActionListener {

    private GameUI parentUI;
    private Timer timer;
    private boolean isWhite = true; // Controla a cor do flash
    private int flashCount = 0; // Contador de flashes
    private final int TOTAL_FLASHES = 20; // 20 flashes * 150ms = 3000ms (3 segundos)

    public TransitionPanel(GameUI parent) {
        this.parentUI = parent;
        
        // O Timer vai "apitar" a cada 150 milissegundos
        this.timer = new Timer(150, this);
    }

    /**
     * Este é o método que o GameUI vai chamar
     * quando a partida for iniciar.
     */
    public void startTransition() {
        this.flashCount = 0;
        this.isWhite = true;
        this.setBackground(Color.WHITE); 
        this.timer.start(); 
    }
    
    /**
     * Este método é chamado pelo Timer a cada 150ms
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (flashCount >= TOTAL_FLASHES) {
            timer.stop();
            parentUI.showBattlePanel(); 
            return;
        }
        
        if (isWhite) {
            this.setBackground(Color.BLACK);
        } else {
            this.setBackground(Color.WHITE);
        }
        
        isWhite = !isWhite;
        flashCount++;
    }
}

