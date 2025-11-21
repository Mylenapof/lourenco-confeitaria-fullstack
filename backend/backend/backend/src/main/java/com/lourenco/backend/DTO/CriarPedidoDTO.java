package com.lourenco.backend.dto;

import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CriarPedidoDTO {
    
    @NotNull(message = "Usuário é obrigatório")
    private UUID usuarioId;
    
    @NotEmpty(message = "Pedido deve ter pelo menos um item")
    @Valid
    private List itens;
    
    @NotBlank(message = "Endereço de entrega é obrigatório")
    @Size(max = 300, message = "Endereço muito longo")
    private String enderecoEntrega;
    
    @Size(max = 500, message = "Observações muito longas")
    private String observacoes;
}