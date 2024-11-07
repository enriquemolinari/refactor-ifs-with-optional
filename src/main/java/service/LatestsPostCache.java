package service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import model.LocalDateTimeTypeAdapter;
import redis.clients.jedis.Jedis;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LatestsPostCache {
    private Gson gson;
    private String cacheHost;
    private int cachePort;
    public LatestsPostCache(String cacheHost, int cachePort) {
        this.cacheHost = cacheHost;
        this.cachePort = cachePort;
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter());
        gson = gsonBuilder.create();
    }

    public void add(String authorId, List<LatestsPost> latestsPosts) {
        try (Jedis jedis = new Jedis(this.cacheHost, this.cachePort)) {
            jedis.set(authorId, this.gson.toJson(latestsPosts));
        }
    }

    public Optional<List<LatestsPost>> get(String authorId) {
        try (Jedis jedis = new Jedis(this.cacheHost, this.cachePort)) {
            var valueFromCache = jedis.get(authorId);
            if (valueFromCache != null) {
                Type listType = new TypeToken<ArrayList<LatestsPost>>() {
                }.getType();
                List<LatestsPost> latestsPosts = gson.fromJson(valueFromCache, listType);
                return Optional.of(latestsPosts);
            }
            return Optional.empty();
        }
    }
}
