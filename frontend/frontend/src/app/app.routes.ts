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
  {
    path: '**',
    redirectTo: ''
  }
];

