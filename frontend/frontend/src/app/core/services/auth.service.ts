import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
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

  constructor(
    private http: HttpClient,
    private router: Router
  ) {
    console.log('🌐 AuthService inicializado');
    this.loadUserFromToken();
  }

  register(data: RegistroRequest): Observable<any> {
    return this.http.post(`${this.apiUrl}/register`, data);
  }

  login(credentials: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/login`, credentials).pipe(
      tap(response => {
        console.log('✅ Login response:', response);
        
        if (response.token) {
          // Salvar token
          localStorage.setItem('token', response.token);
          console.log('💾 Token salvo no localStorage');
          
          // Verificar
          const verificacao = localStorage.getItem('token');
          console.log('🔍 Token salvo com sucesso:', !!verificacao);
          
          // Decodificar
          try {
            const decoded: DecodedToken = jwtDecode(response.token);
            
            const usuario: Usuario = {
              id: '',
              nome: response.nome || decoded.sub,
              email: decoded.sub,
              role: response.role || decoded.role || 'USER',
              ativo: true
            };
            
            console.log('👤 Usuário criado:', usuario);
            console.log('🎭 Role:', usuario.role);
            
            this.currentUserSubject.next(usuario);
            
            // Buscar dados completos
            this.http.get<Usuario>(`${this.usuariosUrl}/me`).subscribe({
              next: (usuarioCompleto) => {
                console.log('📥 Dados completos:', usuarioCompleto);
                this.currentUserSubject.next(usuarioCompleto);
              },
              error: () => console.warn('⚠️ Erro ao carregar dados completos')
            });
            
          } catch (error) {
            console.error('❌ Erro ao decodificar token:', error);
          }
        }
      })
    );
  }

  logout(): void {
    localStorage.removeItem('token');
    console.log('🗑️ Token removido');
    this.currentUserSubject.next(null);
    this.router.navigate(['/login']);
  }

  getToken(): string | null {
    const token = localStorage.getItem('token');
    if (token) {
      console.log('🎫 Token recuperado (20 chars):', token.substring(0, 20));
    } else {
      console.log('⚠️ Nenhum token no localStorage');
    }
    return token;
  }

  isAuthenticated(): boolean {
    const token = this.getToken();
    if (!token) return false;

    try {
      const decoded: DecodedToken = jwtDecode(token);
      const isExpired = decoded.exp * 1000 < Date.now();
      console.log('🔒 Token válido:', !isExpired);
      return !isExpired;
    } catch {
      console.log('❌ Token inválido');
      return false;
    }
  }

  isAdmin(): boolean {
    const user = this.currentUserSubject.value;
    const isAdmin = user?.role === 'ADMIN';
    console.log('🔍 isAdmin - Role:', user?.role, '- Resultado:', isAdmin);
    return isAdmin;
  }

  getCurrentUser(): Usuario | null {
    return this.currentUserSubject.value;
  }

  private loadUserFromToken(): void {
    const token = this.getToken();
    
    if (token && this.isAuthenticated()) {
      try {
        const decoded: DecodedToken = jwtDecode(token);
        console.log('🔄 Carregando usuário do token:', decoded.sub);
        
        this.http.get<Usuario>(`${this.usuariosUrl}/me`).subscribe({
          next: (usuario) => {
            console.log('👤 Usuário carregado:', usuario);
            
            if (!usuario.role && decoded.role) {
              usuario.role = decoded.role;
            }
            
            this.currentUserSubject.next(usuario);
          },
          error: () => {
            // Usar dados do token
            if (decoded.role) {
              const tempUser: Usuario = {
                id: '',
                nome: decoded.sub,
                email: decoded.sub,
                role: decoded.role,
                ativo: true
              };
              console.log('⚠️ Usando usuário temporário:', tempUser);
              this.currentUserSubject.next(tempUser);
            }
          }
        });
      } catch (error) {
        console.error('❌ Erro ao carregar token:', error);
        this.logout();
      }
    }
  }
}