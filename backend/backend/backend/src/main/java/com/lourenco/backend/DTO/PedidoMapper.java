package com.lourenco.backend.dto;


import java.util.stream.Collectors;

import com.lourenco.backend.model.Pedido;

public class PedidoMapper {

    public static PedidoDTO toDTO(Pedido pedido) {
        return PedidoDTO.builder()
                .id(pedido.getId())
                .usuarioId(pedido.getUsuario().getId())
                .nomeUsuario(pedido.getUsuario().getNome())
                .dataPedido(pedido.getDataPedido())
                .status(pedido.getStatus())
                .valorTotal(pedido.getValorTotal())
                .enderecoEntrega(pedido.getEnderecoEntrega())
                .observacoes(pedido.getObservacoes())
                .itens(
                    pedido.getItens().stream()
                        .map(item -> new ItemPedidoDTO(
                                item.getProduto().getId(),
                                item.getProduto().getNome(),
                                item.getQuantidade(),
                                item.getPrecoUnitario(),
                                item.getSubtotal()
                        ))
                        .collect(Collectors.toList())
                )
                .build();
    }
}
