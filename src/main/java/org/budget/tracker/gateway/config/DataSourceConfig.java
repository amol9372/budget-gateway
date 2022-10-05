package org.budget.tracker.gateway.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.IOException;

@Configuration
public class DataSourceConfig {

    private String redisHost = "localhost";

    private int redisPort = 6379;

    @Bean
    public JedisPool jedisPool() {

        JedisPoolConfig config = new JedisPoolConfig();
        config.setJmxEnabled(false);
        return new JedisPool(config, redisHost, redisPort);
    }

    @Bean
    public FirebaseApp getFirebaseApp() throws IOException {
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.getApplicationDefault())
                .build();

        return FirebaseApp.initializeApp(options);
    }

    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }

}
