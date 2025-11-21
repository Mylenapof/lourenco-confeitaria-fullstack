package com.lourenco.backend.dto;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EncomendaDTO {
    
    @NotNull(message = "Usuário é obrigatório")
    private UUID usuarioId;
    
    @NotBlank(message = "Tipo do produto é obrigatório")
    @Size(min = 3, max = 50, message = "Tipo deve ter entre 3 e 50 caracteres")
    private String tipoProduto;
    
    @Pattern(
        regexp = "Pequeno|Médio|Grande",
        message = "Tamanho deve ser: Pequeno, Médio ou Grande"
    )
    private String tamanho;
    
    @Size(max = 150, message = "Sabor não pode ter mais de 500 caracteres")
    private String sabor;
    
    @Size(max = 150, message = "Decoração não pode ter mais de 1000 caracteres")
    private String decoracao;
    
    @Size(max = 150, message = "Observações não pode ter mais de 2000 caracteres")
    private String observacoes;
    
    @NotNull(message = "Data de entrega é obrigatória")
    @Future(message = "Data de entrega deve ser futura")
    private LocalDate dataEntrega;
    
    @DecimalMin(value = "0.00", message = "Valor estimado não pode ser negativo")
    private Double valorEstimado;
}