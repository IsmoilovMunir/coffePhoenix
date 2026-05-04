package ru.feniks.franchise.config;

import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorBody> handleValidation(MethodArgumentNotValidException ex) {
    String message =
        ex.getBindingResult().getFieldErrors().stream()
            .map(e -> e.getField() + ": " + e.getDefaultMessage())
            .collect(Collectors.joining("; "));
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorBody(message));
  }

  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<ErrorBody> handleResponseStatus(ResponseStatusException ex) {
    String message =
        ex.getReason() != null ? ex.getReason() : "Ошибка сервера";
    return ResponseEntity.status(ex.getStatusCode()).body(new ErrorBody(message));
  }

  public record ErrorBody(String message) {}
}
