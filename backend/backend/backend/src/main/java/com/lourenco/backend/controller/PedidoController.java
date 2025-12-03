package com.lourenco.backend.controller;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lourenco.backend.dto.PedidoDTO;
import com.lourenco.backend.dto.PixResponseDTO;
import com.lourenco.backend.model.Pedido;
import com.lourenco.backend.model.StatusPedido;
import com.lourenco.backend.service.PagamentoService;
import com.lourenco.backend.service.PedidoService;

@RestController
@RequestMapping("/pedidos")
@CrossOrigin(origins = "*")
public class PedidoController {

    private final PedidoService pedidoService;
    private final PagamentoService pagamentoService;

    public PedidoController(PedidoService pedidoService,
                            PagamentoService pagamentoService) {
        this.pedidoService = pedidoService;
        this.pagamentoService = pagamentoService;
    }

    // 📌 LISTAR TODOS (ADMIN)
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<PedidoDTO> listarTodos() {
        return pedidoService.listarTodos();
    }

    // 📌 LISTAR PEDIDOS DO USUÁRIO LOGADO
    @GetMapping("/usuario/{usuarioId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<PedidoDTO> listarPorUsuario(@PathVariable UUID usuarioId) {
        return pedidoService.listarPorUsuario(usuarioId);
    }

    // 📌 LISTAR POR STATUS (ADMIN)
    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public List<PedidoDTO> listarPorStatus(@PathVariable StatusPedido status) {
        return pedidoService.listarPorStatus(status);
    }

    // 📌 BUSCAR POR ID
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public Pedido buscarPorId(@PathVariable UUID id) {
        return pedidoService.buscarPorId(id);
    }

    // 📌 CRIAR PEDIDO MANUAL (não via carrinho)
    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public Pedido criar(@RequestBody Pedido pedido) {
        return pedidoService.criarPedido(pedido);
    }

    // 📌 NOVO ENDPOINT: CRIAR PEDIDO A PARTIR DO CARRINHO + GERAR PIX
    @PostMapping("/do-carrinho/usuario/{usuarioId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public PixResponseDTO criarDoCarrinhoEGerarPix(
            @PathVariable UUID usuarioId,
            @RequestBody Map<String, String> body) {

        String enderecoEntrega = body.get("enderecoEntrega");
        String observacoes = body.getOrDefault("observacoes", "");

        // 1) Cria o pedido com base no carrinho
        Pedido pedido = pedidoService.criarPedidoDoCarrinho(usuarioId, enderecoEntrega, observacoes);

        // 2) Gera o pagamento PIX para esse pedido
        return pagamentoService.gerarPagamentoPix(pedido.getId());
    }

    // 📌 ATUALIZAR STATUS (ADMIN)
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public Pedido atualizarStatus(@PathVariable UUID id, @RequestBody String novoStatus) {
        StatusPedido status = StatusPedido.valueOf(novoStatus.replace("\"", ""));
        return pedidoService.atualizarStatus(id, status);
    }

    // 📌 DELETAR PEDIDO (ADMIN)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        pedidoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}