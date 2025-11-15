package com.lourenco.backend.repository;

import com.lourenco.backend.model.Pagamento;
import com.lourenco.backend.model.StatusPagamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PagamentoRepository extends JpaRepository<Pagamento, UUID> {
    Optional<Pagamento> findByPedidoId(UUID pedidoId);
    List<Pagamento> findByStatus(StatusPagamento status);
}
