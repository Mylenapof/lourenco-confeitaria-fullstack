package com.lourenco.backend.controller;

import com.lourenco.backend.dto.PixResponseDTO;
import com.lourenco.backend.model.Pagamento;
import com.lourenco.backend.service.PagamentoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/pagamentos")
@CrossOrigin(origins = "*")
public class PagamentoController {

    private final PagamentoService pagamentoService;

    public PagamentoController(PagamentoService pagamentoService) {
        this.pagamentoService = pagamentoService;
    }

    @PostMapping("/pedido/{pedidoId}/pix")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public PixResponseDTO gerarPagamentoPix(@PathVariable UUID pedidoId) {
        return pagamentoService.gerarPagamentoPix(pedidoId);
    }

    @GetMapping("/pedido/{pedidoId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public Pagamento buscarPagamentoPorPedido(@PathVariable UUID pedidoId) {
        return pagamentoService.buscarPagamentoPorPedido(pedidoId);
    }

    @GetMapping("/{pagamentoId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public Pagamento buscarPagamento(@PathVariable UUID pagamentoId) {
        return pagamentoService.buscarPagamentoPorId(pagamentoId);
    }

    @PostMapping("/{pagamentoId}/simular-pagamento")
    @PreAuthorize("hasRole('ADMIN')")
    public Pagamento simularPagamento(@PathVariable UUID pagamentoId) {
        return pagamentoService.simularPagamento(pagamentoId);
    }

    @PostMapping("/verificar-expirados")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> verificarExpirados() {
        pagamentoService.verificarPagamentosExpirados();
        return ResponseEntity.ok("Pagamentos expirados verificados");
    }
}
