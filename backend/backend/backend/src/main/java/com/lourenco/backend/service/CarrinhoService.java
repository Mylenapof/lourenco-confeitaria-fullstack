package com.lourenco.backend.service;

import com.lourenco.backend.dto.ItemCarrinhoDTO;
import com.lourenco.backend.dto.AtualizarItemCarrinhoDTO;
import com.lourenco.backend.dto.CarrinhoResponseDTO;
import com.lourenco.backend.model.*;
import com.lourenco.backend.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CarrinhoService {

    private final CarrinhoRepository carrinhoRepository;
    private final ItemCarrinhoRepository itemCarrinhoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProdutoRepository produtoRepository;

    public CarrinhoService(CarrinhoRepository carrinhoRepository,
                          ItemCarrinhoRepository itemCarrinhoRepository,
                          UsuarioRepository usuarioRepository,
                          ProdutoRepository produtoRepository) {
        this.carrinhoRepository = carrinhoRepository;
        this.itemCarrinhoRepository = itemCarrinhoRepository;
        this.usuarioRepository = usuarioRepository;
        this.produtoRepository = produtoRepository;
    }

    @Transactional
    public CarrinhoResponseDTO obterCarrinho(UUID usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Carrinho carrinho = carrinhoRepository.findByUsuario(usuario)
                .orElseGet(() -> criarCarrinhoVazio(usuario));

        return converterParaDTO(carrinho);
    }

    private Carrinho criarCarrinhoVazio(Usuario usuario) {
        Carrinho carrinho = Carrinho.builder()
                .usuario(usuario)
                .build();
        return carrinhoRepository.save(carrinho);
    }

    @Transactional
    public CarrinhoResponseDTO adicionarItem(UUID usuarioId, ItemCarrinhoDTO dto) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Produto produto = produtoRepository.findById(dto.getProdutoId())
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        if (!produto.getDisponivel()) {
            throw new RuntimeException("Produto não disponível");
        }

        Carrinho carrinho = carrinhoRepository.findByUsuario(usuario)
                .orElseGet(() -> criarCarrinhoVazio(usuario));

        // Verifica se o item já existe no carrinho
        ItemCarrinho itemExistente = carrinho.getItens().stream()
                .filter(item -> item.getProduto().getId().equals(dto.getProdutoId()))
                .findFirst()
                .orElse(null);

        if (itemExistente != null) {
            // Atualiza quantidade
            itemExistente.setQuantidade(itemExistente.getQuantidade() + dto.getQuantidade());
            itemCarrinhoRepository.save(itemExistente);
        } else {
            // Adiciona novo item
            ItemCarrinho novoItem = ItemCarrinho.builder()
                    .carrinho(carrinho)
                    .produto(produto)
                    .quantidade(dto.getQuantidade())
                    .precoUnitario(produto.getPreco())
                    .build();
            carrinho.getItens().add(novoItem);
            itemCarrinhoRepository.save(novoItem);
        }

        carrinho.setDataAtualizacao(LocalDateTime.now());
        carrinhoRepository.save(carrinho);

        return converterParaDTO(carrinho);
    }

    @Transactional
    public CarrinhoResponseDTO atualizarItem(UUID usuarioId, UUID itemId, AtualizarItemCarrinhoDTO dto) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Carrinho carrinho = carrinhoRepository.findByUsuario(usuario)
                .orElseThrow(() -> new RuntimeException("Carrinho não encontrado"));

        ItemCarrinho item = carrinho.getItens().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Item não encontrado no carrinho"));

        if (dto.getQuantidade() <= 0) {
            throw new RuntimeException("Quantidade deve ser maior que zero");
        }

        item.setQuantidade(dto.getQuantidade());
        itemCarrinhoRepository.save(item);

        carrinho.setDataAtualizacao(LocalDateTime.now());
        carrinhoRepository.save(carrinho);

        return converterParaDTO(carrinho);
    }

    @Transactional
    public CarrinhoResponseDTO removerItem(UUID usuarioId, UUID itemId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Carrinho carrinho = carrinhoRepository.findByUsuario(usuario)
                .orElseThrow(() -> new RuntimeException("Carrinho não encontrado"));

        ItemCarrinho item = carrinho.getItens().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Item não encontrado no carrinho"));

        carrinho.getItens().remove(item);
        itemCarrinhoRepository.delete(item);

        carrinho.setDataAtualizacao(LocalDateTime.now());
        carrinhoRepository.save(carrinho);

        return converterParaDTO(carrinho);
    }

    @Transactional
    public void limparCarrinho(UUID usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Carrinho carrinho = carrinhoRepository.findByUsuario(usuario)
                .orElseThrow(() -> new RuntimeException("Carrinho não encontrado"));

        carrinho.getItens().clear();
        carrinho.setDataAtualizacao(LocalDateTime.now());
        carrinhoRepository.save(carrinho);
    }

    @Transactional
    public void finalizarCarrinho(UUID usuarioId) {
        // Limpa o carrinho após finalizar o pedido
        limparCarrinho(usuarioId);
    }

    private CarrinhoResponseDTO converterParaDTO(Carrinho carrinho) {
        List<CarrinhoResponseDTO.ItemCarrinhoDTO> itensDTO = carrinho.getItens().stream()
                .map(item -> CarrinhoResponseDTO.ItemCarrinhoDTO.builder()
                        .itemId(item.getId())
                        .produtoId(item.getProduto().getId())
                        .produtoNome(item.getProduto().getNome())
                        .produtoImagemUrl(item.getProduto().getImagemUrl())
                        .precoUnitario(item.getPrecoUnitario())
                        .quantidade(item.getQuantidade())
                        .subtotal(item.getSubtotal())
                        .produtoDisponivel(item.getProduto().getDisponivel())
                        .build())
                .collect(Collectors.toList());

        return CarrinhoResponseDTO.builder()
                .carrinhoId(carrinho.getId())
                .usuarioId(carrinho.getUsuario().getId())
                .itens(itensDTO)
                .totalItens(carrinho.contarItens())
                .valorTotal(carrinho.calcularTotal())
                .dataAtualizacao(carrinho.getDataAtualizacao())
                .build();
    }
}