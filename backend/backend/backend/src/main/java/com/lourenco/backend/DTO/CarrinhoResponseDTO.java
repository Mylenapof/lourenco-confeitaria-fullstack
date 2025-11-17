package com.lourenco.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarrinhoResponseDTO {
    private UUID carrinhoId;
    private UUID usuarioId;
    private List<ItemCarrinhoDTO> itens;
    private Integer totalItens;
    private Double valorTotal;
    private LocalDateTime dataAtualizacao;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemCarrinhoDTO {
        private UUID itemId;
        private UUID produtoId;
        private String produtoNome;
        private String produtoImagemUrl;
        private Double precoUnitario;
        private Integer quantidade;
        private Double subtotal;
        private Boolean produtoDisponivel;
    }
}
