import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { ProdutoService } from '@services/produto.service';
import { Produto } from '@models/produto.model';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatButtonModule,
    MatIconModule
  ],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {
  produtosDestaque: Produto[] = [];
  loading = true;

  constructor(private produtoService: ProdutoService) {}

  ngOnInit() {
    this.carregarDestaques();
  }

  carregarDestaques() {
    this.produtoService.listarDestaques().subscribe({
      next: (produtos) => {
        this.produtosDestaque = produtos.slice(0, 3);
        this.loading = false;
      },
      error: (err) => {
        console.error('Erro ao carregar produtos:', err);
        this.loading = false;
      }
    });
  }
}