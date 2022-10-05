package org.budget.tracker.gateway.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import org.budget.tracker.gateway.builder.UserBuilder;
import org.budget.tracker.gateway.db.JUser;
import org.budget.tracker.gateway.exception.InvalidCredentialsException;
import org.budget.tracker.gateway.exception.UserNotFound;
import org.budget.tracker.gateway.repository.UsersJpaRepository;
import org.budget.tracker.gateway.rest.controller.request.AuthenticateUserRequest;
import org.budget.tracker.gateway.rest.controller.request.CreateUserRequest;
import org.budget.tracker.gateway.rest.controller.response.AuthenticateUserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisException;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {

  private static final String GOOGLE_AUTH_URL =
      "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=";

  @Value("${firebaseWebKey}")
  private String firebaseWebKey;

  @Autowired JedisPool jedisPool;

  @Autowired RestTemplate restTemplate;

  @Autowired UsersJpaRepository usersJpaRepository;

  @Override
  public ResponseEntity<Object> loginUser(AuthenticateUserRequest request) {

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    String json = getJsonStringFromRequest(request);
    HttpEntity<String> entity = new HttpEntity<>(json, headers);

    ResponseEntity<Object> response;
    try {
      response =
          restTemplate.postForEntity(GOOGLE_AUTH_URL.concat(firebaseWebKey), entity, Object.class);
    } catch (Exception e) {
      throw new InvalidCredentialsException();
    }

    AuthenticateUserResponse successResponse =
        successResponse =
            new ObjectMapper().convertValue(response.getBody(), AuthenticateUserResponse.class);

    JUser juser = usersJpaRepository.findByEmail(request.getEmail()).orElseThrow(UserNotFound::new);
    successResponse.setUserId(juser.getId());

    if (response.getStatusCode().is2xxSuccessful() && !jedisPool.isClosed()) {
      // successful login
      // save logged-in-user in Redis with sessionId
      try (Jedis jedis = jedisPool.getResource()) {
        jedis.hmset(
            request.getEmail(),
            Map.of(
                "userId",
                juser.getId().toString(),
                "name",
                successResponse.getDisplayName(),
                "firebaseId",
                successResponse.getLocalId()));
        jedis.expire(request.getEmail(), TimeUnit.DAYS.toSeconds(4));
      } catch (JedisException e) {
        System.out.println("Jedis is closed ::: " + e.getMessage());
      }
    }

    return ResponseEntity.status(201).body(successResponse);
  }

  @Override
  public ResponseEntity<Object> createUser(CreateUserRequest request) {

    // Create user in firebase
    UserRecord.CreateRequest createRequest =
        new UserRecord.CreateRequest()
            .setEmail(request.getEmail())
            .setEmailVerified(true)
            .setPassword(request.getPassword())
            // .setPhoneNumber("+11234567890")
            .setDisplayName(request.getFirstName())
            .setDisabled(false);

    UserRecord userRecord = null;
    try {
      userRecord = FirebaseAuth.getInstance().createUser(createRequest);
    } catch (FirebaseAuthException e) {
      throw new RuntimeException(e);
    }
    System.out.println("Successfully created new user: " + userRecord.getUid());

    // Create user in DB via budget App
    JUser jUser = UserBuilder.with(request, userRecord);
    usersJpaRepository.save(jUser);

    return ResponseEntity.status(201).body(userRecord);
  }

  private String getJsonStringFromRequest(AuthenticateUserRequest request) {
    String json;
    try {
      json = new ObjectMapper().writeValueAsString(request);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    return json;
  }
}
