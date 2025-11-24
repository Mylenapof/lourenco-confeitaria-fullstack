package com.lourenco.backend.service;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.lourenco.backend.model.Usuario;
import com.lourenco.backend.repository.UsuarioRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Usuario salvar(Usuario usuario) {
        // Criptografa a senha
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        
        // Define ativo como true por padrão se não foi especificado
        if (usuario.getAtivo() == null) {
            usuario.setAtivo(true);
        }
        
        // 🔹 CORREÇÃO: Garantir que role está em maiúsculo
        if (usuario.getRole() != null) {
            usuario.setRole(usuario.getRole().toUpperCase());
        } else {
            usuario.setRole("USER");
        }
        
        return usuarioRepository.save(usuario);
    }

@Override
public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    System.out.println("\n=== LOAD USER BY USERNAME ===");
    System.out.println("📧 Carregando: " + email);
    
    Usuario usuario = usuarioRepository.findByEmail(email)
            .orElseThrow(() -> {
                System.err.println("❌ Usuário não encontrado: " + email);
                return new UsernameNotFoundException("Usuário não encontrado: " + email);
            });

    System.out.println("👤 Usuário encontrado: " + usuario.getNome());
    System.out.println("🔒 Senha hash (início): " + usuario.getSenha().substring(0, 20));
    System.out.println("🎭 Role original: " + usuario.getRole());
    System.out.println("✅ Ativo: " + usuario.getAtivo());

    String role = usuario.getRole();
    if (role != null && !role.startsWith("ROLE_")) {
        role = "ROLE_" + role;
    } else if (role == null) {
        role = "ROLE_USER";
    }
    
    System.out.println("🎭 Role com prefixo: " + role);

    List<GrantedAuthority> authorities = new ArrayList<>();
    authorities.add(new SimpleGrantedAuthority(role));
    
    System.out.println("🔑 Authorities criadas: " + authorities);
    System.out.println("============================\n");

    return User.builder()
            .username(usuario.getEmail())
            .password(usuario.getSenha())
            .authorities(authorities)
            .accountExpired(false)
            .accountLocked(false)
            .credentialsExpired(false)
            .disabled(!usuario.getAtivo())
            .build();
}
}