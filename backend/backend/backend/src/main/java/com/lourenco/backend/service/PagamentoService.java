package com.lourenco.backend.service;

import com.lourenco.backend.dto.PixResponseDTO;
import com.lourenco.backend.model.*;
import com.lourenco.backend.repository.PagamentoRepository;
import com.lourenco.backend.repository.PedidoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
public class PagamentoService {

    private final PagamentoRepository pagamentoRepository;
    private final PedidoRepository pedidoRepository;
    private final NotificacaoService notificacaoService;

    public PagamentoService(PagamentoRepository pagamentoRepository,
                           PedidoRepository pedidoRepository,
                           NotificacaoService notificacaoService) {
        this.pagamentoRepository = pagamentoRepository;
        this.pedidoRepository = pedidoRepository;
        this.notificacaoService = notificacaoService;
    }

    @Transactional
    public PixResponseDTO gerarPagamentoPix(UUID pedidoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));

        // Verifica se já existe pagamento
        if (pagamentoRepository.findByPedidoId(pedidoId).isPresent()) {
            throw new RuntimeException("Pedido já possui pagamento criado");
        }

        // Gera código PIX fictício (Copia e Cola)
        String pixCopiaCola = gerarCodigoPixFicticio(pedido);

        // Gera QR Code fictício em Base64
        String qrCodeBase64 = gerarQrCodeFicticio(pixCopiaCola);

        // Cria o pagamento
        Pagamento pagamento = Pagamento.builder()
                .pedido(pedido)
                .valor(pedido.getValorTotal())
                .status(StatusPagamento.PENDENTE)
                .metodoPagamento("PIX")
                .pixCopiaCola(pixCopiaCola)
                .qrCodeBase64(qrCodeBase64)
                .dataExpiracao(LocalDateTime.now().plusMinutes(30))
                .build();

        Pagamento pagamentoSalvo = pagamentoRepository.save(pagamento);

        // Atualiza status do pedido
        pedido.setStatus(StatusPedido.PENDENTE);
        pedidoRepository.save(pedido);

        return PixResponseDTO.builder()
                .pagamentoId(pagamentoSalvo.getId())
                .pedidoId(pedido.getId())
                .valor(pagamentoSalvo.getValor())
                .status(pagamentoSalvo.getStatus())
                .pixCopiaCola(pagamentoSalvo.getPixCopiaCola())
                .qrCodeBase64(pagamentoSalvo.getQrCodeBase64())
                .dataExpiracao(pagamentoSalvo.getDataExpiracao())
                .dataCriacao(pagamentoSalvo.getDataCriacao())
                .build();
    }

    private String gerarCodigoPixFicticio(Pedido pedido) {
        // Gera um código PIX fictício realista
        String identificador = pedido.getId().toString().replace("-", "").substring(0, 20).toUpperCase();
        
        return String.format(
            "00020126580014BR.GOV.BCB.PIX0136%s520400005303986540%s5802BR5925Lourenco Confeitaria6009SAO PAULO62070503***6304%s",
            identificador,
            String.format("%.2f", pedido.getValorTotal()),
            gerarCRC16(identificador)
        );
    }

    private String gerarCRC16(String data) {
        // Gera um CRC16 fictício (4 caracteres)
        int hash = data.hashCode();
        return String.format("%04X", Math.abs(hash) % 65536);
    }

    private String gerarQrCodeFicticio(String pixCopiaCola) {
        // Gera um SVG de QR Code fictício
        String svgQrCode = String.format(
            "<svg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 200 200'>" +
            "<rect width='200' height='200' fill='white'/>" +
            "<g fill='black'>" +
            "<rect x='20' y='20' width='20' height='20'/>" +
            "<rect x='60' y='20' width='20' height='20'/>" +
            "<rect x='100' y='20' width='20' height='20'/>" +
            "<rect x='140' y='20' width='20' height='20'/>" +
            "<rect x='20' y='60' width='20' height='20'/>" +
            "<rect x='140' y='60' width='20' height='20'/>" +
            "<rect x='20' y='100' width='60' height='20'/>" +
            "<rect x='100' y='100' width='60' height='20'/>" +
            "<rect x='20' y='140' width='20' height='20'/>" +
            "<rect x='60' y='140' width='80' height='20'/>" +
            "<rect x='140' y='140' width='20' height='20'/>" +
            "<text x='100' y='190' text-anchor='middle' font-size='12' fill='gray'>PIX Demonstração</text>" +
            "</g></svg>"
        );

        // Converte SVG para Base64
        return Base64.getEncoder().encodeToString(svgQrCode.getBytes());
    }

    public Pagamento buscarPagamentoPorPedido(UUID pedidoId) {
        return pagamentoRepository.findByPedidoId(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pagamento não encontrado"));
    }

    public Pagamento buscarPagamentoPorId(UUID pagamentoId) {
        return pagamentoRepository.findById(pagamentoId)
                .orElseThrow(() -> new RuntimeException("Pagamento não encontrado"));
    }

    @Transactional
    public Pagamento simularPagamento(UUID pagamentoId) {
        Pagamento pagamento = buscarPagamentoPorId(pagamentoId);

        if (pagamento.getStatus() != StatusPagamento.PENDENTE) {
            throw new RuntimeException("Pagamento já foi processado");
        }

        // Simula aprovação do pagamento
        pagamento.setStatus(StatusPagamento.APROVADO);
        pagamento.setDataAprovacao(LocalDateTime.now());

        // Atualiza o pedido
        Pedido pedido = pagamento.getPedido();
        pedido.setStatus(StatusPedido.CONFIRMADO);
        pedidoRepository.save(pedido);

        // Notifica usuário
        notificacaoService.criarNotificacao(
            pedido.getUsuario(),
            "Pagamento Aprovado! 💚",
            String.format("O pagamento do pedido #%s foi aprovado via PIX. Seu pedido está sendo preparado!", 
                pedido.getId().toString().substring(0, 8)),
            TipoNotificacao.SISTEMA,
            pedido.getId(),
            null
        );

        // Notifica admins
        notificacaoService.notificarAdminsNovoPedido(pedido);

        return pagamentoRepository.save(pagamento);
    }

    public List<Pagamento> listarPagamentosPendentes() {
        return pagamentoRepository.findByStatus(StatusPagamento.PENDENTE);
    }

    @Transactional
    public void verificarPagamentosExpirados() {
        List<Pagamento> pendentes = listarPagamentosPendentes();
        LocalDateTime agora = LocalDateTime.now();

        for (Pagamento pagamento : pendentes) {
            if (pagamento.getDataExpiracao().isBefore(agora)) {
                pagamento.setStatus(StatusPagamento.EXPIRADO);
                
                Pedido pedido = pagamento.getPedido();
                pedido.setStatus(StatusPedido.CANCELADO);
                
                pagamentoRepository.save(pagamento);
                pedidoRepository.save(pedido);
                
                notificacaoService.criarNotificacao(
                    pedido.getUsuario(),
                    "Pagamento Expirado",
                    String.format("O pagamento do pedido #%s expirou. Por favor, realize um novo pedido.", 
                        pedido.getId().toString().substring(0, 8)),
                    TipoNotificacao.SISTEMA,
                    pedido.getId(),
                    null
                );
            }
        }
    }
}
