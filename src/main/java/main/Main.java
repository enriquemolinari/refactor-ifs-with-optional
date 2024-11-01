package main;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import model.Author;
import model.Post;
import service.Posts;

import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence
                .createEntityManagerFactory("jpa-derby-embedded");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            var autor1 = new Author("Enrique", "Molinari");
            em.persist(new Post("titulo 1", "texto del post 1", autor1, LocalDateTime.now().minusDays(1)));
            em.persist(new Post("titulo 2", "texto del post 2", autor1, LocalDateTime.now().minusDays(2)));
            em.persist(new Post("titulo 3", "texto del post 3", autor1, LocalDateTime.now().minusDays(3)));
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            throw new RuntimeException(e);
        } finally {
            if (em != null && em.isOpen())
                em.close();
        }

        var posts1 = new Posts(emf).latestPosts(1L);
        posts1.stream().forEach(p -> System.out.println(p.title()));

        var posts2 = new Posts(emf).latestPosts(1L);
        posts2.stream().forEach(p -> System.out.println(p.title()));
        
        emf.close();
    }
}