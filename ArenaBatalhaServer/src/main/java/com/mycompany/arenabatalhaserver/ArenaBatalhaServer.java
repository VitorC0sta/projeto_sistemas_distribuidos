package com.mycompany.arenabatalhaserver;

import java.io.IOException;

/**
 *
 * @author vitor
 */
public class ArenaBatalhaServer {

    public static void main(String[] args) {
        // A porta padrão que o seu professor usou
        int porta = 6789; 
        
        System.out.println("Iniciando o Lançador do Servidor...");
        
        try {
            // 1. Cria o "Juiz" (o cérebro do servidor) na porta 6789
            GameServerCore serverCore = new GameServerCore(porta);
            
            // 2. Inicia a thread do servidor (ele começará a ouvir por conexões)
            serverCore.start();
            
        } catch (IOException e) {
            System.err.println("Erro fatal ao iniciar o servidor na porta " + porta + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}