package com.lourenco.backend.security;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import jakarta.servlet.FilterChain;
import jakarta.servlet.GenericFilter;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class JwtAuthenticationFilter extends GenericFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        String path = req.getRequestURI();
        
        System.out.println("\n🔍 FILTRO JWT - Path: " + path);
        
   if (path.equals("/auth/login") || path.equals("/auth/register") || 
    path.startsWith("/produtos") || path.startsWith("/categorias") || 
    path.startsWith("/files/download/") ||
    (path.equals("/encomendas") && req.getMethod().equals("POST"))) { // ✅ Apenas POST público
    chain.doFilter(request, response);
    return;
}


        String authHeader = req.getHeader("Authorization");
        System.out.println("📋 Header Authorization: " + (authHeader != null ? "presente" : "ausente"));

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            
            try {
                if (jwtUtil.validateToken(token)) {
                    String username = jwtUtil.extractUsername(token);
                    String role = jwtUtil.extractRole(token);
                    
                    System.out.println("✅ Token válido");
                    System.out.println("👤 Username: " + username);
                    System.out.println("🎭 Role extraída: " + role);
                    
                    // 🔹 CRIAR AUTHORITIES
                    List<GrantedAuthority> authorities = new ArrayList<>();
                    if (role != null) {
                        String authority = role.startsWith("ROLE_") ? role : "ROLE_" + role;
                        authorities.add(new SimpleGrantedAuthority(authority));
                        System.out.println("🔑 Authority adicionada: " + authority);
                    }
                    
                    // 🔹 CRIAR E CONFIGURAR AUTHENTICATION
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(username, null, authorities);
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));

                    // 🔹 SALVAR NO SECURITY CONTEXT
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    
                    System.out.println("✅ SecurityContext configurado!");
                    System.out.println("🔐 Authorities no context: " + SecurityContextHolder.getContext().getAuthentication().getAuthorities());
                } else {
                    System.out.println("❌ Token inválido");
                }
            } catch (Exception e) {
                System.err.println("❌ Erro ao validar token: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("⚠️ Nenhum token Bearer encontrado");
        }

        chain.doFilter(request, response);
    }
}