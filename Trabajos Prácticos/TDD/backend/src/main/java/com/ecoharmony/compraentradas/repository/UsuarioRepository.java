package com.ecoharmony.compraentradas.repository;

import com.ecoharmony.compraentradas.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
}
