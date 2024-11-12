package com.hprocoder.controller;

import com.hprocoder.exception.TechnicalException;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestControllerAdvisor extends ResponseEntityExceptionHandler {

  private static final String TIMESTAMP = "timestamp";
  private static final String MESSAGE = "message";

  @ExceptionHandler({TechnicalException.class})
  public ResponseEntity<Object> handleTechnicalException(Exception ex){

    Map<String, Object> body = new LinkedHashMap<>();
    body.put(TIMESTAMP, Instant.now());
    body.put(MESSAGE, ex.getMessage());
    return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
