package com.lourenco.backend.service;

import com.lourenco.backend.model.*;
import com.lourenco.backend.repository.NotificacaoRepository;
import com.lourenco.backend.repository.UsuarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class NotificacaoService {

    private final NotificacaoRepository notificacaoRepository;
    private final UsuarioRepository usuarioRepository;

    public NotificacaoService(NotificacaoRepository notificacaoRepository,
                             UsuarioRepository usuarioRepository) {
        this.notificacaoRepository = notificacaoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // Criar notificação
    public Notificacao criarNotificacao(Usuario usuario, String titulo, String mensagem, 
                                       TipoNotificacao tipo, UUID pedidoId, UUID encomendaId) {
        Notificacao notificacao = Notificacao.builder()
                .usuario(usuario)
                .titulo(titulo)
                .mensagem(mensagem)
                .tipo(tipo)
                .pedidoId(pedidoId)
                .encomendaId(encomendaId)
                .lida(false)
                .build();

        return notificacaoRepository.save(notificacao);
    }

    // Notificar admins sobre novo pedido
    public void notificarAdminsNovoPedido(Pedido pedido) {
        List<Usuario> admins = usuarioRepository.findAll().stream()
                .filter(u -> "ADMIN".equals(u.getRole()))
                .toList();

        for (Usuario admin : admins) {
            criarNotificacao(
                admin,
                "Novo Pedido Recebido",
                String.format("Novo pedido #%s de %s no valor de R$ %.2f", 
                    pedido.getId().toString().substring(0, 8),
                    pedido.getUsuario().getNome(),
                    pedido.getValorTotal()),
                TipoNotificacao.NOVO_PEDIDO,
                pedido.getId(),
                null
            );
        }
    }

    // Notificar usuário sobre mudança de status do pedido
    public void notificarUsuarioStatusPedido(Pedido pedido, StatusPedido novoStatus) {
        String titulo = "";
        String mensagem = "";
        TipoNotificacao tipo = TipoNotificacao.SISTEMA;

        switch (novoStatus) {
            case CONFIRMADO:
                titulo = "Pedido Confirmado";
                mensagem = String.format("Seu pedido #%s foi confirmado e está sendo preparado!", 
                    pedido.getId().toString().substring(0, 8));
                tipo = TipoNotificacao.PEDIDO_CONFIRMADO;
                break;
            case EM_PREPARACAO:
                titulo = "Pedido em Preparação";
                mensagem = String.format("Seu pedido #%s está sendo preparado com carinho!", 
                    pedido.getId().toString().substring(0, 8));
                tipo = TipoNotificacao.PEDIDO_EM_PREPARACAO;
                break;
            case PRONTO:
                titulo = "Pedido Pronto";
                mensagem = String.format("Seu pedido #%s está pronto para retirada/entrega!", 
                    pedido.getId().toString().substring(0, 8));
                tipo = TipoNotificacao.PEDIDO_PRONTO;
                break;
            case ENTREGUE:
                titulo = "Pedido Entregue";
                mensagem = String.format("Seu pedido #%s foi entregue. Obrigado pela preferência!", 
                    pedido.getId().toString().substring(0, 8));
                tipo = TipoNotificacao.PEDIDO_ENTREGUE;
                break;
            case CANCELADO:
                titulo = "Pedido Cancelado";
                mensagem = String.format("Seu pedido #%s foi cancelado.", 
                    pedido.getId().toString().substring(0, 8));
                tipo = TipoNotificacao.PEDIDO_CANCELADO;
                break;
        }

        if (!titulo.isEmpty()) {
            criarNotificacao(
                pedido.getUsuario(),
                titulo,
                mensagem,
                tipo,
                pedido.getId(),
                null
            );
        }
    }

    // Notificar admins sobre nova encomenda
    public void notificarAdminsNovaEncomenda(Encomenda encomenda) {
        List<Usuario> admins = usuarioRepository.findAll().stream()
                .filter(u -> "ADMIN".equals(u.getRole()))
                .toList();

        for (Usuario admin : admins) {
            criarNotificacao(
                admin,
                "Nova Encomenda Recebida",
                String.format("Nova encomenda de %s: %s para %s", 
                    encomenda.getUsuario().getNome(),
                    encomenda.getTipoProduto(),
                    encomenda.getDataEntrega()),
                TipoNotificacao.NOVA_ENCOMENDA,
                null,
                encomenda.getId()
            );
        }
    }

    // Notificar usuário sobre mudança de status da encomenda
    public void notificarUsuarioStatusEncomenda(Encomenda encomenda, StatusEncomenda novoStatus) {
        String titulo = "";
        String mensagem = "";
        TipoNotificacao tipo = TipoNotificacao.SISTEMA;

        switch (novoStatus) {
            case ORCAMENTO_ENVIADO:
                titulo = "Orçamento Enviado";
                mensagem = String.format("O orçamento da sua encomenda (%s) foi enviado!", 
                    encomenda.getTipoProduto());
                tipo = TipoNotificacao.ENCOMENDA_ORCAMENTO_ENVIADO;
                break;
            case APROVADO:
                titulo = "Encomenda Aprovada";
                mensagem = String.format("Sua encomenda (%s) foi aprovada e entrará em produção!", 
                    encomenda.getTipoProduto());
                tipo = TipoNotificacao.ENCOMENDA_APROVADA;
                break;
            case EM_PRODUCAO:
                titulo = "Encomenda em Produção";
                mensagem = String.format("Sua encomenda (%s) está sendo produzida!", 
                    encomenda.getTipoProduto());
                tipo = TipoNotificacao.ENCOMENDA_EM_PRODUCAO;
                break;
            case PRONTO:
                titulo = "Encomenda Pronta";
                mensagem = String.format("Sua encomenda (%s) está pronta!", 
                    encomenda.getTipoProduto());
                tipo = TipoNotificacao.ENCOMENDA_PRONTA;
                break;
            case ENTREGUE:
                titulo = "Encomenda Entregue";
                mensagem = String.format("Sua encomenda (%s) foi entregue. Obrigado!", 
                    encomenda.getTipoProduto());
                tipo = TipoNotificacao.ENCOMENDA_ENTREGUE;
                break;
        }

        if (!titulo.isEmpty()) {
            criarNotificacao(
                encomenda.getUsuario(),
                titulo,
                mensagem,
                tipo,
                null,
                encomenda.getId()
            );
        }
    }

    // Listar notificações do usuário
    public List<Notificacao> listarNotificacoesUsuario(UUID usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        return notificacaoRepository.findByUsuarioOrderByDataHoraDesc(usuario);
    }

    // Listar notificações do usuário paginado
    public Page<Notificacao> listarNotificacoesUsuarioPaginado(UUID usuarioId, int page, int size) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dataHora"));
        return notificacaoRepository.findByUsuarioOrderByDataHoraDesc(usuario, pageable);
    }

    // Listar notificações não lidas
    public List<Notificacao> listarNotificacoesNaoLidas(UUID usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        return notificacaoRepository.findByUsuarioAndLidaFalseOrderByDataHoraDesc(usuario);
    }

    // Contar notificações não lidas
    public Long contarNotificacoesNaoLidas(UUID usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        return notificacaoRepository.countByUsuarioAndLidaFalse(usuario);
    }

    // Marcar notificação como lida
    public Notificacao marcarComoLida(UUID notificacaoId) {
        Notificacao notificacao = notificacaoRepository.findById(notificacaoId)
                .orElseThrow(() -> new RuntimeException("Notificação não encontrada"));
        notificacao.setLida(true);
        return notificacaoRepository.save(notificacao);
    }

    // Marcar todas como lidas
    public void marcarTodasComoLidas(UUID usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        
        List<Notificacao> naoLidas = notificacaoRepository.findByUsuarioAndLidaFalseOrderByDataHoraDesc(usuario);
        
        for (Notificacao notificacao : naoLidas) {
            notificacao.setLida(true);
        }
        
        notificacaoRepository.saveAll(naoLidas);
    }

    // Deletar notificação
    public void deletar(UUID notificacaoId) {
        notificacaoRepository.deleteById(notificacaoId);
    }
}
