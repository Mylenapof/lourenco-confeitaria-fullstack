import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatMenuModule } from '@angular/material/menu';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTabsModule } from '@angular/material/tabs';
import { MatExpansionModule } from '@angular/material/expansion';
import { HttpClient } from '@angular/common/http';
import { environment } from '@env/environment';
import { Pedido, StatusPedido } from '@models/pedido.model';

// 🔹 INTERFACE PARA STATUS OPTIONS
interface StatusOption {
  value: StatusPedido; // 🔹 MUDANÇA AQUI: de string para StatusPedido
  label: string;
  color: string;
}

@Component({
  selector: 'app-pedidos',
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
    MatTabsModule,
    MatExpansionModule
  ],
  templateUrl: './pedidos.component.html',
  styleUrls: ['./pedidos.component.scss']
})
export class PedidosComponent implements OnInit {
  pedidos: Pedido[] = [];
  pedidosFiltrados: Pedido[] = [];
  loading = true;
  filtroAtual: string = 'TODOS';

  // 🔹 MUDANÇA AQUI: Tipagem correta
  statusOptions: StatusOption[] = [
    { value: StatusPedido.PENDENTE, label: 'Pendente', color: 'warning' },
    { value: StatusPedido.CONFIRMADO, label: 'Confirmado', color: 'info' },
    { value: StatusPedido.EM_PREPARACAO, label: 'Em Preparação', color: 'primary' },
    { value: StatusPedido.PRONTO, label: 'Pronto', color: 'success' },
    { value: StatusPedido.ENTREGUE, label: 'Entregue', color: 'success' },
    { value: StatusPedido.CANCELADO, label: 'Cancelado', color: 'error' }
  ];

  constructor(
    private http: HttpClient,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit() {
    this.carregarPedidos();
  }

carregarPedidos() {
  this.loading = true;
  const token = localStorage.getItem('token'); // 🔹 PEGAR TOKEN

  this.http.get<Pedido[]>(
    `${environment.apiUrl}/pedidos`,
    {
      headers: { Authorization: `Bearer ${token}` } // 🔹 ENVIAR TOKEN
    }
  )
  .subscribe({
    next: (pedidos) => {
      this.pedidos = pedidos.sort((a, b) => 
        new Date(b.dataPedido).getTime() - new Date(a.dataPedido).getTime()
      );
      this.aplicarFiltro('TODOS');
      this.loading = false;
    },
    error: (err) => {
      console.error('❌ Erro ao carregar pedidos:', err);
      this.snackBar.open('Erro ao carregar pedidos', 'OK', { duration: 3000 });
      this.loading = false;
    }
  });
}

  aplicarFiltro(status: string) {
    this.filtroAtual = status;
    if (status === 'TODOS') {
      this.pedidosFiltrados = this.pedidos;
    } else {
      this.pedidosFiltrados = this.pedidos.filter(p => p.status === status);
    }
  }

  contarPorStatus(status: string): number {
    if (status === 'TODOS') return this.pedidos.length;
    return this.pedidos.filter(p => p.status === status).length;
  }

  atualizarStatus(pedido: Pedido, novoStatus: StatusPedido) {
    this.http.patch(`${environment.apiUrl}/pedidos/${pedido.id}/status`, { status: novoStatus })
      .subscribe({
        next: (pedidoAtualizado: any) => {
          const index = this.pedidos.findIndex(p => p.id === pedido.id);
          if (index !== -1) {
            this.pedidos[index].status = novoStatus;
            this.aplicarFiltro(this.filtroAtual);
          }
          this.snackBar.open('Status atualizado com sucesso', 'OK', { duration: 3000 });
        },
        error: (err) => {
          console.error('Erro ao atualizar status:', err);
          this.snackBar.open('Erro ao atualizar status', 'OK', { duration: 3000 });
        }
      });
  }

  getStatusClass(status: string): string {
    const statusMap: any = {
      'PENDENTE': 'status-pendente',
      'CONFIRMADO': 'status-confirmado',
      'EM_PREPARACAO': 'status-preparacao',
      'PRONTO': 'status-pronto',
      'ENTREGUE': 'status-entregue',
      'CANCELADO': 'status-cancelado'
    };
    return statusMap[status] || '';
  }

  getStatusLabel(status: string): string {
    const statusMap: any = {
      'PENDENTE': 'Pendente',
      'CONFIRMADO': 'Confirmado',
      'EM_PREPARACAO': 'Em Preparação',
      'PRONTO': 'Pronto',
      'ENTREGUE': 'Entregue',
      'CANCELADO': 'Cancelado'
    };
    return statusMap[status] || status;
  }
}
