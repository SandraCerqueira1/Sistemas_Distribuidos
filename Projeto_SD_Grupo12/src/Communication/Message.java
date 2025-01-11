package src.Communication;

/**
 * Representa a estrutura de uma mensagem que será serializada e enviada pelo socket.
 */
public class Message {
    private int id;          // Identificador da mensagem
    private int type;        // Tipo da mensagem (ex: LOGIN = 1, REGISTER = 2, etc.)
    private String content;  // O conteúdo da mensagem (ex: comando, resposta, etc.)

    /**
     * Construtor da classe Message.
     *
     * @param id O identificador da mensagem.
     * @param type O tipo da mensagem.
     * @param content O conteúdo da mensagem.
     */
    public Message(int id, int type, String content) {
        this.id = id;
        this.type = type;
        this.content = content;
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Message{id=" + id + ", type=" + type + ", content='" + content + "'}";
    }
}
