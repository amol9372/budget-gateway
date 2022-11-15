package org.budget.tracker.gateway.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.IOException;

@Configuration
public class DataSourceConfig {

    @Value("${redisHost}")
    private String redisHost;

    @Value("${redisPort}")
    private int redisPort;

    @Value("${redisUser}")
    private String redisUser;

    @Value("${redisPassword}")
    private String redisPassword;

    @Bean
    public JedisPool jedisPool() {

        JedisPoolConfig config = new JedisPoolConfig();
        config.setJmxEnabled(false);
        return new JedisPool(config, redisHost, redisPort, 4000, redisUser , redisPassword);
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
