package com.lourenco.backend.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Page;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lourenco.backend.model.Pedido;
import com.lourenco.backend.model.StatusPedido;
import com.lourenco.backend.service.PedidoService;

@RestController
@RequestMapping("/pedidos")
@CrossOrigin(origins = "*")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<Pedido> listarTodos() {
        return pedidoService.listarTodos();
    }

    @GetMapping("/usuario/{usuarioId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<Pedido> listarPorUsuario(@PathVariable UUID usuarioId) {
        return pedidoService.listarPorUsuario(usuarioId);
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Pedido> listarPorStatus(@PathVariable StatusPedido status) {
        return pedidoService.listarPorStatus(status);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public Pedido buscar(@PathVariable UUID id) {
        return pedidoService.buscarPorId(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public Pedido criar(@RequestBody Pedido pedido) {
        return pedidoService.criarPedido(pedido);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public Pedido atualizarStatus(@PathVariable UUID id, @RequestBody Map<String, String> body) {
        StatusPedido novoStatus = StatusPedido.valueOf(body.get("status"));
        return pedidoService.atualizarStatus(id, novoStatus);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        pedidoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/page")
@PreAuthorize("hasRole('ADMIN')")
public Page<Pedido> listarTodosPaginado(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "dataPedido") String sortBy,
        @RequestParam(defaultValue = "desc") String direction) {
    return pedidoService.listarTodosPaginado(page, size, sortBy, direction);
}

@GetMapping("/usuario/{usuarioId}/page")
@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
public Page<Pedido> listarPorUsuarioPaginado(
        @PathVariable UUID usuarioId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
    return pedidoService.listarPorUsuarioPaginado(usuarioId, page, size);
}

@GetMapping("/status/{status}/page")
@PreAuthorize("hasRole('ADMIN')")
public Page<Pedido> listarPorStatusPaginado(
        @PathVariable StatusPedido status,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
    return pedidoService.listarPorStatusPaginado(status, page, size);
}
@PostMapping("/do-carrinho/usuario/{usuarioId}")
@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
public Pedido criarPedidoDoCarrinho(
        @PathVariable UUID usuarioId,
        @RequestBody Map<String, String> body) {
    String enderecoEntrega = body.get("enderecoEntrega");
    String observacoes = body.get("observacoes");
    return pedidoService.criarPedidoDoCarrinho(usuarioId, enderecoEntrega, observacoes);
}
}
