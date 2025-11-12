package com.lourenco.backend.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lourenco.backend.model.Encomenda;
import com.lourenco.backend.model.StatusEncomenda;
import com.lourenco.backend.model.Usuario;

public interface EncomendaRepository extends JpaRepository<Encomenda, UUID> {
    List<Encomenda> findByUsuario(Usuario usuario);
    List<Encomenda> findByStatus(StatusEncomenda status);
    List<Encomenda> findByUsuarioOrderByDataEntregaDesc(Usuario usuario);
}
