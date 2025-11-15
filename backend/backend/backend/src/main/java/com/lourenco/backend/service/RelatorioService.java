package com.lourenco.backend.service;

import com.lourenco.backend.dto.DashboardDTO;
import com.lourenco.backend.dto.ProdutoMaisVendidoDTO;
import com.lourenco.backend.dto.RelatorioVendasDTO;
import com.lourenco.backend.model.StatusEncomenda;
import com.lourenco.backend.model.StatusPedido;
import com.lourenco.backend.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RelatorioService {

    private final ProdutoRepository produtoRepository;
    private final CategoriaRepository categoriaRepository;
    private final UsuarioRepository usuarioRepository;
    private final PedidoRepository pedidoRepository;
    private final EncomendaRepository encomendaRepository;

    public RelatorioService(
            ProdutoRepository produtoRepository,
            CategoriaRepository categoriaRepository,
            UsuarioRepository usuarioRepository,
            PedidoRepository pedidoRepository,
            EncomendaRepository encomendaRepository) {
        this.produtoRepository = produtoRepository;
        this.categoriaRepository = categoriaRepository;
        this.usuarioRepository = usuarioRepository;
        this.pedidoRepository = pedidoRepository;
        this.encomendaRepository = encomendaRepository;
    }

    public DashboardDTO getDashboard() {
        return DashboardDTO.builder()
                .totalProdutos(produtoRepository.count())
                .totalProdutosDisponiveis((long) produtoRepository.findByDisponivelTrue().size())
                .totalCategorias(categoriaRepository.count())
                .totalUsuarios(usuarioRepository.count())
                .totalPedidos(pedidoRepository.count())
                .pedidosPendentes((long) pedidoRepository.findByStatus(StatusPedido.PENDENTE).size())
                .pedidosConfirmados((long) pedidoRepository.findByStatus(StatusPedido.CONFIRMADO).size())
                .pedidosEntregues((long) pedidoRepository.findByStatus(StatusPedido.ENTREGUE).size())
                .totalEncomendas(encomendaRepository.count())
                .encomendasPendentes((long) encomendaRepository.findByStatus(StatusEncomenda.PENDENTE).size())
                .encomendasAprovadas((long) encomendaRepository.findByStatus(StatusEncomenda.APROVADO).size())
                .faturamentoTotal(calcularFaturamentoTotal())
                .faturamentoMesAtual(calcularFaturamentoMesAtual())
                .ticketMedio(calcularTicketMedio())
                .produtosMaisVendidos(getProdutosMaisVendidos(5))
                .vendasUltimos30Dias(getVendasUltimos30Dias())
                .build();
    }

    public Double calcularFaturamentoTotal() {
        return pedidoRepository.findAll().stream()
                .filter(pedido -> pedido.getStatus() == StatusPedido.ENTREGUE)
                .mapToDouble(pedido -> pedido.getValorTotal() != null ? pedido.getValorTotal() : 0.0)
                .sum();
    }

    public Double calcularFaturamentoMesAtual() {
        LocalDateTime inicioMes = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        
        return pedidoRepository.findAll().stream()
                .filter(pedido -> pedido.getStatus() == StatusPedido.ENTREGUE)
                .filter(pedido -> pedido.getDataPedido().isAfter(inicioMes))
                .mapToDouble(pedido -> pedido.getValorTotal() != null ? pedido.getValorTotal() : 0.0)
                .sum();
    }

    public Double calcularTicketMedio() {
        var pedidosEntregues = pedidoRepository.findByStatus(StatusPedido.ENTREGUE);
        
        if (pedidosEntregues.isEmpty()) {
            return 0.0;
        }
        
        double total = pedidosEntregues.stream()
                .mapToDouble(pedido -> pedido.getValorTotal() != null ? pedido.getValorTotal() : 0.0)
                .sum();
        
        return total / pedidosEntregues.size();
    }

    public List<ProdutoMaisVendidoDTO> getProdutosMaisVendidos(int limite) {
        Map<String, ProdutoMaisVendidoDTO> produtosMap = new HashMap<>();
        
        pedidoRepository.findAll().stream()
                .filter(pedido -> pedido.getStatus() == StatusPedido.ENTREGUE)
                .flatMap(pedido -> pedido.getItens().stream())
                .forEach(item -> {
                    String produtoId = item.getProduto().getId().toString();
                    
                    ProdutoMaisVendidoDTO dto = produtosMap.getOrDefault(produtoId, 
                        ProdutoMaisVendidoDTO.builder()
                            .produtoId(item.getProduto().getId())
                            .nomeProduto(item.getProduto().getNome())
                            .categoriaNome(item.getProduto().getCategoria().getNome())
                            .quantidadeVendida(0L)
                            .valorTotal(0.0)
                            .build()
                    );
                    
                    dto.setQuantidadeVendida(dto.getQuantidadeVendida() + item.getQuantidade());
                    dto.setValorTotal(dto.getValorTotal() + item.getSubtotal());
                    
                    produtosMap.put(produtoId, dto);
                });
        
        return produtosMap.values().stream()
                .sorted((a, b) -> Long.compare(b.getQuantidadeVendida(), a.getQuantidadeVendida()))
                .limit(limite)
                .collect(Collectors.toList());
    }

    public List<RelatorioVendasDTO> getVendasUltimos30Dias() {
        LocalDateTime dataInicio = LocalDateTime.now().minusDays(30);
        
        Map<LocalDate, RelatorioVendasDTO> vendasPorDia = new HashMap<>();
        
        // Inicializa todos os dias com zero
        for (int i = 0; i < 30; i++) {
            LocalDate data = LocalDate.now().minusDays(i);
            vendasPorDia.put(data, RelatorioVendasDTO.builder()
                    .data(data)
                    .totalPedidos(0L)
                    .valorTotal(0.0)
                    .build());
        }
        
        // Preenche com os dados reais
        pedidoRepository.findAll().stream()
                .filter(pedido -> pedido.getDataPedido().isAfter(dataInicio))
                .filter(pedido -> pedido.getStatus() == StatusPedido.ENTREGUE)
                .forEach(pedido -> {
                    LocalDate data = pedido.getDataPedido().toLocalDate();
                    RelatorioVendasDTO dto = vendasPorDia.get(data);
                    
                    if (dto != null) {
                        dto.setTotalPedidos(dto.getTotalPedidos() + 1);
                        dto.setValorTotal(dto.getValorTotal() + (pedido.getValorTotal() != null ? pedido.getValorTotal() : 0.0));
                    }
                });
        
        return vendasPorDia.values().stream()
                .sorted((a, b) -> a.getData().compareTo(b.getData()))
                .collect(Collectors.toList());
    }

    public List<RelatorioVendasDTO> getVendasPorPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        Map<LocalDate, RelatorioVendasDTO> vendasPorDia = new HashMap<>();
        
        LocalDateTime inicio = dataInicio.atStartOfDay();
        LocalDateTime fim = dataFim.atTime(23, 59, 59);
        
        pedidoRepository.findAll().stream()
                .filter(pedido -> pedido.getDataPedido().isAfter(inicio) && pedido.getDataPedido().isBefore(fim))
                .filter(pedido -> pedido.getStatus() == StatusPedido.ENTREGUE)
                .forEach(pedido -> {
                    LocalDate data = pedido.getDataPedido().toLocalDate();
                    
                    RelatorioVendasDTO dto = vendasPorDia.getOrDefault(data, 
                        RelatorioVendasDTO.builder()
                            .data(data)
                            .totalPedidos(0L)
                            .valorTotal(0.0)
                            .build()
                    );
                    
                    dto.setTotalPedidos(dto.getTotalPedidos() + 1);
                    dto.setValorTotal(dto.getValorTotal() + (pedido.getValorTotal() != null ? pedido.getValorTotal() : 0.0));
                    
                    vendasPorDia.put(data, dto);
                });
        
        return new ArrayList<>(vendasPorDia.values()).stream()
                .sorted((a, b) -> a.getData().compareTo(b.getData()))
                .collect(Collectors.toList());
    }
}
