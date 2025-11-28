import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { HttpClient } from '@angular/common/http';
import { environment } from '@env/environment';

interface DashboardStats {
  totalProdutos: number;
  totalProdutosDisponiveis: number;
  totalCategorias: number;
  totalUsuarios: number;
  totalPedidos: number;
  pedidosPendentes: number;
  pedidosConfirmados: number;
  pedidosEntregues: number;
  totalEncomendas: number;
  encomendasPendentes: number;
  encomendasAprovadas: number;
  faturamentoTotal: number;
  faturamentoMesAtual: number;
  ticketMedio: number;
}

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatCardModule,
    MatIconModule,
    MatButtonModule
  ],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {
  stats: DashboardStats | null = null;
  loading = true;

  constructor(private http: HttpClient) {}

  ngOnInit() {
    this.carregarDashboard();
  }
carregarDashboard() {
  const token = localStorage.getItem('token');

  this.http.get<DashboardStats>(
    `${environment.apiUrl}/relatorios/dashboard`,
    {
      headers: { Authorization: `Bearer ${token}` }
    }
  ).subscribe({
    next: (data) => {
      this.stats = data;
      this.loading = false;
      console.log('📊 Dados do Dashboard RECEBIDOS:', data);
    },
    error: (err) => {
      console.error('❌ Erro ao carregar dashboard:', err);
      this.loading = false;
    }
  });
}
}