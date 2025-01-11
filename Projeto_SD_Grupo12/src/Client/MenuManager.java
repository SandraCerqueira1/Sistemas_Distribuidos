package src.Client;
import java.util.Scanner;

/**
 * A classe MenuManager é responsável por gerir a interação entre o user e o sistema,
 * Consoante aquilo que o user escolhe nos menus chama as funções do UserInputProcessor
 */

public class MenuManager {
    private UserInputProcessor inputProcessor;

    public MenuManager(UserInputProcessor inputProcessor) {
        this.inputProcessor = inputProcessor;
    }

    public void start() {
        try (Scanner systemIn = new Scanner(System.in)) {
            boolean login = false;
            String username = "";

            while (true) {
                if (login) {
                    inputProcessor.showClientMenu(username);
                } else {
                    inputProcessor.showStartMenu();
                }

                String userInput = systemIn.nextLine().trim();

                if (!login) {
                    if (userInput.equals("1")) {
                        login = inputProcessor.handleLogin(systemIn);
                        if (login) {
                            username = inputProcessor.getUsername();
                        }
                    } else if (userInput.equals("2")) {
                        inputProcessor.handleRegister(systemIn);
                    } else if (userInput.equals("0")) {
                        System.out.println("Goodbye...");
                        break;
                    } else {
                        System.out.println("Opção inválida. Tente novamente.");
                    }
                } else {
                    // Caso já tenha feito login e o handleUserCommand retorne false, significa logout
                    boolean running = inputProcessor.handleUserCommand(userInput);
                    if (!running) {
                        break; // Sai do loop e o programa volta ao main()
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}