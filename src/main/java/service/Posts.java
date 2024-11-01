package service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import jakarta.persistence.EntityManagerFactory;
import model.LocalDateTimeTypeAdapter;
import model.Post;
import redis.clients.jedis.Jedis;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// sudo docker run -d --name some-redis -p 6379:6379 redis
// sudo docker rm some-redis

public class Posts {

    public static final int REDIS_PORT = 6379;
    public static final String REDIS_HOST = "localhost";
    private final EntityManagerFactory emf;

    public Posts(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public List<LatestsPost> latestPosts(Long authorId) {
        return latestPostsFromCache(authorId).map(jsonString -> {
            Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<LatestsPost>>() {
            }.getType();
            List<LatestsPost> latestsPosts = gson.fromJson(jsonString, listType);
            return latestsPosts;
        }).orElseGet(() -> {
            return latestPostFromDb(authorId);
        });
    }

    private Optional<String> latestPostsFromCache(Long authorId) {
        Optional<String> valueFromCache;
        try (Jedis jedis = new Jedis(REDIS_HOST, REDIS_PORT)) {
            valueFromCache = Optional.ofNullable(jedis.get(authorId.toString())); //Feo: devuelve null si no esta... me obliga la IF
        }
        return valueFromCache;
    }

    private List<LatestsPost> latestPostFromDb(Long authorId) {
        return new Tx(emf).inTx(em -> {
            var query = em.createQuery("from Post order by fechaPublicacion desc", Post.class).setMaxResults(2);
            List<Post> resultList = query.getResultList();
            var lastestsPosts = resultList.stream().map(p -> p.toLatest()).toList();
            Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter()).create();
            String json = gson.toJson(lastestsPosts);
            try (Jedis jedis = new Jedis(REDIS_HOST, REDIS_PORT)) {
                jedis.set(authorId.toString(), json);
            }
            return lastestsPosts;
        });
    }
}
