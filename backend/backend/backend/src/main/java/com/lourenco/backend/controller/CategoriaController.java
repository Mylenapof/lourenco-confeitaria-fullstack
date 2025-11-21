package com.lourenco.backend.controller;
import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lourenco.backend.dto.CategoriaDTO;
import com.lourenco.backend.model.Categoria;
import com.lourenco.backend.service.CategoriaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/categorias")
@CrossOrigin(origins = "*")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @GetMapping
    public List<Categoria> listar() {
        return categoriaService.listarTodos();
    }

    // --- DEIXE APENAS ESTE MÉTODO @PostMapping (O QUE USA DTO) ---
    @PostMapping
    public Categoria salvar(@Valid @RequestBody CategoriaDTO dto) {
        Categoria categoria = new Categoria();
        categoria.setNome(dto.getNome());
        return categoriaService.salvar(categoria);
    }
    // -------------------------------------------------------------

    @GetMapping("/{id}")
    public Categoria buscar(@PathVariable UUID id) {
        return categoriaService.buscarPorId(id);
    }
}