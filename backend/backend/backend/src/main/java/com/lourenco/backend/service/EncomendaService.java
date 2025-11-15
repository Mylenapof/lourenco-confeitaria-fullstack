package com.lourenco.backend.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.lourenco.backend.model.Encomenda;
import com.lourenco.backend.model.StatusEncomenda;
import com.lourenco.backend.model.Usuario;
import com.lourenco.backend.repository.EncomendaRepository;
import com.lourenco.backend.repository.UsuarioRepository;

@Service
public class EncomendaService {

    private final EncomendaRepository encomendaRepository;
    private final UsuarioRepository usuarioRepository;
    // --- INÍCIO DA MUDANÇA ---
    private final NotificacaoService notificacaoService;

    public EncomendaService(EncomendaRepository encomendaRepository,
                           UsuarioRepository usuarioRepository,
                           NotificacaoService notificacaoService) { // ADICIONE
        this.encomendaRepository = encomendaRepository;
        this.usuarioRepository = usuarioRepository;
        this.notificacaoService = notificacaoService; // ADICIONE
    }
    // --- FIM DA MUDANÇA ---

    public List<Encomenda> listarTodas() {
        return encomendaRepository.findAll();
    }

    public List<Encomenda> listarPorUsuario(UUID usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        return encomendaRepository.findByUsuarioOrderByDataEntregaDesc(usuario);
    }

    public List<Encomenda> listarPorStatus(StatusEncomenda status) {
        return encomendaRepository.findByStatus(status);
    }

    public Encomenda criar(Encomenda encomenda) {
        Usuario usuario = usuarioRepository.findById(encomenda.getUsuario().getId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        
        encomenda.setUsuario(usuario);
        encomenda.setStatus(StatusEncomenda.PENDENTE);
        
        // --- INÍCIO DA MUDANÇA ---
        Encomenda encomendaSalva = encomendaRepository.save(encomenda);
        
        // ADICIONE: Notificar admins
        notificacaoService.notificarAdminsNovaEncomenda(encomendaSalva);
            
        return encomendaSalva;
        // --- FIM DA MUDANÇA ---
    }

    public Encomenda buscarPorId(UUID id) {
        return encomendaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Encomenda não encontrada"));
    }

    public Encomenda atualizar(UUID id, Encomenda encomendaAtualizada) {
        Encomenda encomenda = buscarPorId(id);
        
        encomenda.setTipoProduto(encomendaAtualizada.getTipoProduto());
        encomenda.setTamanho(encomendaAtualizada.getTamanho());
        encomenda.setSabor(encomendaAtualizada.getSabor());
        encomenda.setDecoracao(encomendaAtualizada.getDecoracao());
        encomenda.setObservacoes(encomendaAtualizada.getObservacoes());
        encomenda.setDataEntrega(encomendaAtualizada.getDataEntrega());
        encomenda.setValorEstimado(encomendaAtualizada.getValorEstimado());
        
        return encomendaRepository.save(encomenda);
    }

    public Encomenda atualizarStatus(UUID id, StatusEncomenda novoStatus) {
        Encomenda encomenda = buscarPorId(id);
        encomenda.setStatus(novoStatus);
        
        // --- INÍCIO DA MUDANÇA ---
        Encomenda encomendaAtualizada = encomendaRepository.save(encomenda);
        
        // ADICIONE: Notificar usuário
        notificacaoService.notificarUsuarioStatusEncomenda(encomendaAtualizada, novoStatus);
            
        return encomendaAtualizada;
        // --- FIM DA MUDANÇA ---
    }

    public void deletar(UUID id) {
        encomendaRepository.deleteById(id);
    }
}