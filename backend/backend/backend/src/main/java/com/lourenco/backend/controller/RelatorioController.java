package com.lourenco.backend.controller;

import com.lourenco.backend.dto.DashboardDTO;
import com.lourenco.backend.dto.ProdutoMaisVendidoDTO;
import com.lourenco.backend.dto.RelatorioVendasDTO;
import com.lourenco.backend.service.RelatorioService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/relatorios")
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('ADMIN')")
public class RelatorioController {

    private final RelatorioService relatorioService;

    public RelatorioController(RelatorioService relatorioService) {
        this.relatorioService = relatorioService;
    }

    @GetMapping("/dashboard")
    public DashboardDTO getDashboard() {
        return relatorioService.getDashboard();
    }

    @GetMapping("/faturamento/total")
    public Double getFaturamentoTotal() {
        return relatorioService.calcularFaturamentoTotal();
    }

    @GetMapping("/faturamento/mes-atual")
    public Double getFaturamentoMesAtual() {
        return relatorioService.calcularFaturamentoMesAtual();
    }

    @GetMapping("/ticket-medio")
    public Double getTicketMedio() {
        return relatorioService.calcularTicketMedio();
    }

    @GetMapping("/produtos-mais-vendidos")
    public List<ProdutoMaisVendidoDTO> getProdutosMaisVendidos(
            @RequestParam(defaultValue = "10") int limite) {
        return relatorioService.getProdutosMaisVendidos(limite);
    }

    @GetMapping("/vendas/ultimos-30-dias")
    public List<RelatorioVendasDTO> getVendasUltimos30Dias() {
        return relatorioService.getVendasUltimos30Dias();
    }

    @GetMapping("/vendas/periodo")
    public List<RelatorioVendasDTO> getVendasPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        return relatorioService.getVendasPorPeriodo(dataInicio, dataFim);
    }
}