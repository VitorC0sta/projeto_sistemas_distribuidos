/*
 * Ponto de entrada principal da aplicação Cliente.
 */
package com.mycompany.arenabatalhaclient;

/**
 * @author vitor
 */
public class ArenaBatalhaClient {

    public static void main(String[] args) {
        // Isso agenda a criação da nossa janela (GameUI)
        // na thread correta de interface gráfica do Java.
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new GameUI().setVisible(true);
            }
        });
    }
}