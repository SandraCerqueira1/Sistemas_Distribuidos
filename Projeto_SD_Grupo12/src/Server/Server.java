package src.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Server {

    private int S;
    private SharedMap sharedMap; //classe com a nossa HashMap e os seus métodos
    private UserManager userManager;

    private Queue<Socket> waitingQueue; //fila de espera
    private int activeClients; // número de cliente atualmente ligados ao servidor

    private ReentrantLock lock;
    private Condition newSlotAvailable;


    public Server(int S) {
        this.S = S;
        this.sharedMap = new SharedMap();
        this.userManager = new UserManager();
        this.waitingQueue = new LinkedList<>();
        this.activeClients = 0;
        this.lock = new ReentrantLock();
        this.newSlotAvailable = lock.newCondition();
    }


    public static void main(String[] args) {

        int S = 10;
        Server server = new Server(S);

        try(ServerSocket ss = new ServerSocket(17345)) {

            while (true) {
                Socket socket = ss.accept();

                server.lock.lock();
                try{
                    if(server.activeClients < S){
                        //pode aceitar o cliente
                        server.startClient(socket);
                    }else{

                        //adiciona o cliente à fila de espera
                        server.waitingQueue.add(socket);
                        System.out.println("Cliente adicionado à fila de espera. Total na fila: " + server.waitingQueue.size());
                    }
                }finally {
                    server.lock.unlock();
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startClient(Socket socket) {
        activeClients++;
        System.out.println("Novo cliente conectado. Clientes ativos: " + activeClients);

        Thread worker = new Thread(() -> {
            try{
                new ServerWorker(socket, this.sharedMap, this.userManager).run();
            }finally {
                onClientDisconnected(socket);
            }
        });
        worker.start();
    }

    public void onClientDisconnected(Socket socket) {
        lock.lock();
        try {

            activeClients--;
            System.out.println("Cliente desconectado. Clientes ativos: " + activeClients);

            // Notificar uma thread que tá na fila
            if (!waitingQueue.isEmpty()) {
                Socket nextClient = waitingQueue.poll();
                System.out.println("Cliente da fila de espera agora está conectado.");
                startClient(nextClient);
            } else {
                // Notifica quaisquer threads que tão a espera
                newSlotAvailable.signal();
            }
        } finally {
            lock.unlock();
        }
    }

}