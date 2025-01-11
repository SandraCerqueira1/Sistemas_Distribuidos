package src.Tests;

import src.Communication.Message;
import src.Communication.MessageSerializer;
import src.Communication.MessageType;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

public class TestsClients {

    private static final Random random = new Random();
    private static final List<Long> latencias = new ArrayList<>();
    private static final ReentrantLock lock = new ReentrantLock();

    /**
     * Executa comandos específicos.
     */
    public static void executeCommand(Socket socket, int clientId, String executeTest) {
        try (DataOutputStream out = new DataOutputStream(socket.getOutputStream());
             DataInputStream in = new DataInputStream(socket.getInputStream())) {

            switch (executeTest) {
                case "Teste_1":
                    testPut(out, in, clientId);
                    break;
                case "Teste_2":
                    testGet(out, in, clientId);
                    break;
                case "Teste_3":
                    testMultiPut(out, in, clientId);
                    break;
                case "Teste_4":
                    testMultiGet(out, in, clientId);
                    break;
                case "Teste_5":
                    testPutAndGet(out, in, clientId);
                    break;
                case "Teste_6":
                    testMultiPutAndMultiGet(out, in, clientId);
                    break;
                case "Teste_7":
                    testMix(out, in, clientId);
                    break;
                case "Teste_8":
                    testAcessoConcurrente(out, in, clientId);
                    break;
                case "Teste_9":
                    testReconnect(socket, clientId);
                    break;
                case "Teste_10":
                    testGetWhen(out, in, clientId);
                    break;
                case "Teste_11":
                    testAdvancedOperations(out, in, clientId);
                    break;
                case "Teste_12":
                    testAtomicyForMultiput(out, in, clientId);
                    break;
                case "Teste_13":
                    testAtomicityForMultiGet(out, in, clientId);
                    break;
                default:
                    System.out.println("Comando não suportado.");
            }

            sendLogout(out, in, clientId);

        } catch (IOException e) {
        }
    }


    //Teste 1 - Só puts
    private static void testPut(DataOutputStream out, DataInputStream in, int clientId) throws IOException {
        String key = "" + random.nextInt(1000);
        String value = "" + random.nextInt(1000);
        String content = "put " + key + " " + value;

        sendAndMeasureLatency(out, in, clientId, MessageType.PUT, content);
    }

    //Teste 2- Só gets
    private static void testGet(DataOutputStream out, DataInputStream in, int clientId) throws IOException {
        String key = "" + random.nextInt(1000);
        String content = "get " + key;

        sendAndMeasureLatency(out, in, clientId, MessageType.GET, content);
    }

    //Teste 3- Só multiput
    private static void testMultiPut(DataOutputStream out, DataInputStream in, int clientId) throws IOException {
        StringBuilder content = new StringBuilder("multiput ");

        for (int i = 0; i < 3; i++) {
            String key = "" + random.nextInt(1000);
            String value = "" + random.nextInt(1000);
            content.append(key).append(" ").append(value).append(" ");
        }

        sendAndMeasureLatency(out, in, clientId, MessageType.MULTIPUT, content.toString().trim());
    }

    //Teste 4- Só multiget
    private static void testMultiGet(DataOutputStream out, DataInputStream in, int clientId) throws IOException {
        StringBuilder content = new StringBuilder("multiget ");

        for (int i = 0; i < 3; i++) {
            String key = "" + random.nextInt(1000);
            content.append(key).append(" ");
        }

        sendAndMeasureLatency(out, in, clientId, MessageType.MULTIGET, content.toString().trim());
    }

    //Teste 5- Mete um put e da logo get dessa keu para ver se foi bem sucedido
    public static void testPutAndGet(DataOutputStream out, DataInputStream in, int clientId) throws IOException {
        String key = "" + random.nextInt(1000);
        String value = "" + random.nextInt(1000);

        String putCommand = "put " + key + " " + value;
        String getCommand = "get " + key;

        sendAndMeasureLatency(out, in, clientId, MessageType.PUT, putCommand);
        sendAndMeasureLatency(out, in, clientId, MessageType.GET, getCommand);
    }

    //Teste 6- Mete um multiput e da logo multiget dessas keys para ver se foi bem sucedido
    public static void testMultiPutAndMultiGet(DataOutputStream out, DataInputStream in, int clientId) throws IOException {
        StringBuilder multiPutCommand = new StringBuilder("multiput ");
        StringBuilder multiGetCommand = new StringBuilder("multiget ");

        for (int i = 0; i < 3; i++) {
            String key = "" + random.nextInt(1000);
            String value = "" + random.nextInt(1000);
            multiPutCommand.append(key).append(" ").append(value).append(" ");
            multiGetCommand.append(key).append(" ");
        }

        sendAndMeasureLatency(out, in, clientId, MessageType.MULTIPUT, multiPutCommand.toString().trim());
        sendAndMeasureLatency(out, in, clientId, MessageType.MULTIGET, multiGetCommand.toString().trim());
    }

    //Teste 7- Executar sequencias de comandos misturadas
    public static void testMix(DataOutputStream out, DataInputStream in, int clientId) throws IOException {
        testPutAndGet(out, in, clientId);
        testMultiPutAndMultiGet(out, in, clientId);
    }

    //Teste 8- Operações concorrentes de put na mesma key a ver o que acontece
    public static void testAcessoConcurrente(DataOutputStream out, DataInputStream in, int clientId) throws IOException {
        String key = "SistemasDistribuidos"; // Todos os clientes usarão a mesma chave
        String value = "" + random.nextInt(1000);

        // PUT
        String putCommand = "put " + key + " " + value;
        sendAndMeasureLatency(out, in, clientId, MessageType.PUT, putCommand);

        // GET
        String getCommand = "get " + key;
        sendAndMeasureLatency(out, in, clientId, MessageType.GET, getCommand);
    }

    //Teste 9 - Mostrar que se o client desconecatar depois de um put e se reconectar e fizer get do que deu put a info é mantida
    public static void testReconnect(Socket socket, int clientId) {
        try (DataOutputStream out = new DataOutputStream(socket.getOutputStream());
             DataInputStream in = new DataInputStream(socket.getInputStream())) {

            // Envia um comando PUT
            String key = "" + random.nextInt(1000);
            String value = "" + random.nextInt(1000);
            String putCommand = "put " + key + " " + value;
            sendAndMeasureLatency(out, in, clientId, MessageType.PUT, putCommand);

            // Simula desconexão
            socket.close();
            System.out.println("Cliente " + clientId + " desconectado.");

            // Reconecta
            Socket newSocket = new Socket("localhost", 17345);
            try (DataOutputStream newOut = new DataOutputStream(newSocket.getOutputStream());
                 DataInputStream newIn = new DataInputStream(newSocket.getInputStream())) {

                System.out.println("Cliente " + clientId + " reconectado.");

                // Envia o comando GET para verificar persistência dos dados
                String getCommand = "get " + key;
                sendAndMeasureLatency(newOut, newIn, clientId, MessageType.GET, getCommand);

                // Envia logout no contexto da nova conexão
                sendLogout(newOut, newIn, clientId);
            }
        } catch (IOException e) {
            System.err.println("Erro durante o teste de timeout/reconexão: " + e.getMessage());
        }
    }

    // Teste 10 - Teste do GetWhen
    private static void testGetWhen(DataOutputStream out, DataInputStream in, int clientId) throws IOException {
        // O cliente 1 faz o get when
        // O put a seguir ao getwhen é para mostrar que ele espera que a condicao seja atendida antes de continuar
        if(clientId % 3 == 0){
            String key = "20";
            String keyCond = "60";
            String valueCond = "30";

            String getWhenCommand = "getwhen " + key + " " + keyCond + " " + valueCond;

            Message message = new Message(clientId, MessageType.GETWHEN, getWhenCommand);
            MessageSerializer.serialize(out, message);
            out.flush();
            Message response = MessageSerializer.deserialize(in);
            System.out.println("Cliente " + clientId + " executou comando: " + getWhenCommand);
            if (response != null) {
                System.out.println("Cliente " + clientId + " Resposta: " + response);
            } else {
                System.out.println("Cliente " + clientId + " Não recebeu resposta");
            }

            String key2 = "40";
            String value2 = "41";

            String putCommand = "put " + key2 + " " + value2;

            sendAndMeasureLatency(out, in, clientId, MessageType.PUT, putCommand);
        }
        // Esta condicao garante que caso os put aleatorios nao sejam suficientes para validar a condicao ela seja correspondida fazendo o put especifico dela
        else if(clientId == 50){
            String key1 = "20";
            String value1 = "10";

            String putCommand1 = "put " + key1 + " " + value1;

            sendAndMeasureLatency(out, in, clientId, MessageType.PUT, putCommand1);

            String key2 = "60";
            String value2 = "30";

            String putCommand2 = "put " + key2 + " " + value2;

            sendAndMeasureLatency(out, in, clientId, MessageType.PUT, putCommand2);
        }
        // Puts aleatorios para tentar acertar com a condicao
        else {
            String key = "" + random.nextInt(1, 100);
            String value = "" + random.nextInt(1, 100);

            String putCommand = "put " + key + " " + value;

            sendAndMeasureLatency(out, in, clientId, MessageType.PUT, putCommand);
        }
    }

    //Teste 11
    private static void testAdvancedOperations(DataOutputStream out, DataInputStream in, int clientId) throws IOException {
        int iterations = 5; // Número de operações que cada cliente executará
        Random rand = new Random();

        for (int i = 0; i < iterations; i++) {
            int operation = rand.nextInt(4); // 0 = getWhen, 1 = put, 2 = get, 3 = multiPut

            switch (operation) {
                case 0: // getWhen
                    String key = "" + clientId + "" + i;
                    String keyCond = "" + clientId + "" + i;
                    String valueCond = "" + i;

                    String getWhenCommand = "getwhen " + key + " " + keyCond + " " + valueCond;

                    Message getWhenMessage = new Message(clientId, MessageType.GETWHEN, getWhenCommand);
                    MessageSerializer.serialize(out, getWhenMessage);
                    out.flush();

                    Message response = MessageSerializer.deserialize(in);
                    System.out.println("Cliente " + clientId + " executou comando: " + getWhenCommand);

                    if (response != null) {
                        System.out.println("Cliente " + clientId + " Resposta: " + response);
                    } else {
                        System.out.println("Cliente " + clientId + " Não recebeu resposta");
                    }
                    break;

                case 1: //put
                    String putKey = "" + rand.nextInt(10);
                    String putValue = "" + rand.nextInt(5);

                    String putCommand = "put " + putKey + " " + putValue;

                    sendAndMeasureLatency(out, in, clientId, MessageType.PUT, putCommand);
                    break;

                case 2: //get
                    String getKey = "" + rand.nextInt(10);

                    String getCommand = "get " + getKey;

                    sendAndMeasureLatency(out, in, clientId, MessageType.GET, getCommand);
                    break;

                case 3: //multiPut
                    StringBuilder multiPutCommand = new StringBuilder("multiput ");
                    for (int j = 0; j < 3; j++) {
                        String multiKey = "" + rand.nextInt(10);
                        String multiValue = "" + rand.nextInt(10);
                        multiPutCommand.append(multiKey).append(" ").append(multiValue).append(" ");
                    }

                    sendAndMeasureLatency(out, in, clientId, MessageType.MULTIPUT, multiPutCommand.toString().trim());
                    break;

                default:
                    System.out.println("Operação inválida gerada pelo randomizador.");
            }
        }
    }

    // Teste 12 - Verificar Atomicidade em Multiput

    private static void testAtomicyForMultiput(DataOutputStream out, DataInputStream in, int clientId) throws IOException {
        String key1 = "1";
        String value = "2";
        String key2 = "4";
        String content = "multiput " + key1 + " " + value + " "+ key2;

        sendAndMeasureLatency(out, in, clientId, MessageType.MULTIPUT, content);

        String content2 = "get " + key1;

        sendAndMeasureLatency(out, in, clientId, MessageType.GET, content2);

    }
    // Teste 13 - Verificar Atomicidade em Multiget
    private static void testAtomicityForMultiGet(DataOutputStream out, DataInputStream in, int clientId) throws IOException {
        String key1 = "1";
        String value1 = "2";
        String key2 = "3";
        String value2 = "4";
        String key3 = "5";
        String content = "multiput " + key1 + " " + value1 + " " + key2 + " " + value2;

        sendAndMeasureLatency(out, in, clientId, MessageType.MULTIPUT, content);


        String content2 = "multiget " + key1 + " " + key2 + " " + key3;


        sendAndMeasureLatency(out, in, clientId, MessageType.MULTIGET, content2);

    }

    private static void sendLogout(DataOutputStream out, DataInputStream in, int clientId) throws IOException {
        String content = "logout";

        long start = System.currentTimeMillis();
        Message message = new Message(clientId, MessageType.LOGOUT, content);
        MessageSerializer.serialize(out, message);
        out.flush();
        MessageSerializer.deserialize(in);
        long end = System.currentTimeMillis();

        long latency = end - start;
        lock.lock();
        try {
            latencias.add(latency);
        } finally {
            lock.unlock();
        }

        System.out.println("Cliente " + clientId + " realizou logout.");
    }




    private static void sendAndMeasureLatency(DataOutputStream out, DataInputStream in, int clientId, int type, String content) throws IOException {
        long inicio = System.currentTimeMillis();

        Message message = new Message(clientId, type, content);
        MessageSerializer.serialize(out, message);
        out.flush();
        Message response = MessageSerializer.deserialize(in);

        long fim = System.currentTimeMillis();
        long latencia = fim - inicio;

        lock.lock();
        try {
            latencias.add(latencia);
        } finally {
            lock.unlock();
        }

        System.out.println("Cliente " + clientId + " executou comando: " + content + " | Latência: " + latencia + "ms");
        if (response != null) {
            System.out.println("Cliente " + clientId + " Resposta: " + response);
        } else {
            System.out.println("Cliente " + clientId + " Não recebeu resposta");
        }
    }

    public static double calculateAverageLatency() {
        lock.lock();
        List<Long> copy;
        try {
            copy = new ArrayList<>(latencias);
        } finally {
            lock.unlock();
        }

        if (copy.isEmpty()) {
            return 0.0;
        }

        long sum = 0;
        for (long l : copy) {
            sum += l;
        }

        return (double) sum / copy.size();
    }


}