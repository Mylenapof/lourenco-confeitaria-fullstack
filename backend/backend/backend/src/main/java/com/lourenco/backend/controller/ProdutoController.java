package com.lourenco.backend.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lourenco.backend.model.Produto;
import com.lourenco.backend.service.ProdutoService;

@RestController
@RequestMapping("/produtos")
@CrossOrigin(origins = "*")
public class ProdutoController {

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @GetMapping
    public List<Produto> listar() {
        return produtoService.listarTodos();
    }
    
    @GetMapping("/disponiveis")
    public List<Produto> listarDisponiveis() {
        return produtoService.listarDisponiveis();
    }
    
    @GetMapping("/destaques")
    public List<Produto> listarDestaques() {
        return produtoService.listarDestaques();
    }

    @GetMapping("/{id}")
    public Produto buscar(@PathVariable UUID id) {
        return produtoService.buscarPorId(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Produto salvar(@RequestBody Produto produto) {
        return produtoService.salvar(produto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Produto atualizar(@PathVariable UUID id, @RequestBody Produto produto) {
        return produtoService.atualizar(id, produto);
    }
    
    @PatchMapping("/{id}/destaque")
    @PreAuthorize("hasRole('ADMIN')")
    public Produto toggleDestaque(@PathVariable UUID id) {
        return produtoService.toggleDestaque(id);
    }
    
    @PatchMapping("/{id}/disponibilidade")
    @PreAuthorize("hasRole('ADMIN')")
    public Produto toggleDisponibilidade(@PathVariable UUID id) {
        return produtoService.toggleDisponibilidade(id);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        produtoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}