package com.lourenco.backend.model;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Encomenda {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false)
    private String tipoProduto; // Bolo, Torta, Cupcake, etc.

    private String tamanho; // Pequeno, Médio, Grande

    @Column(length = 500)
    private String sabor;

    @Column(length = 1000)
    private String decoracao;

    @Column(length = 2000)
    private String observacoes;

    @Column(nullable = false)
    private LocalDate dataEntrega;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private StatusEncomenda status = StatusEncomenda.PENDENTE;

    private Double valorEstimado;
}
