import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatChipsModule } from '@angular/material/chips';
import { MatIconModule } from '@angular/material/icon';
import { ProdutoService } from '@services/produto.service';
import { CategoriaService } from '@services/categoria.service';
import { Produto } from '@models/produto.model';
import { Categoria } from '@models/categoria.model';

@Component({
  selector: 'app-cardapio',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatButtonModule,
    MatChipsModule,
    MatIconModule
  ],
  templateUrl: './cardapio.component.html',
  styleUrls: ['./cardapio.component.scss']
})
export class CardapioComponent implements OnInit {
  produtos: Produto[] = [];
  produtosFiltrados: Produto[] = [];
  categorias: Categoria[] = [];
  categoriaSelected: string | null = null;
  loading = true;

  constructor(
    private produtoService: ProdutoService,
    private categoriaService: CategoriaService
  ) {}

  ngOnInit() {
    this.carregarCategorias();
    this.carregarProdutos();
  }

  carregarCategorias() {
    this.categoriaService.listarTodos().subscribe({
      next: (categorias) => {
        this.categorias = categorias;
      },
      error: (err) => console.error('Erro ao carregar categorias:', err)
    });
  }

  carregarProdutos() {
    this.produtoService.listarDisponiveis().subscribe({
      next: (produtos) => {
        this.produtos = produtos;
        this.produtosFiltrados = produtos;
        this.loading = false;
      },
      error: (err) => {
        console.error('Erro ao carregar produtos:', err);
        this.loading = false;
      }
    });
  }

  filtrarPorCategoria(categoriaId: string | null) {
    this.categoriaSelected = categoriaId;
    
    if (!categoriaId) {
      this.produtosFiltrados = this.produtos;
    } else {
      this.produtosFiltrados = this.produtos.filter(
        p => p.categoria.id === categoriaId
      );
    }
  }
}