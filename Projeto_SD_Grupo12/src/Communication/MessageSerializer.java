package src.Communication;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;

/**
 * Classe responsável por serializar e desserializar objetos Message.
 */
public class MessageSerializer {

    /**
     * Serializa e envia uma mensagem pelo DataOutputStream.
     *
     * @param out O stream de saída de dados.
     * @param message A mensagem a ser enviada.
     */
    public static void serialize(DataOutputStream out, Message message) throws IOException {
        // Escreve o ID da mensagem
        out.writeInt(message.getId());

        // Escreve o TYPE da mensagem
        out.writeInt(message.getType());

        // Serializa o conteúdo
        byte[] contentBytes = message.getContent().getBytes("UTF-8");

        // Envia o comprimento do conteúdo
        out.writeInt(contentBytes.length);

        // Envia o conteúdo
        out.write(contentBytes);


        out.flush();
    }

    /**
     * Recebe e desserializa uma mensagem do DataInputStream
     *
     * @param in O stream de entrada de dados.
     * @return Uma instância de Message.
     */
    public static Message deserialize(DataInputStream in) throws IOException {

        try {
            if(Thread.currentThread().isInterrupted()) {
                return null;
            }

            // Lê o ID da mensagem
            int id = in.readInt();

            // Lê o TYPE da mensagem
            int type = in.readInt();

            // Lê o comprimento do conteúdo
            int contentLength = in.readInt();

            if (contentLength <= 0) {
                throw new IOException("Invalid content length: " + contentLength);
            }

            // Lê o conteúdo
            byte[] contentBytes = new byte[contentLength];
            in.readFully(contentBytes);

            // Converte o conteúdo para string
            String content = new String(contentBytes, "UTF-8");

            // Retorna a mensagem desserializada
            return new Message(id, type, content);
        }
        catch (EOFException e) {
            // Caso o socket seja encerrado, retorna null
            return null;
        }

    }
}