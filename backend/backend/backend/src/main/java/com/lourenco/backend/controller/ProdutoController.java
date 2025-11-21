package com.lourenco.backend.controller;

import com.lourenco.backend.dto.ProdutoDTO;
import com.lourenco.backend.model.Produto;
import com.lourenco.backend.service.ProdutoService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

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
    public List listar() {
        return produtoService.listarTodos();
    }
    
    @GetMapping("/disponiveis")
    public List listarDisponiveis() {
        return produtoService.listarDisponiveis();
    }
    
    @GetMapping("/destaques")
    public List listarDestaques() {
        return produtoService.listarDestaques();
    }

    // NOVAS ROTAS COM PAGINAÇÃO
    @GetMapping("/page")
    public Page listarPaginado(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "nome") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
        return produtoService.listarTodosPaginado(page, size, sortBy, direction);
    }
    
    @GetMapping("/disponiveis/page")
    public Page listarDisponiveisPaginado(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "nome") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
        return produtoService.listarDisponiveisPaginado(page, size, sortBy, direction);
    }
    
    @GetMapping("/destaques/page")
    public Page listarDestaquesPaginado(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "nome") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
        return produtoService.listarDestaquesPaginado(page, size, sortBy, direction);
    }
    
    @GetMapping("/categoria/{categoriaId}/page")
    public Page listarPorCategoriaPaginado(
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

    // ✅ ATUALIZADO COM @Valid E DTO
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Produto salvar(@Valid @RequestBody ProdutoDTO dto) {
        return produtoService.salvarComDTO(dto);
    }

    // ✅ ATUALIZADO COM @Valid E DTO
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Produto atualizar(@PathVariable UUID id, @Valid @RequestBody ProdutoDTO dto) {
        return produtoService.atualizarComDTO(id, dto);
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
    public ResponseEntity deletar(@PathVariable UUID id) {
        produtoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}