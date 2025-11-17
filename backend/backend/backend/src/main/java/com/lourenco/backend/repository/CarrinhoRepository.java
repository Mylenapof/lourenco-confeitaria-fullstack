package com.lourenco.backend.repository;
import com.lourenco.backend.model.Carrinho;
import com.lourenco.backend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CarrinhoRepository extends JpaRepository<Carrinho, UUID> {
    Optional<Carrinho> findByUsuario(Usuario usuario);
    Optional<Carrinho> findByUsuarioId(UUID usuarioId);
}
