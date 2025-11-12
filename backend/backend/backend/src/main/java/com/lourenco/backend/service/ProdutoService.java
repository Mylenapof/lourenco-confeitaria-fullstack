package com.lourenco.backend.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

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

    public List<Produto> listarTodos() {
        return produtoRepository.findAll();
    }
    
    public List<Produto> listarDisponiveis() {
        return produtoRepository.findByDisponivelTrue();
    }
    
    public List<Produto> listarDestaques() {
        return produtoRepository.findByDestaqueTrue();
    }

    public Produto salvar(Produto produto) {
        if (produto.getCategoria() != null && produto.getCategoria().getId() != null) {
            Categoria categoriaCompleta = categoriaRepository.findById(produto.getCategoria().getId())
                    .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));
            produto.setCategoria(categoriaCompleta);
        }
        
        // Valores padrão
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