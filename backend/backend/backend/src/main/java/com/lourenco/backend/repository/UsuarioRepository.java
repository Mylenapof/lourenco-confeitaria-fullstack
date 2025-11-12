package com.lourenco.backend.repository;

import java.util.Optional;
import java.util.UUID; // Adicionar esta importação

import org.springframework.data.jpa.repository.JpaRepository;

import com.lourenco.backend.model.Usuario;

// Mudar o tipo de Long para UUID
public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {
    Optional<Usuario> findByEmail(String email);
}