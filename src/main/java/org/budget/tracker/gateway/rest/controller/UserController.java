package org.budget.tracker.gateway.rest.controller;

import org.budget.tracker.gateway.interceptors.ValidateHeader;
import org.budget.tracker.gateway.rest.controller.request.AuthenticateUserRequest;
import org.budget.tracker.gateway.rest.controller.request.CreateUserRequest;
import org.budget.tracker.gateway.rest.controller.request.RefreshTokenRequest;
import org.budget.tracker.gateway.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.netty.http.server.HttpServerRequest;

@RestController
// @CrossOrigin(origins = "https://budget-tracker-4de96.web.app", allowedHeaders = "*", allowCredentials = "true")
public class UserController {

  @Autowired UserService userService;

  @PostMapping("login")
  public ResponseEntity<Object> login(@RequestBody AuthenticateUserRequest request) {
    return userService.loginUser(request);
  }

  @PostMapping("refresh-token")
  @ValidateHeader
  public ResponseEntity<Object> refreshToken(@RequestBody RefreshTokenRequest request, @RequestHeader(HttpHeaders.AUTHORIZATION) String header){
    return userService.refreshToken(request);
  }

  @PostMapping("create-user")
  ResponseEntity<Object> createUser(@RequestBody CreateUserRequest request) {
    return userService.createUser(request);
  }

  @GetMapping("health-check")
  public String healthCheck() {
    return "Gateway is running !!!";
  }
}
