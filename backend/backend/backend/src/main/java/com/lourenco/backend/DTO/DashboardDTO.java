package com.lourenco.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDTO {
    // Estatísticas Gerais
    private Long totalProdutos;
    private Long totalProdutosDisponiveis;
    private Long totalCategorias;
    private Long totalUsuarios;
    
    // Pedidos
    private Long totalPedidos;
    private Long pedidosPendentes;
    private Long pedidosConfirmados;
    private Long pedidosEntregues;
    
    // Encomendas
    private Long totalEncomendas;
    private Long encomendasPendentes;
    private Long encomendasAprovadas;
    
    // Financeiro
    private Double faturamentoTotal;
    private Double faturamentoMesAtual;
    private Double ticketMedio;
    
    // Produtos
    private List<ProdutoMaisVendidoDTO> produtosMaisVendidos;
    
    // Vendas por período
    private List<RelatorioVendasDTO> vendasUltimos30Dias;
}
