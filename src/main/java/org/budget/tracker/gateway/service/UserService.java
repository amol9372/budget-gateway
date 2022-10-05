package org.budget.tracker.gateway.service;

import org.budget.tracker.gateway.rest.controller.request.AuthenticateUserRequest;
import org.budget.tracker.gateway.rest.controller.request.CreateUserRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface UserService {

    ResponseEntity<Object> loginUser(AuthenticateUserRequest request);

    ResponseEntity<Object> createUser(CreateUserRequest request);

}
