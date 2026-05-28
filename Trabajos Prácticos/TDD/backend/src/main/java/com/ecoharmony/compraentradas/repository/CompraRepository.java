package com.ecoharmony.compraentradas.repository;

import com.ecoharmony.compraentradas.model.Compra;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompraRepository extends JpaRepository<Compra, Long> {
}
