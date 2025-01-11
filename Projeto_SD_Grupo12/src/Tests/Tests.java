package src.Tests;

import src.Communication.MessageType;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Tests {
    public static void main(String[] args) {
        int numberOfClients = 100; // Número de clientes
        String executeTest= "Teste_11";

        List<Thread> threads = new ArrayList<>();

        // Marca o início do teste
        long inicioTeste= System.currentTimeMillis();
        System.out.println("\u001B[32mA iniciar: " + executeTest + "\u001B[0m");
        for (int i = 0; i < numberOfClients; i++) {
            int clientId = i + 1;
            Thread t = new Thread(() -> {
                try (Socket socket = new Socket("localhost", 17345)) {
                    System.out.println("Cliente " + clientId + " conectado.");
                    TestsClients.executeCommand(socket, clientId, executeTest);
                } catch (IOException e) {
                    System.out.println("Erro ao conectar o cliente " + clientId + ": " + e.getMessage());
                }
            });
            threads.add(t);
        }

        // Inicia todas as threads
        for (Thread t : threads) {
            t.start();
        }

        // Aguarda todas as threads terminarem
        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                System.err.println("Thread interrompida: " + e.getMessage());
            }
        }

        // Marca o fim do teste
        long fimTeste = System.currentTimeMillis();

        //calcula a duração total do teste
        long duracaoTotalTeste = fimTeste - inicioTeste;

        // Calcula e imprime a latência média
        double avgLatency = TestsClients.calculateAverageLatency();


        System.out.println("\u001B[32m-----------------------------------------\u001B[0m");
        System.out.println("\u001B[32mTeste \"" + executeTest + "\" finalizado!\u001B[0m");
        System.out.println("\u001B[32mTempo total (ms): " + duracaoTotalTeste + "\u001B[0m");
        System.out.println("\u001B[32mLatência média (ms): " + avgLatency + "\u001B[0m");
        System.out.println("\u001B[32m-----------------------------------------\u001B[0m");
    }
}