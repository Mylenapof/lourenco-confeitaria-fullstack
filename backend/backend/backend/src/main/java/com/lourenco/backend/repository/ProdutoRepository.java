package com.lourenco.backend.repository;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lourenco.backend.model.Produto;

public interface ProdutoRepository extends JpaRepository<Produto, UUID> {
    List<Produto> findByDisponivelTrue();
    List<Produto> findByDestaqueTrue();
    List<Produto> findByCategoriaId(UUID categoriaId);
}
