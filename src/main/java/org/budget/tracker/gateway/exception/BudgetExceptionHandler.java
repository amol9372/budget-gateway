package org.budget.tracker.gateway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.handler.ResponseStatusExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class BudgetExceptionHandler extends ResponseStatusExceptionHandler {

  @org.springframework.web.bind.annotation.ExceptionHandler(InvalidCredentialsException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<ErrorMessage> invalidCredentialsException(InvalidCredentialsException ex) {
    var errorMessage =
        new ErrorMessage(HttpStatus.BAD_REQUEST.value(), LocalDateTime.now(), ex.getMessage());

    return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
  }
}
