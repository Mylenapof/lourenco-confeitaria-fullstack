package com.lourenco.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProdutoMaisVendidoDTO {
    private UUID produtoId;
    private String nomeProduto;
    private String categoriaNome;
    private Long quantidadeVendida;
    private Double valorTotal;
}
