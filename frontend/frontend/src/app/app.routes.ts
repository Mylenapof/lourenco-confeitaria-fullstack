import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  // ==================== ROTAS PÚBLICAS ====================
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

  // ==================== ÁREA ADMINISTRATIVA ====================
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
        path: 'produtos/novo',
        loadComponent: () => import('./pages/admin/produto-form/produto-form.component').then(m => m.ProdutoFormComponent)
      },
      {
        path: 'produtos/editar/:id',
        loadComponent: () => import('./pages/admin/produto-form/produto-form.component').then(m => m.ProdutoFormComponent)
      },
      {
        path: 'pedidos',
        loadComponent: () => import('./pages/admin/pedidos/pedidos.component').then(m => m.PedidosComponent)
      },
      {
        path: 'encomendas',
        loadComponent: () => import('./pages/admin/encomendas-admin/encomendas-admin.component').then(m => m.EncomendasAdminComponent)
      },
      {
        path: 'relatorios',
        loadComponent: () => import('./pages/admin/relatorios/relatorios.component').then(m => m.RelatoriosComponent)
      }
    ]
  },

  {
    path: '**',
    redirectTo: ''
  }
];
