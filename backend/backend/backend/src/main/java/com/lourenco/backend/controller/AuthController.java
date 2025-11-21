package com.lourenco.backend.controller;
import com.lourenco.backend.dto.LoginDTO;
import com.lourenco.backend.dto.RegistroUsuarioDTO;
import com.lourenco.backend.model.Usuario;
import com.lourenco.backend.security.JwtUtil;
import com.lourenco.backend.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private UsuarioService usuarioService;

    /**
     * Registro de novo usuário com validação
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegistroUsuarioDTO dto) {
        // Converte DTO para entidade
        Usuario usuario = Usuario.builder()
                .nome(dto.getNome())
                .email(dto.getEmail())
                .senha(dto.getSenha())
                .cpf(dto.getCpf())
                .role(dto.getRole() != null && !dto.getRole().isEmpty() ? dto.getRole() : "USER")
                .telefone(dto.getTelefone())
                .endereco(dto.getEndereco())
                .ativo(true)
                .build();
        
        Usuario novoUsuario = usuarioService.salvar(usuario);
        
        // Remove a senha da resposta
        novoUsuario.setSenha(null);
        
        return ResponseEntity.ok(Map.of(
            "message", "Usuário cadastrado com sucesso",
            "usuario", novoUsuario
        ));
    }

    /**
     * Login com validação
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDTO dto) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getSenha())
            );

            String username = authentication.getName();
            String token = jwtUtil.generateToken(username);

            return ResponseEntity.ok(Map.of(
                "message", "Login realizado com sucesso",
                "token", token
            ));

        } catch (AuthenticationException e) {
            return ResponseEntity.status(401)
                    .body(Map.of("error", "Credenciais inválidas"));
        }
    }
}