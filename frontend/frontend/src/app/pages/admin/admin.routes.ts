import { Routes } from '@angular/router';
import { authGuard } from '../../core/guards/auth.guard';

export const ADMIN_ROUTES: Routes = [
  {
    path: '',
    canActivate: [authGuard],
    data: { requireAdmin: true },
    children: [
      {
        path: '',
        redirectTo: 'dashboard',
        pathMatch: 'full'
      },
      {
        path: 'dashboard',
        loadComponent: () => import('./dashboard/dashboard.component').then(m => m.DashboardComponent)
      },
      {
        path: 'produtos',
        loadComponent: () => import('./produtos/produtos.component').then(m => m.ProdutosComponent)
      },
      {
        path: 'pedidos',
        loadComponent: () => import('./pedidos/pedidos.component').then(m => m.PedidosComponent)
      },
      {
        path: 'encomendas',
        loadComponent: () => import('./encomendas-admin/encomendas-admin.component').then(m => m.EncomendasAdminComponent)
      }
    ]
  }
];