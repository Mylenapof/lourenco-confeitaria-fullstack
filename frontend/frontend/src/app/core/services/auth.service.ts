import { Injectable, PLATFORM_ID, inject } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { Router } from '@angular/router';
import { jwtDecode } from 'jwt-decode';
import { environment } from '@env/environment';
import { LoginRequest, LoginResponse, RegistroRequest, Usuario } from '@models/usuario.model';

interface DecodedToken {
  sub: string;
  exp: number;
  authorities?: string[];
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
        
        if (this.isBrowser) {
          localStorage.setItem('token', response.token);
          this.loadUserFromToken();
        }
      })
    );
  }

  logout(): void {
    if (this.isBrowser) {
      localStorage.removeItem('token');
    }
    this.currentUserSubject.next(null);
    this.router.navigate(['/login']);
  }

  getToken(): string | null {
    if (this.isBrowser) {
      return localStorage.getItem('token');
    }
    return null;
  }

  isAuthenticated(): boolean {
    if (!this.isBrowser) return false;
    
    const token = this.getToken();
    if (!token) return false;

    try {
      const decoded: DecodedToken = jwtDecode(token);
      const isExpired = decoded.exp * 1000 < Date.now();
      return !isExpired;
    } catch {
      return false;
    }
  }

  isAdmin(): boolean {
    const user = this.currentUserSubject.value;
    console.log('🔍 Verificando admin:', user?.role);
    return user?.role === 'ADMIN';
  }

  getCurrentUser(): Usuario | null {
    return this.currentUserSubject.value;
  }

  private loadUserFromToken(): void {
    if (!this.isBrowser) return;
    
    const token = this.getToken();
    if (token && this.isAuthenticated()) {
      // Buscar dados completos do usuário
      this.http.get<Usuario>(`${this.usuariosUrl}/me`).subscribe({
        next: (usuario) => {
          console.log('👤 Usuário carregado:', usuario);
          console.log('🎭 Role:', usuario.role);
          this.currentUserSubject.next(usuario);
        },
        error: (err) => {
          console.error('Erro ao carregar usuário:', err);
          this.logout();
        }
      });
    }
  }
}