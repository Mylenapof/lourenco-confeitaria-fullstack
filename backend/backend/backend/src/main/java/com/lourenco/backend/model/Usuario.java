package com.lourenco.backend.model;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String nome;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String senha;
    
    // ✅ ADICIONAR ESTE CAMPO
    @Column(unique = true, length = 14)
    private String cpf;
    
    @Column(nullable = false)
    private String role; // USER, ADMIN
    
    private String telefone;
    
    private String endereco;
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean ativo = true;
}