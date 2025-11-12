package com.mycompany.arenabatalhaserver;

import java.io.IOException;

/**
 *
 * @author vitor
 */
public class ArenaBatalhaServer {

    public static void main(String[] args) {
        int porta = 6789; 
        
        System.out.println("Iniciando o Lançador do Servidor...");
        
        try {
            // 1. servidor na porta 6789
            GameServerCore serverCore = new GameServerCore(porta);
            
            //2. Adiciona um Shutdown Hook para fechar o servidor corretamente
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                serverCore.stopServer();
            }));
            
            // 3. Inicia a thread do servidor
            serverCore.start();
            
        } catch (IOException e) {
            System.err.println("Erro fatal ao iniciar o servidor na porta " + porta + "| " + e.getMessage());
            e.printStackTrace();
        }
    }
}