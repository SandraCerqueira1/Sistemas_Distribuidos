package src.Client;

import src.Communication.Message;
import src.Communication.MessageSerializer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * A classe ClientCommunicator facilita a comunicação entre o client e o server.
 * usa Message e MessageSerializer para enviar e receber as mensagens no formato binário.
 */
public class ClientCommunicator {

    private DataInputStream in;   // DataInputStream para entrada de dados binários
    private DataOutputStream out; // DataOutputStream para saída de dados binários
    private Socket socket;

    public ClientCommunicator(Socket socket) {
        this.socket = socket;
        try {
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Envia uma mensagem serializada para o servidor.
     *
     * @param message A mensagem a ser enviada.
     */
    public void sendMessage(Message message) {
        try {
            MessageSerializer.serialize(out, message); // Serializa e envia a mensagem
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Recebe e desserializa uma mensagem do servidor
     *
     * @return A mensagem recebida.
     */
    public Message receiveMessage() {
        try {
            return MessageSerializer.deserialize(in); // Desserializa a mensagem recebida
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Fecha o socket
     */
    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
