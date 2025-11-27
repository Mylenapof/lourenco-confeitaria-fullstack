import { Injectable, PLATFORM_ID, inject } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap, delay } from 'rxjs';
import { Router } from '@angular/router';
import { jwtDecode } from 'jwt-decode';
import { environment } from '@env/environment';
import { LoginRequest, LoginResponse, RegistroRequest, Usuario } from '@models/usuario.model';

interface DecodedToken {
  sub: string;
  exp: number;
  role?: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = `${environment.apiUrl}/auth`;
  private usuariosUrl = `${environment.apiUrl}/usuarios`;
  private currentUserSubject = new BehaviorSubject<Usuario | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();
  private platformId = inject(PLATFORM_ID);
  private isBrowser: boolean;

  constructor(
    private http: HttpClient,
    private router: Router
  ) {
    this.isBrowser = isPlatformBrowser(this.platformId);
    if (this.isBrowser) {
      this.loadUserFromToken();
    }
  }

  register(data: RegistroRequest): Observable<any> {
    return this.http.post(`${this.apiUrl}/register`, data);
  }

  login(credentials: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/login`, credentials).pipe(
      tap(response => {
        console.log('✅ Login response:', response);
        
        if (this.isBrowser && response.token) {
          // 🔹 SALVAR TOKEN IMEDIATAMENTE E SINCRONAMENTE
          localStorage.setItem('token', response.token);
          console.log('💾 Token salvo no localStorage');
          
          // 🔹 DECODIFICAR E CRIAR USUÁRIO IMEDIATAMENTE
          try {
            const decoded: DecodedToken = jwtDecode(response.token);
            
            // 🔹 CRIAR USUÁRIO COM DADOS DA RESPOSTA E DO TOKEN
            const usuario: Usuario = {
              id: '', // Será atualizado depois
              nome: response.nome || decoded.sub,
              email: decoded.sub,
              role: response.role || decoded.role || 'USER',
              ativo: true
            };
            
            console.log('👤 Usuário criado:', usuario);
            console.log('🎭 Role do usuário:', usuario.role);
            
            // 🔹 ATUALIZAR IMEDIATAMENTE
            this.currentUserSubject.next(usuario);
            
            // 🔹 BUSCAR DADOS COMPLETOS EM BACKGROUND (SEM BLOQUEAR)
            this.http.get<Usuario>(`${this.usuariosUrl}/me`).subscribe({
              next: (usuarioCompleto) => {
                console.log('📥 Dados completos recebidos:', usuarioCompleto);
                this.currentUserSubject.next(usuarioCompleto);
              },
              error: (err) => {
                console.warn('⚠️ Não foi possível carregar dados completos, mantendo usuário do token');
              }
            });
            
          } catch (error) {
            console.error('❌ Erro ao decodificar token:', error);
          }
        }
      })
    );
  }

  logout(): void {
    if (this.isBrowser) {
      localStorage.removeItem('token');
      console.log('🗑️ Token removido do localStorage');
    }
    this.currentUserSubject.next(null);
    this.router.navigate(['/login']);
  }

  getToken(): string | null {
    if (this.isBrowser) {
      const token = localStorage.getItem('token');
      if (token) {
        console.log('🎫 Token recuperado do localStorage (primeiros 20 chars):', token.substring(0, 20));
      }
      return token;
    }
    return null;
  }

  isAuthenticated(): boolean {
    if (!this.isBrowser) return false;
    
    const token = this.getToken();
    if (!token) {
      console.log('⚠️ Nenhum token encontrado - não autenticado');
      return false;
    }

    try {
      const decoded: DecodedToken = jwtDecode(token);
      const isExpired = decoded.exp * 1000 < Date.now();
      
      if (isExpired) {
        console.log('⏰ Token expirado');
      } else {
        console.log('✅ Token válido');
      }
      
      return !isExpired;
    } catch {
      console.log('❌ Token inválido');
      return false;
    }
  }

  isAdmin(): boolean {
    const user = this.currentUserSubject.value;
    const isAdmin = user?.role === 'ADMIN';
    console.log('🔍 Verificando admin - Role:', user?.role, '- É admin?', isAdmin);
    return isAdmin;
  }

  getCurrentUser(): Usuario | null {
    return this.currentUserSubject.value;
  }

  private loadUserFromToken(): void {
    if (!this.isBrowser) return;
    
    const token = this.getToken();
    
    if (token && this.isAuthenticated()) {
      try {
        const decoded: DecodedToken = jwtDecode(token);
        
        console.log('🔍 Carregando usuário do token:', decoded.sub);
        
        // 🔹 BUSCAR DADOS COMPLETOS
        this.http.get<Usuario>(`${this.usuariosUrl}/me`).subscribe({
          next: (usuario) => {
            console.log('👤 Usuário carregado:', usuario);
            console.log('🎭 Role:', usuario.role);
            
            // 🔹 GARANTIR QUE A ROLE ESTÁ CORRETA
            if (!usuario.role && decoded.role) {
              usuario.role = decoded.role;
            }
            
            this.currentUserSubject.next(usuario);
          },
          error: (err) => {
            console.error('❌ Erro ao carregar usuário:', err);
            
            // 🔹 CRIAR USUÁRIO TEMPORÁRIO DO TOKEN
            if (decoded.sub && decoded.role) {
              const tempUser: Usuario = {
                id: '',
                nome: decoded.sub,
                email: decoded.sub,
                role: decoded.role,
                ativo: true
              };
              
              console.log('⚠️ Usando usuário temporário do token:', tempUser);
              this.currentUserSubject.next(tempUser);
            }
          }
        });
      } catch (error) {
        console.error('❌ Erro ao decodificar token:', error);
        this.logout();
      }
    } else {
      console.log('⚠️ Nenhum token válido encontrado ao inicializar');
    }
  }
}