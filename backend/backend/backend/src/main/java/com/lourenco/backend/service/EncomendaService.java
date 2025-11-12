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

    public EncomendaService(EncomendaRepository encomendaRepository,
                           UsuarioRepository usuarioRepository) {
        this.encomendaRepository = encomendaRepository;
        this.usuarioRepository = usuarioRepository;
    }

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
        
        return encomendaRepository.save(encomenda);
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
        return encomendaRepository.save(encomenda);
    }

    public void deletar(UUID id) {
        encomendaRepository.deleteById(id);
    }
}