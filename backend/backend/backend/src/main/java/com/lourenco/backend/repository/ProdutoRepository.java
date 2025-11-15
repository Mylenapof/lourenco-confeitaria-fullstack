package com.lourenco.backend.repository;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.lourenco.backend.model.Produto;

public interface ProdutoRepository extends JpaRepository<Produto, UUID> {
    List<Produto> findByDisponivelTrue();
    List<Produto> findByDestaqueTrue();
    List<Produto> findByCategoriaId(UUID categoriaId);
    
    // MÉTODOS COM PAGINAÇÃO
    Page<Produto> findByDisponivelTrue(Pageable pageable);
    Page<Produto> findByDestaqueTrue(Pageable pageable);
    Page<Produto> findByCategoriaId(UUID categoriaId, Pageable pageable);
}
