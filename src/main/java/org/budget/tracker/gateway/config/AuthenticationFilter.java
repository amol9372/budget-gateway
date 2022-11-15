package org.budget.tracker.gateway.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import org.budget.tracker.gateway.rest.controller.request.AuthenticateUserRequest;
import org.budget.tracker.gateway.rest.controller.request.CreateUserRequest;
import org.budget.tracker.gateway.rest.controller.response.AuthenticateUserResponse;
import org.budget.tracker.gateway.utils.FirebaseUtils;
import org.budget.tracker.gateway.utils.RedisUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RefreshScope
public class AuthenticationFilter implements GlobalFilter {

  @Autowired private RouterValidator routerValidator;

  @Autowired
  FirebaseUtils<CreateUserRequest, AuthenticateUserRequest, AuthenticateUserResponse> firebaseUtils;

  @Autowired RedisUtils redisUtils;

  private static final Logger log = LoggerFactory.getLogger(AuthenticationFilter.class);

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    ServerHttpRequest request = exchange.getRequest();

    if (routerValidator.isSecured.test(request)) {
      if (this.isAuthMissing(request)) {
        log.info("Auth header is missing");
        return this.onError(exchange, Constants.AUTH_HEADER_MISSING);
      }

      try {
        FirebaseAuth.getInstance().verifyIdToken(getAuthHeader(request));
      } catch (FirebaseAuthException e) {
        var errorCode = e.getAuthErrorCode().toString();
        if (errorCode.equals("INVALID_ARGUMENT")) {
          throw new RuntimeException(e);
        }
        if(errorCode.equals("EXPIRED_ID_TOKEN")){
          return this.onError(exchange, Constants.AUTH_HEADER_INVALID);
          // check for url, if "/refresh-token" -> allow it
          // return chain.filter(exchange);
        }
      }

      this.populateRequestWithHeaders(exchange);
    }
    return chain.filter(exchange);
  }

  private Mono<Void> onError(ServerWebExchange exchange, String err) {
    log.info("User is unauthorised ::: {}", err);
    ServerHttpResponse response = exchange.getResponse();
    response.setStatusCode(HttpStatus.UNAUTHORIZED);
    return response.setComplete();
  }

  private String getAuthHeader(ServerHttpRequest request) {
    return request.getHeaders().getOrEmpty("Authorization").get(0);
  }

  private boolean isAuthMissing(ServerHttpRequest request) {
    return !request.getHeaders().containsKey("Authorization");
  }

  private void populateRequestWithHeaders(ServerWebExchange exchange) {

    exchange.getRequest().mutate().build();
  }
}
