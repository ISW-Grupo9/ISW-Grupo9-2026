package com.ecoharmony.compraentradas.service.impl;

import com.ecoharmony.compraentradas.exception.UsuarioNoRegistradoException;
import com.ecoharmony.compraentradas.repository.UsuarioRepository;
import com.ecoharmony.compraentradas.service.*;
import org.springframework.stereotype.Service;

@Service
public class ValidadorUsuarioServiceImpl implements ValidadorUsuarioService {

  private final UsuarioRepository usuarioRepository;

  public ValidadorUsuarioServiceImpl(UsuarioRepository usuarioRepository) {
    this.usuarioRepository = usuarioRepository;
  }

  @Override
  public void validar(Long usuarioId) {
    if (usuarioId == null || !usuarioRepository.existsById(usuarioId)) {
      throw new UsuarioNoRegistradoException(
          "El usuario con id " + usuarioId + " no estÃ¡ registrado.");
    }
  }
}
