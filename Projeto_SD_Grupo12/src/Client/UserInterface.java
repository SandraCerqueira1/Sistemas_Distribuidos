package src.Client;

/**
 * Classe com todos os menus para o cliente interagir
 */
public class UserInterface {

    // Menu inicial (antes de fazer login)
    public String startMenu() {
        return
                "\n" +
                        "########################################\n" +
                        "#            Welcome to DataBase       #\n" +
                        "########################################\n" +
                        "\n" +
                        "1 - Log in\n" +
                        "2 - Create Account\n" +
                        "0 - Exit\n" +
                        "\n" +
                        "Choose an option (1, 2, or 0): ";
    }

    // Menu após login, onde o cliente pode escrever o comando diretamente
    public String clientMenu(String username) {
        return
                "\n" +
                        "###################################################################\n" +
                        "#                      Welcome " + username + "                   #\n" +
                        "###################################################################\n" +
                        "\n" +
                        "Available Services:\n" +
                        "------------------------------------------------------------------\n" +
                        "PUT <key> <value>                        (e.g., PUT 1 2)\n" +
                        "GET <key>                                (e.g., GET 1)\n" +
                        "MULTIPUT <key1> <value1> <key2> <value2> (e.g., MULTIPUT 1 2 3 4)\n" +
                        "MULTIGET <key1> <key2>                   (e.g., MULTIGET 1 2)\n" +
                        "GETWHEN <key> <keyCond> <valueCond>      (e.g., GETWHEN 1 2 3)\n" +
                        "LOGOUT\n" +
                        "\n" +
                        "------------------------------------------------------------------\n" +
                        "Enter your command:";
    }
}
