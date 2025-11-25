import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (!authService.isAuthenticated()) {
    router.navigate(['/login'], {
      queryParams: { returnUrl: state.url }
    });
    return false;
  }

  // 🔹 VERIFICAR SE PRECISA SER ADMIN
  if (route.data['requireAdmin'] && !authService.isAdmin()) {
    console.warn('⛔ Acesso negado: usuário não é ADMIN');
    router.navigate(['/']);
    return false;
  }

  return true;
};