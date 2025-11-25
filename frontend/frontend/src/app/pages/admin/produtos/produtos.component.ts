import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatMenuModule } from '@angular/material/menu';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';
import { MatTooltipModule } from '@angular/material/tooltip'; // ⬅ IMPORTADO!
import { ProdutoService } from '@services/produto.service';
import { Produto } from '@models/produto.model';
import { MatDivider } from '@angular/material/divider';

@Component({
  selector: 'app-produtos',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatChipsModule,
    MatMenuModule,
    MatSnackBarModule,
    MatDialogModule,
    MatDivider,
    MatTooltipModule   // ⬅ ADICIONE AQUI!
  ],
  templateUrl: './produtos.component.html',
  styleUrls: ['./produtos.component.scss']
})
export class ProdutosComponent implements OnInit {
  produtos: Produto[] = [];
  loading = true;
  displayedColumns: string[] = ['imagem', 'nome', 'categoria', 'preco', 'status', 'acoes'];

  constructor(
    private produtoService: ProdutoService,
    private snackBar: MatSnackBar,
    private dialog: MatDialog
  ) {}

  ngOnInit() {
    this.carregarProdutos();
  }

  carregarProdutos() {
    this.loading = true;
    this.produtoService.listarTodos().subscribe({
      next: (produtos) => {
        this.produtos = produtos;
        this.loading = false;
      },
      error: (err) => {
        console.error('Erro ao carregar produtos:', err);
        this.snackBar.open('Erro ao carregar produtos', 'OK', { duration: 3000 });
        this.loading = false;
      }
    });
  }

  toggleDisponibilidade(produto: Produto) {
    this.produtoService.toggleDisponibilidade(produto.id).subscribe({
      next: (produtoAtualizado) => {
        const index = this.produtos.findIndex(p => p.id === produto.id);
        if (index !== -1) {
          this.produtos[index] = produtoAtualizado;
        }
        this.snackBar.open(
          `Produto ${produtoAtualizado.disponivel ? 'disponibilizado' : 'indisponibilizado'}`,
          'OK',
          { duration: 3000 }
        );
      },
      error: (err) => {
        console.error('Erro ao atualizar disponibilidade:', err);
        this.snackBar.open('Erro ao atualizar produto', 'OK', { duration: 3000 });
      }
    });
  }

  toggleDestaque(produto: Produto) {
    this.produtoService.toggleDestaque(produto.id).subscribe({
      next: (produtoAtualizado) => {
        const index = this.produtos.findIndex(p => p.id === produto.id);
        if (index !== -1) {
          this.produtos[index] = produtoAtualizado;
        }
        this.snackBar.open(
          `Produto ${produtoAtualizado.destaque ? 'adicionado aos' : 'removido dos'} destaques`,
          'OK',
          { duration: 3000 }
        );
      },
      error: (err) => {
        console.error('Erro ao atualizar destaque:', err);
        this.snackBar.open('Erro ao atualizar produto', 'OK', { duration: 3000 });
      }
    });
  }

  deletar(produto: Produto) {
    if (confirm(`Tem certeza que deseja deletar "${produto.nome}"?\n\nEsta ação não pode ser desfeita.`)) {
      this.produtoService.deletar(produto.id).subscribe({
        next: () => {
          this.produtos = this.produtos.filter(p => p.id !== produto.id);
          this.snackBar.open('Produto deletado com sucesso', 'OK', { duration: 3000 });
        },
        error: (err) => {
          console.error('Erro ao deletar produto:', err);
          this.snackBar.open('Erro ao deletar produto', 'OK', { duration: 3000 });
        }
      });
    }
  }
}
