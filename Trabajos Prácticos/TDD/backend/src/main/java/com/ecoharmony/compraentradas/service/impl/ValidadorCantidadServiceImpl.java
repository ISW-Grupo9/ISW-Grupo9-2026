package com.ecoharmony.compraentradas.service.impl;

import com.ecoharmony.compraentradas.service.*;

import com.ecoharmony.compraentradas.exception.CantidadInvalidaException;
import org.springframework.stereotype.Service;

@Service
public class ValidadorCantidadServiceImpl implements ValidadorCantidadService {

  private static final int MIN = 1;
  private static final int MAX = 10;

  @Override
  public void validar(int cantidad) {
    if (cantidad < MIN || cantidad > MAX) {
      throw new CantidadInvalidaException(
          "La cantidad debe ser entre " + MIN + " y " + MAX + ". Recibido: " + cantidad);
    }
  }
}

