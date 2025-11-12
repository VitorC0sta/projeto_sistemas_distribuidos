package com.mycompany.arenabatalhaclient; 

import com.mycompany.arenabatalhaclient.TCPClientHandler;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class TCPClientMain {

    // 4. Note a mudança aqui para GameUI
    public TCPClientMain(String serverAddress, int serverPort, GameUI caller) throws UnknownHostException, IOException {
        this.socket = new Socket(serverAddress, serverPort);
        handler = new TCPClientHandler(socket, caller); // Passa o 'caller'
        this.handler.start();
        this.output = new PrintWriter(this.socket.getOutputStream(), true);
    }

    // Este é o método que vamos usar para enviar "ESCOLHEU|Pikachu"
    public void writeMessage(String outMessage) {
        this.output.println(outMessage);
    }

    public void closeConnection() throws IOException {
        this.output.close();
        this.socket.close();
        this.handler.interrupt();
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            this.closeConnection();
        } finally {
            super.finalize();
        }
    }

    private TCPClientHandler handler;
    private Socket socket;
    private PrintWriter output;
}