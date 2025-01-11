package src.Server;

import java.io.*;
import java.net.Socket;
import java.util.Objects;

import src.Communication.Message;
import src.Communication.MessageSerializer;



public class ServerWorker implements Runnable {

    private Socket socket;
    private ComandProcessor commandProcessor;



    public ServerWorker(Socket socket, SharedMap sharedMap, UserManager userManager) {
        this.socket = socket;
        this.commandProcessor= new ComandProcessor(sharedMap,userManager);
    }


    @Override
    public void run() {

        DataInputStream in = null;
        DataOutputStream out = null;

        try {
            in = new DataInputStream(socket.getInputStream()); //leitura
            out = new DataOutputStream(socket.getOutputStream()); //escrita
        }
        catch (IOException e) {
            e.printStackTrace();
        }


        String line = "filler";
        String response = "filler";

        while (!socket.isClosed() && !Thread.currentThread().isInterrupted() && line != null) {

            Message receivedMessage = null;
            try {
                receivedMessage = MessageSerializer.deserialize(in);

                if(receivedMessage == null) break;

                line = receivedMessage.getContent();

                if(line != null) {
                    // Processa o comando recebido no socket
                    response = commandProcessor.processCommand(line);

                    Message responseMessage = new Message(receivedMessage.getId(), 9, response);


                    if(Objects.equals(response, "Logout realizado com sucesso")) {

                        MessageSerializer.serialize(out, responseMessage);
                        //closeSocket
                        break;
                    }
                    else if(response != null) {
                        MessageSerializer.serialize(out, responseMessage); // Enviar a resposta para o cliente
                    }
                }
            } catch (IOException e) {
                break;
            }
        }

        closeSocket();
    }

    private void closeSocket() {
        try {
            this.socket.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

}