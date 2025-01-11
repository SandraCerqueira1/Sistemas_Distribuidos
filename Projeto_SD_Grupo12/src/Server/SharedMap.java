package src.Server;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantReadWriteLock;


/**
 * A classe SharedMap representa um mapa partilhado, com os seus métodos de inserção,
 * obtenção e manipulação de key-values de forma concorrente usando read/write locks
 */

public class SharedMap {
    private Map<String, byte[]> sharedMap;
    private ReentrantReadWriteLock readWriteLock;
    private final Map<String, Condition> conditionMap; //condição para esperar quando necessário



    /**
     * Construtor da classe SharedMap.
     */
    public SharedMap() {
        this.sharedMap = new HashMap<String, byte[]>();
        this.readWriteLock = new ReentrantReadWriteLock();
        this.conditionMap = new HashMap<>();
    }


    /**
     * Método que mostra o conteúdo atual do map partilhado.
     */
    public void showContent() {
        readWriteLock.readLock().lock();
        try {
            sharedMap.forEach((key, value) -> System.out.println(key + ": " + Arrays.toString(value)));
        }finally {readWriteLock.readLock().unlock();}
    }

    /**
     * Método que insere um novo par chave-valor no mapa.
     * O uso do writeLock() impede que qualquer thread aceda enquanto a escrita é feita
     * @param key   Chave associada ao valor
     * @param value Valor a ser associado à chave
     */
    public void put(String key, byte[] value) {
        try {
            readWriteLock.writeLock().lock();
            sharedMap.put(key, value);

            if (conditionMap.containsKey(key)) {
                Condition specificCondition = conditionMap.get(key);
                specificCondition.signalAll();
            }
        }
        finally {
            readWriteLock.writeLock().unlock();
        }
    }


    /**
     * Método para obter o valor associado a uma key
     * O uso do readLock() permite que várias threads façam read simultaneamente
     * (como o valor não é alterado com o get, não causa problemas ler simultaneamente)
     *
     * @param key Chave do valor a ser obtido
     * @return Valor associado à chave ou null se a chave não existir
     */
    public byte[] get(String key) {
        try {
            readWriteLock.readLock().lock();
            return this.sharedMap.get(key);
        }
        finally {
            readWriteLock.readLock().unlock();
        }
    }

    /**
     * Método que insere múltiplos pares key-value no mapa.
     * Utilizamos write lock para impedir que outras threads acedam ao
     * mapa durante a escrita dos pares.
     *
     * @param pairs Mapa que contém os pares key-value que vão ser inseridos
     */
    public void multiPut(Map<String, byte[]> pairs) {
        try {
            readWriteLock.writeLock().lock();
            for (Map.Entry<String, byte[]> entry : pairs.entrySet()) {
                this.sharedMap.put(entry.getKey(), entry.getValue());

                // Notifica apenas as threads esperando pela condição específica
                if (conditionMap.containsKey(entry.getKey())) {
                    Condition specificCondition = conditionMap.get(entry.getKey());
                    specificCondition.signalAll();
                }
            }

        }
        finally {
            readWriteLock.writeLock().unlock();
        }
    }

    /**
     * Método que obtém múltiplos values associados a um conjunto de keys.
     * Utilizamos um read lock para permitir o acesso simultâneo ao conteúdo do mapa.
     *
     * @param keys Conjunto de keys para as quais se pretende obter os valores
     * @return Mapa que contém os pares key-value correspondentes às keys fornecidas
     */
    public Map<String, byte[]> multiGet(Set<String> keys) {
        Map<String, byte[]> mapaGets = new HashMap<>();

        try {
            readWriteLock.readLock().lock();
            for (String key : keys) {
                byte[] value = this.sharedMap.get(key);
                if (value == null) {
                    // Se qualquer chave não for encontrada, retorna um mapa vazio
                    return new HashMap<>();
                }
                mapaGets.put(key, value);
            }
            return mapaGets;
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    /**
     * Obtém o valor associado a uma key específica quando uma dada condição é satisfeita.
     * Utilizamos um  write lock e a condição associada para aguardar até que
     * o valor associado a keyCond corresponda a valueCond.
     *
     * @param key     Chave do valor a ser obtido
     * @param keyCond Chave da condição
     * @param valueCond Valor condicional que keyCond deve ter para liberar o acesso
     * @return Valor associado a key quando a condição é satisfeita
     */

    public byte[] getWhen(String key, String keyCond, byte[] valueCond) throws InterruptedException {
        readWriteLock.writeLock().lock();

        try {
            // Verifica se já existe uma condição para a keyCond, caso contrário cria uma
            Condition specificCondition = conditionMap.get(keyCond);
            if (specificCondition == null) {
                specificCondition = readWriteLock.writeLock().newCondition();
                conditionMap.put(keyCond, specificCondition);
            }

            long startTime = System.currentTimeMillis();
            long tempoDecorrido = 0;
            long timeout = 5000; // 5 segundos para teste

            // Aguarda enquanto a condição não é satisfeita ou até o timeout
            while (!Arrays.equals(sharedMap.get(keyCond), valueCond)) {
                if (tempoDecorrido >= timeout) {
                    // Timeout atingido
                    throw new InterruptedException("Timeout atingido no GETWHEN para a chave condicional: " + keyCond);
                }

                // Aguarda o restante do tempo disponível
                specificCondition.await(timeout - tempoDecorrido, java.util.concurrent.TimeUnit.MILLISECONDS);

                // Calcula o tempo decorrido
                tempoDecorrido = System.currentTimeMillis() - startTime;
            }

            // Remove a condição do mapa após a condição ser satisfeita
            conditionMap.remove(keyCond);

            return sharedMap.get(key);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

}