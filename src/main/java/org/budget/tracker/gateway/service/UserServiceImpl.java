package org.budget.tracker.gateway.service;

import com.google.firebase.auth.UserRecord;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.budget.tracker.gateway.builder.UserBuilder;
import org.budget.tracker.gateway.db.JUser;
import org.budget.tracker.gateway.exception.UserNotFound;
import org.budget.tracker.gateway.repository.UsersJpaRepository;
import org.budget.tracker.gateway.rest.controller.request.AuthenticateUserRequest;
import org.budget.tracker.gateway.rest.controller.request.CreateUserRequest;
import org.budget.tracker.gateway.rest.controller.request.RefreshTokenRequest;
import org.budget.tracker.gateway.rest.controller.response.AuthenticateUserResponse;
import org.budget.tracker.gateway.utils.FirebaseUtils;
import org.budget.tracker.gateway.utils.RedisUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

  @Autowired RedisUtils redisUtils;

  @Autowired RestTemplate restTemplate;

  @Autowired UsersJpaRepository usersJpaRepository;

  @Autowired
  FirebaseUtils<CreateUserRequest, AuthenticateUserRequest, AuthenticateUserResponse> firebaseUtils;

  private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

  @Override
  public ResponseEntity<Object> loginUser(AuthenticateUserRequest request) {

    var response = firebaseUtils.loginUser(request);

    JUser juser = usersJpaRepository.findByEmail(request.getEmail()).orElseThrow(UserNotFound::new);
    response.setUserId(juser.getId());

    if (!redisUtils.isClosed()) {
      // successful login
      // save logged-in-user in Redis with sessionId

      redisUtils.hmSet(
          request.getEmail(),
          Map.of(
              "userId",
              juser.getId().toString(),
              "name",
              response.getDisplayName(),
              "firebaseId",
              response.getLocalId(),
              "refresh_token",
              response.getRefreshToken()));

      log.info("Successfully saved user in redis ::: {}", request.getEmail());
    }

    return ResponseEntity.status(200).body(response);
  }

  @Override
  public ResponseEntity<Object> createUser(CreateUserRequest request) {

    UserRecord userRecord = firebaseUtils.createUser(request);
    log.info("Successfully created new user ::: {}", userRecord.getUid());

    // Create user in DB via budget App
    JUser jUser = UserBuilder.with(request, userRecord);
    usersJpaRepository.save(jUser);
    log.info("Created user in DB ::: {}", userRecord.getUid());

    return ResponseEntity.status(201).body(userRecord);
  }

  @Override
  public ResponseEntity<Object> refreshToken(RefreshTokenRequest request) {

    var userRedis = redisUtils.getValue(request.getEmail(), "refresh_token");

    JsonObject entity = new JsonObject();
    entity.add("grant_type", new JsonPrimitive("refresh_token"));
    entity.add("refresh_token", new JsonPrimitive(userRedis.get(0)));

    Object refreshTokenRequest = new Gson().fromJson(entity, Object.class);

    return firebaseUtils.refreshToken(refreshTokenRequest);
  }
}
