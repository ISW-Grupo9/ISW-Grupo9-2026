package com.ecoharmony.compraentradas.exception;

import java.util.Map;
import java.util.NoSuchElementException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler({
    FechaInvalidaException.class,
    ParqueCerradoException.class,
    CantidadInvalidaException.class,
    VisitantesInvalidosException.class,
    FormaPagoRequeridaException.class
  })
  public ResponseEntity<Map<String, String>> handleBadRequest(RuntimeException ex) {
    return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
  }

  @ExceptionHandler(UsuarioNoRegistradoException.class)
  public ResponseEntity<Map<String, String>> handleUsuarioNoRegistrado(
      UsuarioNoRegistradoException ex) {
    return ResponseEntity.status(404).body(Map.of("error", ex.getMessage()));
  }

  @ExceptionHandler(NoSuchElementException.class)
  public ResponseEntity<Void> handleNotFound(NoSuchElementException ex) {
    return ResponseEntity.notFound().build();
  }
}
