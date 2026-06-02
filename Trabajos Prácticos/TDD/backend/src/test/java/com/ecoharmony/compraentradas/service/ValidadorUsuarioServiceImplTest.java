package com.ecoharmony.compraentradas.service;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ecoharmony.compraentradas.exception.UsuarioNoRegistradoException;
import com.ecoharmony.compraentradas.model.Usuario;
import com.ecoharmony.compraentradas.repository.UsuarioRepository;
import com.ecoharmony.compraentradas.service.impl.ValidadorUsuarioServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@DisplayName("ValidadorUsuarioService")
class ValidadorUsuarioServiceImplTest {

  @Autowired private UsuarioRepository usuarioRepository;

  private ValidadorUsuarioService validador;

  @BeforeEach
  void setUp() {
    validador = new ValidadorUsuarioServiceImpl(usuarioRepository);
  }

  // ─── Ciclo 8.1 ────────────────────────────────────────────────────────────
  // El usuario existe en la BD: la validación debe pasar sin excepción.
  @Test
  @DisplayName("debe aceptar un usuario registrado en la BD")
  void debe_aceptar_usuario_registrado() {
    Usuario usuario =
        usuarioRepository.save(new Usuario("Juan Pérez", "juan@example.com", "12345678"));

    assertThatNoException().isThrownBy(() -> validador.validar(usuario.getId()));
  }

  // ─── Ciclo 8.2 ────────────────────────────────────────────────────────────
  // El ID no corresponde a ningún usuario en la BD.
  @Test
  @DisplayName("debe rechazar un usuario que no existe en la BD")
  void debe_rechazar_usuario_no_registrado() {
    assertThatThrownBy(() -> validador.validar(999L))
        .isInstanceOf(UsuarioNoRegistradoException.class);
  }

  // ─── Ciclo 8.3 ────────────────────────────────────────────────────────────
  // Un ID null no puede corresponder a ningún usuario válido.
  @Test
  @DisplayName("debe rechazar un usuarioId null")
  void debe_rechazar_id_nulo() {
    assertThatThrownBy(() -> validador.validar(null))
        .isInstanceOf(UsuarioNoRegistradoException.class);
  }
}
