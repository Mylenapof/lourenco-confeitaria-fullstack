import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  
  // 🔹 PEGAR TOKEN
  const token = authService.getToken();

  console.log('🔐 Interceptor - URL:', req.url);
  console.log('🎫 Interceptor - Token presente?', !!token);

  // 🔹 DEBUG: Mostrar primeiros 20 chars do token
  if (token) {
    console.log('🎫 Token (primeiros 20):', token.substring(0, 20));
  }

  // 🔹 SE TEM TOKEN, ADICIONAR NO HEADER
  if (token) {
    req = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });

    console.log('✅ Token adicionado ao header Authorization');
    console.log('📤 Request headers:', req.headers.get('Authorization')?.substring(0, 30)); // Debug
  } else {
    console.log('⚠️ Nenhum token encontrado no localStorage');
  }

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      console.error('❌ Erro na requisição:', error.status, error.message);
      
      if (error.status === 401) {
        console.log('🚪 Token expirado ou inválido - redirecionando para login');
        authService.logout();
        router.navigate(['/login']);
      }
      
      return throwError(() => error);
    })
  );
};