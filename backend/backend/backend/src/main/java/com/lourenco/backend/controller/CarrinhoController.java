package com.lourenco.backend.controller;

import com.lourenco.backend.dto.ItemCarrinhoDTO;
import com.lourenco.backend.dto.AtualizarItemCarrinhoDTO;
import com.lourenco.backend.dto.CarrinhoResponseDTO;
import com.lourenco.backend.service.CarrinhoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/carrinho")
@CrossOrigin(origins = "*")
public class CarrinhoController {

    private final CarrinhoService carrinhoService;

    public CarrinhoController(CarrinhoService carrinhoService) {
        this.carrinhoService = carrinhoService;
    }

    @GetMapping("/usuario/{usuarioId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public CarrinhoResponseDTO obterCarrinho(@PathVariable UUID usuarioId) {
        return carrinhoService.obterCarrinho(usuarioId);
    }

    @PostMapping("/usuario/{usuarioId}/item")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public CarrinhoResponseDTO adicionarItem(
            @PathVariable UUID usuarioId,
            @RequestBody ItemCarrinhoDTO dto) {
        return carrinhoService.adicionarItem(usuarioId, dto);
    }

    @PutMapping("/usuario/{usuarioId}/item/{itemId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public CarrinhoResponseDTO atualizarItem(
            @PathVariable UUID usuarioId,
            @PathVariable UUID itemId,
            @RequestBody AtualizarItemCarrinhoDTO dto) {
        return carrinhoService.atualizarItem(usuarioId, itemId, dto);
    }

    @DeleteMapping("/usuario/{usuarioId}/item/{itemId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public CarrinhoResponseDTO removerItem(
            @PathVariable UUID usuarioId,
            @PathVariable UUID itemId) {
        return carrinhoService.removerItem(usuarioId, itemId);
    }

    @DeleteMapping("/usuario/{usuarioId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Void> limparCarrinho(@PathVariable UUID usuarioId) {
        carrinhoService.limparCarrinho(usuarioId);
        return ResponseEntity.noContent().build();
    }
}
