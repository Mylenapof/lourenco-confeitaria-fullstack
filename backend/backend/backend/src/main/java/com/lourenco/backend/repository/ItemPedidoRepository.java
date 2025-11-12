package com.lourenco.backend.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lourenco.backend.model.ItemPedido;

public interface ItemPedidoRepository extends JpaRepository<ItemPedido, UUID> {
}
