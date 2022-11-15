package org.budget.tracker.gateway.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.internal.LinkedTreeMap;
import org.budget.tracker.gateway.exception.InvalidCredentialsException;
import org.budget.tracker.gateway.rest.controller.request.AuthenticateUserRequest;
import org.budget.tracker.gateway.rest.controller.request.CreateUserRequest;
import org.budget.tracker.gateway.rest.controller.request.RefreshTokenRequest;
import org.budget.tracker.gateway.rest.controller.response.AuthenticateUserResponse;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component
public class FirebaseUtils<
    T extends CreateUserRequest,
    K extends AuthenticateUserRequest,
    M extends AuthenticateUserResponse> {

  private static final String GOOGLE_AUTH_URL =
      "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=";

  private static final String REFRESH_TOKEN_URL =
      "https://securetoken.googleapis.com/v1/token?key=";

  @Value("${firebaseWebKey}")
  private String firebaseWebKey;

  @Autowired RedisUtils redisUtils;

  @Autowired RestTemplate restTemplate;

  public UserRecord createUser(T request) {
    UserRecord.CreateRequest createRequest =
        new UserRecord.CreateRequest()
            .setEmail(request.getEmail())
            .setEmailVerified(true)
            .setPassword(request.getPassword())
            // .setPhoneNumber("+11234567890")
            .setDisplayName(request.getFirstName())
            .setDisabled(false);

    UserRecord userRecord;
    try {
      userRecord = FirebaseAuth.getInstance().createUser(createRequest);
    } catch (FirebaseAuthException e) {
      throw new RuntimeException(e);
    }
    return userRecord;
  }

  public M loginUser(K request) {
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
        new ObjectMapper().convertValue(response.getBody(), AuthenticateUserResponse.class);
    return (M) successResponse;
  }

  public ResponseEntity<Object> refreshToken(Object request) {
    ResponseEntity<Object> response;

    try {
      response =
          restTemplate.postForEntity(REFRESH_TOKEN_URL.concat(firebaseWebKey), request, Object.class);
    } catch (Exception e) {
      throw new InvalidCredentialsException();
    }

    return response;
  }

  private String getJsonStringFromRequest(K request) {
    String json;
    try {
      json = new ObjectMapper().writeValueAsString(request);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    return json;
  }
}
