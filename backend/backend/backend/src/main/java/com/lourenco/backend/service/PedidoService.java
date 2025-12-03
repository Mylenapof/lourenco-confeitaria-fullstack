package com.lourenco.backend.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lourenco.backend.dto.CarrinhoResponseDTO;
import com.lourenco.backend.dto.PedidoDTO;
import com.lourenco.backend.dto.PedidoMapper;
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
    private final CarrinhoService carrinhoService;

    public PedidoService(PedidoRepository pedidoRepository,
                         UsuarioRepository usuarioRepository,
                         ProdutoRepository produtoRepository,
                         NotificacaoService notificacaoService,
                         CarrinhoService carrinhoService) {

        this.pedidoRepository = pedidoRepository;
        this.usuarioRepository = usuarioRepository;
        this.produtoRepository = produtoRepository;
        this.notificacaoService = notificacaoService;
        this.carrinhoService = carrinhoService;
    }

    // 🔹 LISTAR TODOS → Agora retorna DTO
    public List<PedidoDTO> listarTodos() {
        return pedidoRepository.findAll().stream()
                .map(PedidoMapper::toDTO)
                .collect(Collectors.toList());
    }

    // 🔹 LISTAR POR USUÁRIO → Agora retorna DTO
    public List<PedidoDTO> listarPorUsuario(UUID usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        return pedidoRepository.findByUsuarioOrderByDataPedidoDesc(usuario).stream()
                .map(PedidoMapper::toDTO)
                .collect(Collectors.toList());
    }

    // 🔹 LISTAR POR STATUS (se precisar)
    public List<PedidoDTO> listarPorStatus(StatusPedido status) {
        return pedidoRepository.findByStatus(status).stream()
                .map(PedidoMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public Pedido criarPedido(Pedido pedido) {

        Usuario usuario = usuarioRepository.findById(pedido.getUsuario().getId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        pedido.setUsuario(usuario);
        pedido.setDataPedido(LocalDateTime.now());
        pedido.setStatus(StatusPedido.PENDENTE);

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

        Pedido pedidoSalvo = pedidoRepository.save(pedido);

        notificacaoService.notificarAdminsNovoPedido(pedidoSalvo);

        return pedidoSalvo;
    }

    // 🔹 PEDIDO DO CARRINHO
    @Transactional
    public Pedido criarPedidoDoCarrinho(UUID usuarioId, String enderecoEntrega, String observacoes) {

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        CarrinhoResponseDTO carrinhoDTO = carrinhoService.obterCarrinho(usuarioId);

        if (carrinhoDTO.getItens().isEmpty()) {
            throw new RuntimeException("Carrinho está vazio");
        }

        Pedido pedido = Pedido.builder()
                .usuario(usuario)
                .enderecoEntrega(enderecoEntrega)
                .observacoes(observacoes)
                .dataPedido(LocalDateTime.now())
                .status(StatusPedido.PENDENTE)
                .build();

        List<ItemPedido> itensPedido = new ArrayList<>();
        double total = 0.0;

        for (CarrinhoResponseDTO.ItemCarrinhoDTO itemDTO : carrinhoDTO.getItens()) {

            Produto produto = produtoRepository.findById(itemDTO.getProdutoId())
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

            ItemPedido itemPedido = ItemPedido.builder()
                    .pedido(pedido)
                    .produto(produto)
                    .quantidade(itemDTO.getQuantidade())
                    .precoUnitario(itemDTO.getPrecoUnitario())
                    .build();

            itensPedido.add(itemPedido);
            total += itemPedido.getSubtotal();
        }

        pedido.setItens(itensPedido);
        pedido.setValorTotal(total);

        Pedido pedidoSalvo = pedidoRepository.save(pedido);

        carrinhoService.finalizarCarrinho(usuarioId);

        notificacaoService.notificarAdminsNovoPedido(pedidoSalvo);

        return pedidoSalvo;
    }

    public Pedido buscarPorId(UUID id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));
    }

    public Pedido atualizarStatus(UUID id, StatusPedido novoStatus) {
        Pedido pedido = buscarPorId(id);
        pedido.setStatus(novoStatus);

        Pedido pedidoAtualizado = pedidoRepository.save(pedido);

        notificacaoService.notificarUsuarioStatusPedido(pedidoAtualizado, novoStatus);

        return pedidoAtualizado;
    }

    public void deletar(UUID id) {
        pedidoRepository.deleteById(id);
    }

    public Page<Pedido> listarTodosPaginado(int page, int size, String sortBy, String direction) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc")
                ? Sort.Direction.DESC : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        return pedidoRepository.findAll(pageable);
    }
}