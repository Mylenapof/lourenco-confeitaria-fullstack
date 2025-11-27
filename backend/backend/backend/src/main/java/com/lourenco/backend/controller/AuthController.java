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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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
        
        // Verificar se usuário existe
        Usuario usuario = usuarioRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        
        System.out.println("👤 Usuário encontrado: " + usuario.getNome());
        System.out.println("🎭 Role do usuário: " + usuario.getRole());
        
        // Testar senha manualmente
        boolean senhaCorreta = passwordEncoder.matches(dto.getSenha(), usuario.getSenha());
        System.out.println("🔑 Senha correta: " + senhaCorreta);
        
        if (!senhaCorreta) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Senha incorreta"));
        }
        
        // Autenticação pelo Spring Security
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getSenha())
        );

        String username = authentication.getName();
        
        // 🔹 MUDANÇA AQUI: Passar a role ao gerar o token
        String token = jwtUtil.generateToken(username, usuario.getRole());
        
        System.out.println("✅ Login bem-sucedido para: " + username);
        System.out.println("🎫 Token gerado com role: " + usuario.getRole());

        return ResponseEntity.ok(Map.of(
            "message", "Login realizado com sucesso",
            "token", token,
            "role", usuario.getRole(),
            "nome", usuario.getNome()
        ));

    } catch (Exception e) {
        System.err.println("❌ Erro no login: " + e.getMessage());
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Erro no servidor: " + e.getMessage()));
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
// ========== ENDPOINT DE DEBUG - TEMPORÁRIO ==========
@GetMapping("/debug-token")
public ResponseEntity<?> debugToken(@RequestHeader(value = "Authorization", required = false) String authHeader) {
    Map<String, Object> debug = new HashMap<>();
    
    System.out.println("\n========== DEBUG TOKEN ==========");
    System.out.println("Header recebido: " + authHeader);
    
    if (authHeader == null) {
        debug.put("status", "NO_HEADER");
        debug.put("message", "Nenhum header Authorization foi enviado");
        System.out.println("❌ Nenhum header enviado");
        return ResponseEntity.ok(debug);
    }
    
    debug.put("headerReceived", authHeader);
    
    if (!authHeader.startsWith("Bearer ")) {
        debug.put("status", "INVALID_FORMAT");
        debug.put("message", "Header não começa com 'Bearer '");
        System.out.println("❌ Header não começa com Bearer");
        return ResponseEntity.ok(debug);
    }
    
    String token = authHeader.substring(7);
    debug.put("tokenLength", token.length());
    debug.put("tokenPrefix", token.substring(0, Math.min(20, token.length())));
    
    System.out.println("Token recebido (20 primeiros chars): " + token.substring(0, Math.min(20, token.length())));
    
    try {
        String username = jwtUtil.extractUsername(token);
        String role = jwtUtil.extractRole(token);
        boolean isValid = jwtUtil.validateToken(token);
        
        System.out.println("✅ Username: " + username);
        System.out.println("✅ Role: " + role);
        System.out.println("✅ Valid: " + isValid);
        
        debug.put("status", "SUCCESS");
        debug.put("username", username);
        debug.put("role", role);
        debug.put("isValid", isValid);
        
        // Verificar o SecurityContext atual
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            debug.put("securityContextUsername", auth.getName());
            debug.put("securityContextAuthorities", auth.getAuthorities().toString());
            System.out.println("✅ SecurityContext username: " + auth.getName());
            System.out.println("✅ SecurityContext authorities: " + auth.getAuthorities());
        } else {
            debug.put("securityContext", "NULL");
            System.out.println("❌ SecurityContext está NULL");
        }
        
        System.out.println("==================================\n");
        
    } catch (Exception e) {
        debug.put("status", "ERROR");
        debug.put("error", e.getMessage());
        System.err.println("❌ Erro ao processar token: " + e.getMessage());
        e.printStackTrace();
    }
    
    return ResponseEntity.ok(debug);
}

}