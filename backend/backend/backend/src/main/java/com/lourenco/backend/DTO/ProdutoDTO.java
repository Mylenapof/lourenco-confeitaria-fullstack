package com.lourenco.backend.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProdutoDTO {
    
    @NotBlank(message = "Nome do produto é obrigatório")
    @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
    private String nome;
    
    @NotNull(message = "Preço é obrigatório")
    @DecimalMin(value = "0.01", message = "Preço deve ser maior que zero")
    @DecimalMax(value = "99999.99", message = "Preço muito alto")
    private Double preco;
    
    @Size(max = 1000, message = "Ingredientes não pode ter mais de 1000 caracteres")
    private String ingredientes;
    
    @Pattern(
        regexp = "^(http|https)://.*\\.(jpg|jpeg|png|gif|webp)$",
        message = "URL da imagem inválida"
    )
    private String imagemUrl;
    
    @NotNull(message = "Categoria é obrigatória")
    private UUID categoriaId;
    
    private Boolean disponivel = true;
    private Boolean destaque = false;
}
