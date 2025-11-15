package com.lourenco.backend.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RelatorioVendasDTO {
    private LocalDate data;
    private Long totalPedidos;
    private Double valorTotal;
}
