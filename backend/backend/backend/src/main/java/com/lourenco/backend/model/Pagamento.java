package com.lourenco.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "pedido_id", nullable = false, unique = true)
    private Pedido pedido;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private StatusPagamento status = StatusPagamento.PENDENTE;

    @Column(nullable = false)
    private Double valor;

    @Column(nullable = false)
    private String metodoPagamento = "PIX";

    @Column(length = 2000)
    private String pixCopiaCola; // Código PIX para copiar

    @Column(length = 5000)
    private String qrCodeBase64; // QR Code em Base64

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime dataCriacao = LocalDateTime.now();

    @Builder.Default
    private LocalDateTime dataExpiracao = LocalDateTime.now().plusMinutes(30); // Expira em 30min

    private LocalDateTime dataAprovacao;
}
