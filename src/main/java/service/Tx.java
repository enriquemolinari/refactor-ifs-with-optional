package service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.function.Consumer;
import java.util.function.Function;

public class Tx {
    private EntityManagerFactory emf;

    public Tx(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public <T> T inTx(Function<EntityManager, T> toExecute) {
        return executeInTransaction(em -> toExecute.apply(em));
    }

    public void inTx(Consumer<EntityManager> toExecute) {
        executeInTransaction(em -> {
            toExecute.accept(em);
            // just to conform the compiler
            return null;
        });
    }

    private <T> T executeInTransaction(Function<EntityManager, T> toExecute) {
        var em = emf.createEntityManager();
        var tx = em.getTransaction();

        try {
            tx.begin();

            T t = toExecute.apply(em);
            tx.commit();

            return t;
        } catch (Exception e) {
            tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}
