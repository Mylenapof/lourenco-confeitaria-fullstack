import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);
  
  // 🔹 PEGAR TOKEN DIRETAMENTE DO LOCALSTORAGE
  const token = localStorage.getItem('token');

  console.log('🔐 Interceptor - URL:', req.url);
  console.log('🎫 Token presente?', !!token);

  // 🔹 ADICIONAR TOKEN SE EXISTIR
  let clonedReq = req;
  
  if (token) {
    clonedReq = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
    console.log('✅ Token adicionado ao header');
  } else {
    console.log('⚠️ Nenhum token encontrado');
  }

  return next(clonedReq).pipe(
    catchError((error: HttpErrorResponse) => {
      console.error('❌ Erro na requisição:', error.status, error.message);
      
      if (error.status === 401 || error.status === 403) {
        console.log('🚪 Acesso negado - redirecionando para login');
        localStorage.removeItem('token');
        router.navigate(['/login']);
      }
      
      return throwError(() => error);
    })
  );
};