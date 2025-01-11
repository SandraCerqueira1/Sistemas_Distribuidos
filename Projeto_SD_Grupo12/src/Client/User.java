package src.Client;

import java.util.concurrent.locks.ReentrantLock;

public class User {

    private String nome;
    private String  password;
    private boolean logged;
    private ReentrantLock lock = new ReentrantLock();

    public User(String nome, String password) {
        this.nome = nome;
        this.password = password;
    }

    public String getNome() {
        lock.lock();
        try {
            return this.nome;
        }
        finally {
            lock.unlock();
        }
    }

    public void setNome(String nome) {
        lock.lock();
        try {
            this.nome = nome;
        }
        finally {
            lock.unlock();
        }
    }

    public String getPassword() {
        lock.lock();
        try {
            return this.password;
        }
        finally {
            lock.unlock();
        }
    }

    public void setPassword(String password) {
        lock.lock();
        try {
            this.password = password;
        }
        finally {
            lock.unlock();
        }
    }

    public boolean getLogged() {
        lock.lock();
        try {
            return this.logged;
        }
        finally {
            lock.unlock();
        }
    }

    public void setLogged(Boolean logged) {
        lock.lock();
        try {
            this.logged = logged;
        }
        finally {
            lock.unlock();
        }
    }

}
