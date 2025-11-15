package com.lourenco.backend.dto;

import com.lourenco.backend.model.StatusPagamento;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PixResponseDTO {
    private UUID pagamentoId;
    private UUID pedidoId;
    private Double valor;
    private StatusPagamento status;
    private String pixCopiaCola;
    private String qrCodeBase64;
    private LocalDateTime dataExpiracao;
    private LocalDateTime dataCriacao;
}
