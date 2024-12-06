package socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    
    private static final int PORT = 2525;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Servidor SMTP iniciado na porta " + PORT);

            // Loop para aceitar múltiplas conexões de clientes
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Cliente conectado: " + clientSocket.getInetAddress());
                
                // Cria uma nova thread para atender o cliente
                new Thread().start();
            }
        } catch (IOException e) {
            System.out.println("Erro ao iniciar o servidor: " + e.getMessage());
        }
    }
}
