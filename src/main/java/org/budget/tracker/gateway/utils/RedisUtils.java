package org.budget.tracker.gateway.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisException;

import java.util.List;
import java.util.Map;

@Component
public class RedisUtils {

  @Autowired JedisPool jedisPool;

  private static final Logger log = LoggerFactory.getLogger(RedisUtils.class);

  public List<String> getValue(String key, String field) {
    try (Jedis jedis = jedisPool.getResource()) {
      return jedis.hmget(key, field);
    } catch (JedisException e) {
      log.error("Something wrong with Redis ::: {}", e.getMessage());
    }
    return null;
  }

  public String getValue(String key) {
    try (Jedis jedis = jedisPool.getResource()) {
      return jedis.get(key);
    } catch (JedisException e) {
      log.error("Something wrong with Redis ::: {}", e.getMessage());
    }
    return null;
  }

  public void hmSet(String key, Map<String, String> valueMap) {
    try (Jedis jedis = jedisPool.getResource()) {
      jedis.hmset(key, valueMap);
      // jedis.expire(request.getEmail(), TimeUnit.DAYS.toSeconds(4));
    } catch (JedisException e) {
      e.printStackTrace();
      log.error("Something wrong with Redis ::: {}", e.getMessage());
    }
  }

  public boolean isClosed(){
      return jedisPool.isClosed();
  }
}
