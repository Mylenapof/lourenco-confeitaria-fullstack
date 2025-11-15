package com.lourenco.backend.controller;

import com.lourenco.backend.model.Notificacao;
import com.lourenco.backend.service.NotificacaoService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/notificacoes")
@CrossOrigin(origins = "*")
public class NotificacaoController {

    private final NotificacaoService notificacaoService;

    public NotificacaoController(NotificacaoService notificacaoService) {
        this.notificacaoService = notificacaoService;
    }

    @GetMapping("/usuario/{usuarioId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<Notificacao> listarNotificacoesUsuario(@PathVariable UUID usuarioId) {
        return notificacaoService.listarNotificacoesUsuario(usuarioId);
    }

    @GetMapping("/usuario/{usuarioId}/page")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public Page<Notificacao> listarNotificacoesUsuarioPaginado(
            @PathVariable UUID usuarioId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return notificacaoService.listarNotificacoesUsuarioPaginado(usuarioId, page, size);
    }

    @GetMapping("/usuario/{usuarioId}/nao-lidas")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<Notificacao> listarNotificacoesNaoLidas(@PathVariable UUID usuarioId) {
        return notificacaoService.listarNotificacoesNaoLidas(usuarioId);
    }

    @GetMapping("/usuario/{usuarioId}/contador")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public Long contarNotificacoesNaoLidas(@PathVariable UUID usuarioId) {
        return notificacaoService.contarNotificacoesNaoLidas(usuarioId);
    }

    @PatchMapping("/{id}/marcar-lida")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public Notificacao marcarComoLida(@PathVariable UUID id) {
        return notificacaoService.marcarComoLida(id);
    }

    @PatchMapping("/usuario/{usuarioId}/marcar-todas-lidas")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Void> marcarTodasComoLidas(@PathVariable UUID usuarioId) {
        notificacaoService.marcarTodasComoLidas(usuarioId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        notificacaoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
