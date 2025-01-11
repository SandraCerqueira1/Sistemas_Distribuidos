package src.Client;
import java.net.Socket;


/**
 * Estabelece a conexão com o servidor e chama as classes que permitem ao user
 * efetuar o login, o registo e o envio comandos para o server.
 *
 */

public class Client {

    public static void main(String[] args) {
        try {
            //Abrir socket
            Socket socket = new Socket("localhost", 17345);

            ClientCommunicator serverConnection = new ClientCommunicator(socket);


            UserInterface ui = new UserInterface();
            UserInputProcessor inputProcessor = new UserInputProcessor(serverConnection, ui);

            // Inicializa o menu manager
            MenuManager menuManager = new MenuManager(inputProcessor);

            // Executa o processo de login e de exibição de menus
            menuManager.start();
            serverConnection.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
