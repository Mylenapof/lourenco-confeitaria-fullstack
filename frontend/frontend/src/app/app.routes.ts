import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./pages/home/home.component').then(m => m.HomeComponent)
  },
  {
    path: 'cardapio',
    loadComponent: () => import('./pages/cardapio/cardapio.component').then(m => m.CardapioComponent)
  },
  {
    path: 'produto/:id',
    loadComponent: () => import('./pages/produto-detalhes/produto-detalhes.component').then(m => m.ProdutoDetalhesComponent)
  },
  {
    path: 'encomendas',
    loadComponent: () => import('./pages/encomendas/encomendas.component').then(m => m.EncomendasComponent)
  },
  {
    path: 'contato',
    loadComponent: () => import('./pages/contato/contato.component').then(m => m.ContatoComponent)
  },
  {
    path: 'login',
    loadComponent: () => import('./pages/login/login.component').then(m => m.LoginComponent)
  },
  {
    path: 'cadastro',
    loadComponent: () => import('./pages/cadastro/cadastro.component').then(m => m.CadastroComponent)
  },
  {
    path: 'minha-conta',
    canActivate: [authGuard],
    loadComponent: () => import('./pages/minha-conta/minha-conta.component').then(m => m.MinhaContaComponent)
  },
  {
    path: 'carrinho',
    loadComponent: () => import('./pages/carrinho/carrinho.component').then(m => m.CarrinhoComponent)
  },
  // 🔹 ROTAS DE ADMIN
  {
    path: 'admin',
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
        loadComponent: () => import('./pages/admin/dashboard/dashboard.component').then(m => m.DashboardComponent)
      },
      {
        path: 'produtos',
        loadComponent: () => import('./pages/admin/produtos/produtos.component').then(m => m.ProdutosComponent)
      },
      {
        path: 'pedidos',
        loadComponent: () => import('./pages/admin/pedidos/pedidos.component').then(m => m.PedidosComponent)
      }
    ]
  },
  {
    path: '**',
    redirectTo: ''
  }
];
