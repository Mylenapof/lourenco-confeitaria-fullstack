package com.lourenco.backend.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lourenco.backend.model.Pedido;
import com.lourenco.backend.model.StatusPedido;
import com.lourenco.backend.model.Usuario;

public interface PedidoRepository extends JpaRepository<Pedido, UUID> {
    List<Pedido> findByUsuario(Usuario usuario);
    List<Pedido> findByStatus(StatusPedido status);
    List<Pedido> findByUsuarioOrderByDataPedidoDesc(Usuario usuario);
}
