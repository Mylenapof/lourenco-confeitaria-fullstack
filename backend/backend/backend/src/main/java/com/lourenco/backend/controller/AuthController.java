package com.lourenco.backend.controller;

import com.lourenco.backend.dto.LoginDTO;
import com.lourenco.backend.dto.RegistroUsuarioDTO;
import com.lourenco.backend.model.Usuario;
import com.lourenco.backend.security.JwtUtil;
import com.lourenco.backend.service.UsuarioService;
import com.lourenco.backend.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
// 🛑 ANOTAÇÃO @CrossOrigin REMOVIDA DAQUI
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegistroUsuarioDTO dto) {
        try {
            Usuario usuario = Usuario.builder()
                    .nome(dto.getNome())
                    .email(dto.getEmail())
                    .senha(dto.getSenha())
                    .cpf(dto.getCpf())
                    .role(dto.getRole() != null && !dto.getRole().isEmpty() ? dto.getRole().toUpperCase() : "USER")
                    .telefone(dto.getTelefone())
                    .endereco(dto.getEndereco())
                    .ativo(true)
                    .build();
            
            Usuario novoUsuario = usuarioService.salvar(usuario);
            novoUsuario.setSenha(null);
            
            return ResponseEntity.ok(Map.of(
                "message", "Usuário cadastrado com sucesso",
                "usuario", novoUsuario
            ));
        } catch (Exception e) {
            System.err.println("❌ Erro no registro: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    @GetMapping("/gerar-senha/{senha}")
public ResponseEntity<?> gerarSenha(@PathVariable String senha) {
    String senhaCriptografada = passwordEncoder.encode(senha);
    
    return ResponseEntity.ok(Map.of(
        "senhaOriginal", senha,
        "senhaCriptografada", senhaCriptografada,
        "info", "Use esta senha criptografada no banco de dados"
    ));
}

private final PasswordEncoder passwordEncoder;

public AuthController(PasswordEncoder passwordEncoder) {
    this.passwordEncoder = passwordEncoder;
}
@PostMapping("/login")
public ResponseEntity<?> login(@Valid @RequestBody LoginDTO dto) {
    try {
        System.out.println("\n=== TENTATIVA DE LOGIN ===");
        System.out.println("📧 Email: " + dto.getEmail());
        System.out.println("🔐 Senha: " + dto.getSenha());
        
        // Verificar se usuário existe
        Usuario usuario = usuarioRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        
        System.out.println("👤 Usuário encontrado: " + usuario.getNome());
        System.out.println("🎭 Role do usuário: " + usuario.getRole());
        System.out.println("✅ Usuário ativo: " + usuario.getAtivo());
        
        // Testar senha manualmente
        boolean senhaCorreta = passwordEncoder.matches(dto.getSenha(), usuario.getSenha());
        System.out.println("🔑 Senha correta (verificação manual): " + senhaCorreta);
        
        if (!senhaCorreta) {
            System.err.println("❌ SENHA INCORRETA - verificação manual falhou");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Senha incorreta"));
        }
        
        // Tentar autenticação pelo Spring Security
        System.out.println("🔐 Tentando autenticação pelo Spring Security...");
        
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getSenha())
            );
            System.out.println("✅ Autenticação pelo Spring Security OK");
        } catch (BadCredentialsException e) {
            System.err.println("❌ BadCredentialsException: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Credenciais inválidas - Spring Security rejeitou"));
        } catch (Exception e) {
            System.err.println("❌ Erro na autenticação: " + e.getClass().getName());
            System.err.println("❌ Mensagem: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Erro na autenticação: " + e.getMessage()));
        }

        String username = authentication.getName();
        String token = jwtUtil.generateToken(username);
        
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        System.out.println("✅ Login bem-sucedido para: " + username);
        System.out.println("🎫 Token gerado");
        System.out.println("🔑 Authorities: " + authorities);
        System.out.println("======================\n");

        return ResponseEntity.ok(Map.of(
            "message", "Login realizado com sucesso",
            "token", token,
            "role", usuario.getRole(),
            "nome", usuario.getNome()
        ));

    } catch (Exception e) {
        System.err.println("❌ Erro inesperado no login: " + e.getMessage());
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Erro no servidor: " + e.getMessage()));
    }
}

    @PostMapping("/verificar-senha")
public ResponseEntity<?> verificarSenha(@RequestBody Map<String, String> dados) {
    try {
        String email = dados.get("email");
        String senhaDigitada = dados.get("senha");
        
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        
        boolean senhaCorreta = passwordEncoder.matches(senhaDigitada, usuario.getSenha());
        
        System.out.println("=================================");
        System.out.println("📧 Email: " + email);
        System.out.println("🔐 Senha digitada: " + senhaDigitada);
        System.out.println("🔒 Senha do banco (início): " + usuario.getSenha().substring(0, 30));
        System.out.println("✅ Senha está correta? " + senhaCorreta);
        System.out.println("👤 Role: " + usuario.getRole());
        System.out.println("🟢 Ativo: " + usuario.getAtivo());
        System.out.println("=================================");
        
        return ResponseEntity.ok(Map.of(
            "email", email,
            "senhaDigitadaMatches", senhaCorreta,
            "role", usuario.getRole(),
            "ativo", usuario.getAtivo(),
            "senhaHash20chars", usuario.getSenha().substring(0, 20)
        ));
        
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
    }
}
@PostMapping("/resetar-senha")
public ResponseEntity<?> resetarSenha(@RequestBody Map<String, String> dados) {
    try {
        String email = dados.get("email");
        String novaSenha = dados.get("novaSenha");
        
        System.out.println("🔄 Resetando senha para: " + email);
        
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        
        // Criptografar a nova senha
        String senhaCriptografada = passwordEncoder.encode(novaSenha);
        
        System.out.println("🔒 Senha antiga (início): " + usuario.getSenha().substring(0, 20));
        System.out.println("🔒 Senha nova (início): " + senhaCriptografada.substring(0, 20));
        
        // Atualizar
        usuario.setSenha(senhaCriptografada);
        Usuario usuarioSalvo = usuarioRepository.save(usuario);
        
        // Testar se a senha foi salva corretamente
        boolean testeMatch = passwordEncoder.matches(novaSenha, usuarioSalvo.getSenha());
        System.out.println("✅ Teste de match após salvar: " + testeMatch);
        
        System.out.println("✅ Senha resetada com sucesso para: " + email);
        
        return ResponseEntity.ok(Map.of(
            "message", "Senha resetada com sucesso",
            "email", email,
            "novaSenhaFunciona", testeMatch
        ));
        
    } catch (Exception e) {
        System.err.println("❌ Erro ao resetar senha: " + e.getMessage());
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
    }
}
}