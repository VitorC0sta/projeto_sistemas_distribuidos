package com.mycompany.arenabatalhaclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;


public class TCPClientHandler extends Thread {

    private Socket socket;
    private GameUI caller;
    private BufferedReader input;

    // 4. Note a mudança aqui
    public TCPClientHandler(Socket socket, GameUI caller) throws IOException {
        this.socket = socket;
        this.caller = caller;
        this.input = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
    }

    @Override
    public void run() {
        String message;
        while (true) {
            try {
                if (this.socket.isConnected() && this.input != null) {
                    message = this.input.readLine();
                } else {
                    break;
                }
                if (message == null || message.equals("")) {
                    break;
                }
                
                // O "caller" (GameUI) vai ter este método
                this.caller.escreverMensagem(message); 
                
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
                break;
            }
        }
    }
}