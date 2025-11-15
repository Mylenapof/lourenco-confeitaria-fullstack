package com.lourenco.backend.repository;


import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.lourenco.backend.model.Pedido;
import com.lourenco.backend.model.StatusPedido;
import com.lourenco.backend.model.Usuario;

public interface PedidoRepository extends JpaRepository<Pedido, UUID> {
    List<Pedido> findByUsuario(Usuario usuario);
    List<Pedido> findByStatus(StatusPedido status);
    List<Pedido> findByUsuarioOrderByDataPedidoDesc(Usuario usuario);
    
    // COM PAGINAÇÃO
    Page<Pedido> findAll(Pageable pageable);
    Page<Pedido> findByUsuarioOrderByDataPedidoDesc(Usuario usuario, Pageable pageable);
    Page<Pedido> findByStatus(StatusPedido status, Pageable pageable);
}
