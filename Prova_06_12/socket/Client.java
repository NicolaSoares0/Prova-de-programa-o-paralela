package socket;

import java.io.*;
import java.net.*;
import java.util.regex.*;

class ClientHandler implements Runnable {

    private static final String SERVER_NAME = "SimpleSMTPServer";
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try {
            // Inicializa as streams para leitura e escrita
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);

            // Envia a mensagem de boas-vindas
            out.println("220 " + SERVER_NAME + " Simple Mail Transfer Service Ready");

            String clientCommand;
            String senderEmail = null;
            String recipientEmail = null;
            // Loop para processar os comandos do cliente
            while ((clientCommand = in.readLine()) != null) {
                System.out.println("Comando recebido: " + clientCommand);

                // Comando HELO
                if (clientCommand.startsWith("HELO")) {
                    out.println("250 Hello " + clientCommand.substring(5) + ", pleased to meet you");

                // Comando MAIL FROM
                } else if (clientCommand.startsWith("MAIL FROM:")) {
                    senderEmail = clientCommand.substring(10).trim();
                    if (isValidEmail(senderEmail)) {
                        out.println("250 Sender <" + senderEmail + "> OK");
                    } else {
                        out.println("500 Syntax error, command unrecognized");
                    }

                // Comando RCPT TO
                } else if (clientCommand.startsWith("RCPT TO:")) {
                    recipientEmail = clientCommand.substring(9).trim();
                    if (isValidEmail(recipientEmail)) {
                        out.println("250 Recipient <" + recipientEmail + "> OK");
                    } else {
                        out.println("500 Syntax error, command unrecognized");
                    }

                // Comando DATA
                } else if (clientCommand.equals("DATA")) {
                    out.println("354 End data with <CR><LF>.<CR><LF>");
                    String dataLine;
                    StringBuilder emailData = new StringBuilder();

                    // Lê o corpo do e-mail
                    while (!(dataLine = in.readLine()).equals(".")) {
                        emailData.append(dataLine).append("\n");
                    }
                    out.println("250 Message accepted for delivery");

                // Comando QUIT
                } else if (clientCommand.equals("QUIT")) {
                    out.println("221 " + SERVER_NAME + " Service closing transmission channel");
                    break;

                // Comandos não reconhecidos
                } else {
                    out.println("500 Syntax error, command unrecognized");
                }
            }

        } catch (IOException e) {
            System.out.println("Erro de I/O: " + e.getMessage());
        } finally {
            try {
                // Fecha o socket ao final da comunicação
                clientSocket.close();
            } catch (IOException e) {
                System.out.println("Erro ao fechar o socket: " + e.getMessage());
            }
        }
    }

    // Função de validação simples para o formato de e-mail
    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        Pattern pattern = Pattern.compile(emailRegex);
        MatchResult matcher = pattern.matcher(email);
        return ((Matcher) matcher).matches();
    }
}

    
