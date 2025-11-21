package com.lourenco.backend.dto;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistroUsuarioDTO {
    
    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
    private String nome;
    
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    private String email;
    
    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, max = 100, message = "Senha deve ter no mínimo 6 caracteres")
    @Pattern(
        regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).*$",
        message = "Senha deve conter letras maiúsculas, minúsculas e números"
    )
    private String senha;
    
    @NotBlank(message = "CPF é obrigatório")
    @Pattern(
        regexp = "^[0-9]{3}\\.?[0-9]{3}\\.?[0-9]{3}-?[0-9]{2}$",
        message = "CPF inválido. Formato: 123.456.789-09"
    )
    private String cpf;
    
    @Pattern(regexp = "USER|ADMIN", message = "Role deve ser USER ou ADMIN")
    private String role = "USER";
    
    @Pattern(
        regexp = "^\\([0-9]{2}\\) [0-9]{4,5}-[0-9]{4}$",
        message = "Telefone inválido. Formato: (11) 98765-4321"
    )
    private String telefone;
    
    @Size(max = 200, message = "Endereço não pode ter mais de 200 caracteres")
    private String endereco;
}