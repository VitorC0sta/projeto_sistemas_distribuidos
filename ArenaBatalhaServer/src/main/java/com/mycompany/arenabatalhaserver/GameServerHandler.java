package com.mycompany.arenabatalhaserver; // Verifique se o pacote está correto

import java.io.IOException;

// Este é o "Mensageiro" (baseado no TCPServerAtivosHandler)
// Também não precisa de 'Form'
public class GameServerHandler extends Thread {

    private ServerConnection cliente;
    private GameServerCore main; // O "Juiz"

    public GameServerHandler(ServerConnection cliente, GameServerCore main) throws IOException {
        this.cliente = cliente;
        this.main = main;
    }

    @Override
    protected void finalize() throws Throwable {
        encerrar();
    }

    private void encerrar() {
        this.main.removerCliente(this.cliente);
    }

    @Override
    public void run() {
        String message;
        while (true) {
            try {
                if (this.cliente.getSocket().isConnected() && this.cliente.getInput() != null) {
                    message = this.cliente.getInput().readLine();
                } else {
                    break;
                }
                
                if (message == null || message.equals("")) {
                    break;
                }
                
                // Entrega a ação para o "Juiz" (Main) processar
                this.main.processarAcao(this.cliente, message);
                
            } catch (Exception ex) {
                // System.out.println(ex.getMessage()); // Opcional: logar o erro
                break; // Encerra a thread se o cliente desconectar (ex: fechar a janela)
            }
        }
        encerrar(); // Limpa o cliente quando o loop quebra
    }
}