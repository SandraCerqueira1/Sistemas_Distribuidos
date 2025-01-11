package src.Server;

import src.Client.User;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;


/**
 * Classe responsável por tratar do registo e autenticação de utilizadores (clientes)
 * */
public class UserManager {

    // Mapa de todos os clientes registados
    private Map<String, User> mapa;
    private ReentrantLock lock = new ReentrantLock();

    public UserManager() {
        this.mapa = new HashMap<String, User>();
    }

    public void showUsers() {
        lock.lock();
        try{
            System.out.println(mapa.keySet());
        }finally {
            lock.unlock();
        }
    }

    public void register(String nome, String password) {
        lock.lock();
        try{
            if(!this.mapa.containsKey(nome)){
                User user = new User(nome, password);
                this.mapa.put(nome, user);
            }
            else{
                System.out.println("O utilizador já existe.");
            }
        }finally {
            lock.unlock();
        }
    }

    public boolean authenticate(String nome, String password) {
        User user = this.mapa.get(nome);
        String passwordUser = user.getPassword();

        if(user != null) {
            if(!user.getLogged()) {
                if (passwordUser.equals(password)) {
                    user.setLogged(true);
                    this.mapa.put(nome, user);
                    return true;
                }
            }
        }
        else {
            return false;
        }

        return false;
    }

}

