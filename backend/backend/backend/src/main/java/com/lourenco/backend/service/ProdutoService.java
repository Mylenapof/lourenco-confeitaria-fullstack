package com.lourenco.backend.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.lourenco.backend.dto.ProdutoDTO;
import com.lourenco.backend.model.Categoria;
import com.lourenco.backend.model.Produto;
import com.lourenco.backend.repository.CategoriaRepository;
import com.lourenco.backend.repository.ProdutoRepository;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final CategoriaRepository categoriaRepository;

    public ProdutoService(ProdutoRepository produtoRepository, 
                         CategoriaRepository categoriaRepository) {
        this.produtoRepository = produtoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    // MÉTODOS SEM PAGINAÇÃO (mantém os existentes)
    public List listarTodos() {
        return produtoRepository.findAll();
    }
    
    public List listarDisponiveis() {
        return produtoRepository.findByDisponivelTrue();
    }
    
    public List listarDestaques() {
        return produtoRepository.findByDestaqueTrue();
    }

    // NOVOS MÉTODOS COM PAGINAÇÃO
    public Page listarTodosPaginado(int page, int size, String sortBy, String direction) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        return produtoRepository.findAll(pageable);
    }
    
    public Page listarDisponiveisPaginado(int page, int size, String sortBy, String direction) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        return produtoRepository.findByDisponivelTrue(pageable);
    }
    
    public Page listarDestaquesPaginado(int page, int size, String sortBy, String direction) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        return produtoRepository.findByDestaqueTrue(pageable);
    }
    
    public Page listarPorCategoriaPaginado(UUID categoriaId, int page, int size, String sortBy, String direction) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        return produtoRepository.findByCategoriaId(categoriaId, pageable);
    }

    // ✅ NOVO: Salvar com DTO validado
    public Produto salvarComDTO(ProdutoDTO dto) {
        Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));
        
        Produto produto = new Produto();
        produto.setNome(dto.getNome());
        produto.setPreco(dto.getPreco());
        produto.setIngredientes(dto.getIngredientes());
        produto.setImagemUrl(dto.getImagemUrl());
        produto.setCategoria(categoria);
        produto.setDisponivel(dto.getDisponivel() != null ? dto.getDisponivel() : true);
        produto.setDestaque(dto.getDestaque() != null ? dto.getDestaque() : false);
        
        return produtoRepository.save(produto);
    }

    // ✅ NOVO: Atualizar com DTO validado
    public Produto atualizarComDTO(UUID id, ProdutoDTO dto) {
        Produto produto = buscarPorId(id);
        
        Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));
        
        produto.setNome(dto.getNome());
        produto.setPreco(dto.getPreco());
        produto.setIngredientes(dto.getIngredientes());
        produto.setImagemUrl(dto.getImagemUrl());
        produto.setCategoria(categoria);
        produto.setDisponivel(dto.getDisponivel() != null ? dto.getDisponivel() : produto.getDisponivel());
        produto.setDestaque(dto.getDestaque() != null ? dto.getDestaque() : produto.getDestaque());
        
        return produtoRepository.save(produto);
    }

    // MÉTODOS EXISTENTES (mantém todos)
    public Produto salvar(Produto produto) {
        if (produto.getCategoria() != null && produto.getCategoria().getId() != null) {
            Categoria categoriaCompleta = categoriaRepository.findById(produto.getCategoria().getId())
                    .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));
            produto.setCategoria(categoriaCompleta);
        }
        
        if (produto.getDisponivel() == null) {
            produto.setDisponivel(true);
        }
        if (produto.getDestaque() == null) {
            produto.setDestaque(false);
        }
        
        return produtoRepository.save(produto);
    }

    public Produto buscarPorId(UUID id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));
    }
    
    public Produto atualizar(UUID id, Produto produtoAtualizado) {
        Produto produto = buscarPorId(id);
        
        produto.setNome(produtoAtualizado.getNome());
        produto.setPreco(produtoAtualizado.getPreco());
        produto.setIngredientes(produtoAtualizado.getIngredientes());
        produto.setImagemUrl(produtoAtualizado.getImagemUrl());
        produto.setDisponivel(produtoAtualizado.getDisponivel());
        produto.setDestaque(produtoAtualizado.getDestaque());
        
        if (produtoAtualizado.getCategoria() != null) {
            Categoria categoria = categoriaRepository.findById(produtoAtualizado.getCategoria().getId())
                    .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));
            produto.setCategoria(categoria);
        }
        
        return produtoRepository.save(produto);
    }
    
    public Produto toggleDestaque(UUID id) {
        Produto produto = buscarPorId(id);
        produto.setDestaque(!produto.getDestaque());
        return produtoRepository.save(produto);
    }
    
    public Produto toggleDisponibilidade(UUID id) {
        Produto produto = buscarPorId(id);
        produto.setDisponivel(!produto.getDisponivel());
        return produtoRepository.save(produto);
    }

    public void deletar(UUID id) {
        produtoRepository.deleteById(id);
    }
}