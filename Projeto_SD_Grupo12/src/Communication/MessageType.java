package src.Communication;

/**
 * Classe que define os tipos de mensagens para o protocolo de comunicação.
 * Estes identificadores serão usados para distinguir o tipo de comando enviado ou recebido
 */
public final class MessageType {
    public final static int REGISTER = 1;
    public final static int LOGIN = 2;
    public final static int PUT = 3;
    public final static int GET = 4;
    public final static int MULTIPUT = 5;
    public final static int MULTIGET = 6;
    public final static int GETWHEN = 7;
    public final static int LOGOUT = 8;
    public final static int RESPONSE = 9;
    public final static int ERROR = 99;

    // Construtor privado para evitar instanciar a classe
    private MessageType() {}
}
