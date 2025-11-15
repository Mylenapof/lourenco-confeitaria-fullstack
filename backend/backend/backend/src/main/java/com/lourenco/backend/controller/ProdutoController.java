package com.lourenco.backend.controller;
import java.util.List;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    // ROTAS SEM PAGINAÇÃO (mantém as existentes)
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

    // NOVAS ROTAS COM PAGINAÇÃO
    @GetMapping("/page")
    public Page<Produto> listarPaginado(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "nome") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
        return produtoService.listarTodosPaginado(page, size, sortBy, direction);
    }
    
    @GetMapping("/disponiveis/page")
    public Page<Produto> listarDisponiveisPaginado(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "nome") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
        return produtoService.listarDisponiveisPaginado(page, size, sortBy, direction);
    }
    
    @GetMapping("/destaques/page")
    public Page<Produto> listarDestaquesPaginado(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "nome") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
        return produtoService.listarDestaquesPaginado(page, size, sortBy, direction);
    }
    
    @GetMapping("/categoria/{categoriaId}/page")
    public Page<Produto> listarPorCategoriaPaginado(
            @PathVariable UUID categoriaId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "nome") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
        return produtoService.listarPorCategoriaPaginado(categoriaId, page, size, sortBy, direction);
    }

    // ROTAS EXISTENTES (mantém todas)
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