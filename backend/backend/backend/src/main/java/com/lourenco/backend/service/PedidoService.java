package com.lourenco.backend.service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lourenco.backend.model.ItemPedido;
import com.lourenco.backend.model.Pedido;
import com.lourenco.backend.model.Produto;
import com.lourenco.backend.model.StatusPedido;
import com.lourenco.backend.model.Usuario;
import com.lourenco.backend.repository.PedidoRepository;
import com.lourenco.backend.repository.ProdutoRepository;
import com.lourenco.backend.repository.UsuarioRepository;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProdutoRepository produtoRepository;
    private final NotificacaoService notificacaoService;

    public PedidoService(PedidoRepository pedidoRepository,
                         UsuarioRepository usuarioRepository,
                         ProdutoRepository produtoRepository,
                         NotificacaoService notificacaoService) {
        this.pedidoRepository = pedidoRepository;
        this.usuarioRepository = usuarioRepository;
        this.produtoRepository = produtoRepository;
        this.notificacaoService = notificacaoService;
    }

    public List<Pedido> listarTodos() {
        return pedidoRepository.findAll();
    }

    public List<Pedido> listarPorUsuario(UUID usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        return pedidoRepository.findByUsuarioOrderByDataPedidoDesc(usuario);
    }

    public List<Pedido> listarPorStatus(StatusPedido status) {
        return pedidoRepository.findByStatus(status);
    }

    @Transactional
    public Pedido criarPedido(Pedido pedido) {
        // Valida usuário
        Usuario usuario = usuarioRepository.findById(pedido.getUsuario().getId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        
        pedido.setUsuario(usuario);
        pedido.setDataPedido(LocalDateTime.now());
        pedido.setStatus(StatusPedido.PENDENTE);
        
        // Calcula total e valida produtos
        double total = 0.0;
        for (ItemPedido item : pedido.getItens()) {
            Produto produto = produtoRepository.findById(item.getProduto().getId())
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado"));
            
            item.setProduto(produto);
            item.setPrecoUnitario(produto.getPreco());
            item.setPedido(pedido);
            total += item.getSubtotal();
        }
        
        pedido.setValorTotal(total);
        
        // --- INÍCIO DA MUDANÇA ---
        Pedido pedidoSalvo = pedidoRepository.save(pedido);
        
        // ADICIONE: Notificar admins
        notificacaoService.notificarAdminsNovoPedido(pedidoSalvo);
            
        return pedidoSalvo;
        // --- FIM DA MUDANÇA ---
    }

    public Pedido buscarPorId(UUID id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));
    }

    public Pedido atualizarStatus(UUID id, StatusPedido novoStatus) {
        Pedido pedido = buscarPorId(id);
        pedido.setStatus(novoStatus);
        
        // --- INÍCIO DA MUDANÇA ---
        Pedido pedidoAtualizado = pedidoRepository.save(pedido);
        
        // ADICIONE: Notificar usuário
        notificacaoService.notificarUsuarioStatusPedido(pedidoAtualizado, novoStatus);
            
        return pedidoAtualizado;
        // --- FIM DA MUDANÇA ---
    }

    public void deletar(UUID id) {
        pedidoRepository.deleteById(id);
    }

    public Page<Pedido> listarTodosPaginado(int page, int size, String sortBy, String direction) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        return pedidoRepository.findAll(pageable);
    }

    public Page<Pedido> listarPorUsuarioPaginado(UUID usuarioId, int page, int size) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dataPedido"));
        return pedidoRepository.findByUsuarioOrderByDataPedidoDesc(usuario, pageable);
    }

    public Page<Pedido> listarPorStatusPaginado(StatusPedido status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dataPedido"));
        return pedidoRepository.findByStatus(status, pageable);
    }
}