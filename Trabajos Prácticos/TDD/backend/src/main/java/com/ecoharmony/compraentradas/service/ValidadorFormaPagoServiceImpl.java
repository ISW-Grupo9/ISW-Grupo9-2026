package com.ecoharmony.compraentradas.service;

import com.ecoharmony.compraentradas.exception.FormaPagoRequeridaException;
import com.ecoharmony.compraentradas.model.FormaPago;
import org.springframework.stereotype.Service;

@Service
public class ValidadorFormaPagoServiceImpl implements ValidadorFormaPagoService {

  @Override
  public void validar(FormaPago formaPago) {
    if (formaPago == null) {
      throw new FormaPagoRequeridaException("La forma de pago es requerida.");
    }
  }
}
