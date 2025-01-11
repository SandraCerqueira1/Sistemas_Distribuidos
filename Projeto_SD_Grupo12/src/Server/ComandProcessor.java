package src.Server;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A classe responsável por processar e executar os pedidos recebidos no servidor
 * Cada comando é interpretado, e a ação correspondente é realizada, interagindo com o SharedMap e o UserManager.
 */
public class ComandProcessor {

    private SharedMap sharedMap;
    private UserManager userManager;

    /**
     * Construtor
     */
    public ComandProcessor(SharedMap sharedMap, UserManager userManager) {
        this.sharedMap = sharedMap;
        this.userManager = userManager;
    }

    /**
     * Método principal que processa o comando recebido. Divide o comando em partes e chama o método
     * correspondente para cada tipo de comando (ex. REGISTER, LOGIN, PUT, etc).
     *
     * @param comand O comando recebido do cliente.
     * @return A resposta ao comando processado.
     */
    public String processCommand(String comand) {
        String[] commandParts = comand.split(" "); // Array que contém o comando separado em partes
        String action = commandParts[0].toUpperCase(); // Operação: PUT, GET, MULTIPUT, MULTIGET, GETWHEN
        String response = "";

        // Verificar se o comando está correto
        if (!action.equals("LOGOUT") && commandParts.length < 2) {
            return "Erro: Parâmetros insuficientes para o comando " + action;
        }

        try {
            // Verificar a ação que deve ser executada com base no comando recebido
            if (action.equals("REGISTER")) {
                response = handleRegister(commandParts);
            } else if (action.equals("LOGIN")) {
                response = handleLogin(commandParts);
            } else if (action.equals("PUT")) {
                response = handlePut(commandParts);
            } else if (action.equals("GET")) {
                response = handleGet(commandParts);
            } else if (action.equals("MULTIPUT")) {
                response = handleMultiPut(commandParts);
            } else if (action.equals("MULTIGET")) {
                response = handleMultiGet(commandParts);
            } else if (action.equals("GETWHEN")) {
                response = handleGetWhen(commandParts);
            } else if (action.equals("LOGOUT")) {
                response = handleLogout();
            } else {
                response = "Erro: Comando desconhecido";
            }
        } catch (Exception e) {
            response = "Erro ao processar o comando: " + e.getMessage();
        }

        return response;
    }

    private String handleRegister(String[] commandParts) {
        if (commandParts.length != 3) {
            return "Erro: O comando REGISTER necessita de 2 parâmetros (username e password)";
        }

        String username = commandParts[1];
        String password = commandParts[2];

        try {
            userManager.register(username, password);
            return "Utilizador registado com sucesso!";
        } catch (Exception e) {
            return "Erro ao registar utilizador: " + e.getMessage();
        }
    }

    private String handleLogin(String[] commandParts) {
        if (commandParts.length != 3) {
            return "Erro: O comando LOGIN necessita de 2 parâmetros (username e password)";
        }

        String username = commandParts[1];
        String password = commandParts[2];

        try {
            if (userManager.authenticate(username, password)) {
                return "Login bem sucedido";
            } else {
                return "Login falhou (dados inválidos)";
            }
        } catch (Exception e) {
            return "Erro ao autenticar utilizador: " + e.getMessage();
        }
    }

    private String handlePut(String[] commandParts) {
        if (commandParts.length != 3) {
            return "Erro: O comando PUT necessita de 2 parâmetros (key e value)";
        }

        String key = commandParts[1];
        byte[] value = commandParts[2].getBytes(); // Converte o valor para bytes

        try {
            sharedMap.put(key, value);
            return "PUT realizado com sucesso";
        } catch (Exception e) {
            return "Erro ao realizar o PUT: " + e.getMessage();
        }
    }

    private String handleGet(String[] commandParts) {
        if (commandParts.length != 2) {
            return "Erro: O comando GET necessita de 1 parâmetro (key)";
        }

        String key = commandParts[1];
        try {
            byte[] result = sharedMap.get(key);
            if (result != null) {
                return "O value da key " + key + " é: " + new String(result);
            } else {
                return "Chave não encontrada para a key: " + key;
            }
        } catch (Exception e) {
            return "Erro ao realizar o GET: " + e.getMessage();
        }
    }

    private String handleMultiPut(String[] commandParts) {
        if (commandParts.length < 3 || commandParts.length % 2 != 1) {
            return "Erro: O comando MULTIPUT necessita de um número par de parâmetros (key1 value1 key2 value2 ...)";
        }

        Map<String, byte[]> pairs = new HashMap<>();
        for (int i = 1; i < commandParts.length; i += 2) {
            String key = commandParts[i];
            byte[] value = commandParts[i + 1].getBytes();
            pairs.put(key, value);
        }

        try {
            sharedMap.multiPut(pairs);
            return "MULTIPUT realizado com sucesso";
        } catch (Exception e) {
            return "Erro ao realizar o MULTIPUT: " + e.getMessage();
        }
    }

    private String handleMultiGet(String[] commandParts) {
        if (commandParts.length < 2) {
            return "Erro: O comando MULTIGET necessita de pelo menos 1 parâmetro (key1 key2 ...)";
        }

        Set<String> keys = new HashSet<>();
        for (int i = 1; i < commandParts.length; i++) {
            keys.add(commandParts[i]);
        }

        try {
            Map<String, byte[]> results = sharedMap.multiGet(keys);
            if (results != null && !results.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                sb.append("Os resultados obtidos foram os seguintes:");
                for (Map.Entry<String, byte[]> entry : results.entrySet()) {
                    sb.append(" [key: ").append(entry.getKey()).append(", value: ").append(new String(entry.getValue())).append("]");
                }
                return sb.toString();
            } else {
                return "Uma ou mais chaves não encontradas!";
            }
        } catch (Exception e) {
            return "Erro ao realizar o MULTIGET: " + e.getMessage();
        }
    }

    private String handleGetWhen(String[] commandParts) {
        if (commandParts.length != 4) {
            return "Erro: O comando GETWHEN necessita de 3 parâmetros (key keyCond valueCond)";
        }

        String key = commandParts[1];
        String keyCond = commandParts[2];
        byte[] valueCond = commandParts[3].getBytes();

        try {
            byte[] result = sharedMap.getWhen(key, keyCond, valueCond);
            return "O resultado do GETWHEN é: " + new String(result);
        } catch (Exception e) {
            return "Erro ao realizar o GETWHEN: " + e.getMessage();
        }
    }

    private String handleLogout() {
        return "Logout realizado com sucesso";
    }
}
