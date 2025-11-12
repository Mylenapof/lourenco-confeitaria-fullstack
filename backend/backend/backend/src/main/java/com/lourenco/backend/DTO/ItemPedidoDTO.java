package com.lourenco.backend.DTO;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemPedidoDTO {
    private UUID produtoId;
    private String nomeProduto;
    private Integer quantidade;
    private Double precoUnitario;
    private Double subtotal;
}