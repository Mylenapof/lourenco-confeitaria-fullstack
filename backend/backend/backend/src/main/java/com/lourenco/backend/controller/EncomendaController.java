package com.lourenco.backend.controller;

import java.util.List;
import java.util.Map;
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

import com.lourenco.backend.model.Encomenda;
import com.lourenco.backend.model.StatusEncomenda;
import com.lourenco.backend.service.EncomendaService;

@RestController
@RequestMapping("/encomendas")
@CrossOrigin(origins = "*")
public class EncomendaController {

    private final EncomendaService encomendaService;

    public EncomendaController(EncomendaService encomendaService) {
        this.encomendaService = encomendaService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<Encomenda> listarTodas() {
        return encomendaService.listarTodas();
    }

    @GetMapping("/usuario/{usuarioId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<Encomenda> listarPorUsuario(@PathVariable UUID usuarioId) {
        return encomendaService.listarPorUsuario(usuarioId);
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Encomenda> listarPorStatus(@PathVariable StatusEncomenda status) {
        return encomendaService.listarPorStatus(status);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public Encomenda buscar(@PathVariable UUID id) {
        return encomendaService.buscarPorId(id);
    }

    @PostMapping
    public Encomenda criar(@RequestBody Encomenda encomenda) {
        return encomendaService.criar(encomenda);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Encomenda atualizar(@PathVariable UUID id, @RequestBody Encomenda encomenda) {
        return encomendaService.atualizar(id, encomenda);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public Encomenda atualizarStatus(@PathVariable UUID id, @RequestBody Map<String, String> body) {
        StatusEncomenda novoStatus = StatusEncomenda.valueOf(body.get("status"));
        return encomendaService.atualizarStatus(id, novoStatus);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        encomendaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}