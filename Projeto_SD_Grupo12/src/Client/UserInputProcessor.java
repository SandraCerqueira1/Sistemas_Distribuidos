package src.Client;

import src.Communication.Message;
import src.Communication.MessageType;

import java.io.IOException;
import java.util.Scanner;

/**
 * Classe usada pelo MenuManager que tem os métodos para mostrar os menus e processar os inputs do client
 * está integrada com o sistema de serialização de mensagens (Message e MessageSerializer).
 */
public class UserInputProcessor {
    private ClientCommunicator clientCommunicator; // Objeto para comunicar com o servidor
    private UserInterface ui;                    //  para mostrar menus
    private String username = "";
    private int messageId;                      // Identificador único da mensagem, que aumenta a cada envio

    public UserInputProcessor(ClientCommunicator clientCommunicator, UserInterface ui) {
        this.clientCommunicator = clientCommunicator;
        this.ui = ui;
        this.messageId = 0; // Inicializa o contador de ID
    }

    // Mostra o menu de login e registo
    public void showStartMenu() {
        System.out.println(ui.startMenu());
    }

    // Mostra o menu do cliente que já se autenticou
    public void showClientMenu(String username) {
        System.out.println(ui.clientMenu(username));
    }

    /**
     * Processa o login do utilizador.
     * @param systemIn Scanner para leitura de input.
     * @return true se o login for bem-sucedido.
     */
    public boolean handleLogin(Scanner systemIn) throws IOException {
        System.out.print("Username: ");
        String loginUsername = systemIn.nextLine();
        System.out.print("Password: ");
        String password = systemIn.nextLine();

        // Cria uma nova Message com o comando LOGIN, tipo de mensagem e o id único
        Message loginMessage = new Message(messageId++, MessageType.LOGIN, "LOGIN " + loginUsername + " " + password);

        // Envia o comando de login
        clientCommunicator.sendMessage(loginMessage);

        // Recebe a resposta
        Message responseMessage = clientCommunicator.receiveMessage();
        System.out.println(responseMessage.getContent());

        // Se o login for bem-sucedido, armazena o nome de utilizador
        if (responseMessage.getContent().equals("Login bem sucedido")) {
            this.username = loginUsername;
            return true;
        }
        return false;
    }

    /**
     * Regista um novo utilizador no sistema.
     * @param systemIn Scanner para leitura de input.
     */
    public void handleRegister(Scanner systemIn) throws IOException {
        System.out.print("Username: ");
        String registerUsername = systemIn.nextLine();
        System.out.print("Password: ");
        String password = systemIn.nextLine();

        // Cria uma nova Message com o comando REGISTER, tipo de mensagem e o id único
        Message registerMessage = new Message(messageId++, MessageType.REGISTER, "REGISTER " + registerUsername + " " + password);

        // Envia comando de registo usando
        clientCommunicator.sendMessage(registerMessage);

        // Recebe a resposta
        Message responseMessage = clientCommunicator.receiveMessage();
        System.out.println(responseMessage.getContent());
    }

    /**
     * Processa os comandos do utilizador.
     * @param userInput Comando introduzido pelo utilizador.
     */
    // No UserInputProcessor
    public boolean handleUserCommand(String userInput) throws IOException {
        int messageType = getMessageTypeFromCommand(userInput);
        Message userCommandMessage = new Message(messageId++, messageType, userInput);
        clientCommunicator.sendMessage(userCommandMessage);

        //desserializa
        Message responseMessage = clientCommunicator.receiveMessage();
        System.out.print("\033[92m");
        System.out.println("Resposta do servidor: " + responseMessage.getContent());
        System.out.print("\033[0m");

        if (responseMessage.getContent().equals("Logout realizado com sucesso")) {
            System.out.println("Cliente encerrado!");
            clientCommunicator.close();
            return false;
        }

        return true; // Continua em execução
    }


    /**
     * Determina o tipo de mensagem com base no comando do user
     * @param userInput Comando fornecido pelo user
     * @return O tipo de mensagem correspondente ao comando.
     */
    private int getMessageTypeFromCommand(String userInput) {
        if (userInput.toUpperCase().startsWith("PUT")) {
            return MessageType.PUT;
        } else if (userInput.toUpperCase().startsWith("GET")) {
            return MessageType.GET;
        } else if (userInput.toUpperCase().startsWith("MULTIPUT")) {
            return MessageType.MULTIPUT;
        } else if (userInput.toUpperCase().startsWith("MULTIGET")) {
            return MessageType.MULTIGET;
        } else if (userInput.toUpperCase().startsWith("GETWHEN")) {
            return MessageType.GETWHEN;
        } else if (userInput.toUpperCase().equals("LOGOUT")) {
            return MessageType.LOGOUT;
        } else {
            return MessageType.ERROR; // Caso o comando não seja reconhecido, retorna o tipo ERROR
        }
    }

    public String getUsername() {
        return this.username;
    }
}
