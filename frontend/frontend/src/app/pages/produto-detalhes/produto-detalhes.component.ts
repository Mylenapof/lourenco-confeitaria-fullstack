import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { ProdutoService } from '@services/produto.service';
import { CarrinhoService } from '@services/carrinho.service';
import { AuthService } from '@services/auth.service';
import { Produto } from '@models/produto.model';

@Component({
  selector: 'app-produto-detalhes',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatButtonModule,
    MatIconModule,
    MatSnackBarModule
  ],
  templateUrl: './produto-detalhes.component.html',
  styleUrls: ['./produto-detalhes.component.scss']
})
export class ProdutoDetalhesComponent implements OnInit {
  produto: Produto | null = null;
  loading = true;
  quantidade = 1;
  adicionandoCarrinho = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private produtoService: ProdutoService,
    private carrinhoService: CarrinhoService,
    private authService: AuthService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.carregarProduto(id);
    }
  }

  carregarProduto(id: string) {
    this.produtoService.buscarPorId(id).subscribe({
      next: (produto) => {
        this.produto = produto;
        this.loading = false;
      },
      error: (err) => {
        console.error('Erro ao carregar produto:', err);
        this.loading = false;
        this.snackBar.open('Produto não encontrado', 'OK', { duration: 3000 });
        this.router.navigate(['/cardapio']);
      }
    });
  }

  diminuirQuantidade() {
    if (this.quantidade > 1) {
      this.quantidade--;
    }
  }

  aumentarQuantidade() {
    this.quantidade++;
  }

  adicionarAoCarrinho() {
    if (!this.produto) return;

    const user = this.authService.getCurrentUser();
    
    if (!user) {
      this.snackBar.open('Faça login para adicionar ao carrinho!', 'OK', { 
        duration: 3000,
        panelClass: ['snackbar-warning']
      });
      this.router.navigate(['/login']);
      return;
    }

    this.adicionandoCarrinho = true;

    this.carrinhoService.adicionarItem(user.id, {
      produtoId: this.produto.id,
      quantidade: this.quantidade
    }).subscribe({
      next: (carrinho) => {
        this.snackBar.open('✓ Produto adicionado ao carrinho!', 'Ver Carrinho', { 
          duration: 4000,
          panelClass: ['snackbar-success']
        }).onAction().subscribe(() => {
          this.router.navigate(['/carrinho']);
        });
        this.adicionandoCarrinho = false;
        this.quantidade = 1;
      },
      error: (err) => {
        console.error('Erro ao adicionar ao carrinho:', err);
        this.snackBar.open('Erro ao adicionar ao carrinho', 'OK', { 
          duration: 3000,
          panelClass: ['snackbar-error']
        });
        this.adicionandoCarrinho = false;
      }
    });
  }
}