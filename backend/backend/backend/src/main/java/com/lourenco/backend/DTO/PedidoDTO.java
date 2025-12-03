package com.lourenco.backend.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.lourenco.backend.model.StatusPedido;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class PedidoDTO {
    private UUID id;
    private UUID usuarioId;
    private String nomeUsuario;
    private List<ItemPedidoDTO> itens;
    private LocalDateTime dataPedido;
    private StatusPedido status;
    private Double valorTotal;
    private String enderecoEntrega;
    private String observacoes;
}
