import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { CarrinhoService } from '@services/carrinho.service';
import { AuthService } from '@services/auth.service';
import { Carrinho } from '@models/carrinho.model';

@Component({
  selector: 'app-carrinho',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatButtonModule,
    MatIconModule
  ],
  templateUrl: './carrinho.component.html',
  styleUrls: ['./carrinho.component.scss']
})
export class CarrinhoComponent implements OnInit {
  carrinho: Carrinho | null = null;
  loading = true;
  usuarioId: string | null = null;

  constructor(
    private carrinhoService: CarrinhoService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit() {
    const user = this.authService.getCurrentUser();
    if (user) {
      this.usuarioId = user.id;
      this.carregarCarrinho();
    } else {
      this.loading = false;
      this.router.navigate(['/login']);
    }
  }

  carregarCarrinho() {
    if (!this.usuarioId) return;

    this.carrinhoService.obterCarrinho(this.usuarioId).subscribe({
      next: (carrinho) => {
        this.carrinho = carrinho;
        this.loading = false;
      },
      error: (err) => {
        console.error('Erro ao carregar carrinho:', err);
        // Se carrinho não existe (404), considerar como vazio
        if (err.status === 404 || err.status === 500) {
          this.carrinho = null;
        }
        this.loading = false;
      }
    });
  }

  atualizarQuantidade(itemId: string, novaQuantidade: number) {
    if (!this.usuarioId || novaQuantidade < 1) return;

    this.carrinhoService.atualizarItem(this.usuarioId, itemId, { quantidade: novaQuantidade }).subscribe({
      next: (carrinho) => {
        this.carrinho = carrinho;
      },
      error: (err) => console.error('Erro ao atualizar quantidade:', err)
    });
  }

  removerItem(itemId: string) {
    if (!this.usuarioId) return;

    if (confirm('Deseja remover este item do carrinho?')) {
      this.carrinhoService.removerItem(this.usuarioId, itemId).subscribe({
        next: (carrinho) => {
          this.carrinho = carrinho;
        },
        error: (err) => console.error('Erro ao remover item:', err)
      });
    }
  }

  limparCarrinho() {
    if (!this.usuarioId) return;

    if (confirm('Deseja limpar todo o carrinho?')) {
      this.carrinhoService.limparCarrinho(this.usuarioId).subscribe({
        next: () => {
          this.carrinho = null;
        },
        error: (err) => console.error('Erro ao limpar carrinho:', err)
      });
    }
  }
finalizarPedido() {
  this.router.navigate(['/checkout']);
}
}