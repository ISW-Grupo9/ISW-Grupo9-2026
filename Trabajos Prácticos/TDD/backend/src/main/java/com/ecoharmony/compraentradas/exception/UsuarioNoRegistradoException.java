package com.ecoharmony.compraentradas.exception;

public class UsuarioNoRegistradoException extends RuntimeException {
  public UsuarioNoRegistradoException(String mensaje) {
    super(mensaje);
  }
}
