package com.lourenco.backend.repository;

import com.lourenco.backend.model.Notificacao;
import com.lourenco.backend.model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NotificacaoRepository extends JpaRepository<Notificacao, UUID> {
    List<Notificacao> findByUsuarioOrderByDataHoraDesc(Usuario usuario);
    Page<Notificacao> findByUsuarioOrderByDataHoraDesc(Usuario usuario, Pageable pageable);
    List<Notificacao> findByUsuarioAndLidaFalseOrderByDataHoraDesc(Usuario usuario);
    Long countByUsuarioAndLidaFalse(Usuario usuario);
}