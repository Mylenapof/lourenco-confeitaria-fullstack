import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { MatTabsModule } from '@angular/material/tabs';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatChipsModule } from '@angular/material/chips';
import { AuthService } from '@services/auth.service';
import { PedidoService } from '@services/pedido.service';
import { EncomendaService } from '@services/encomenda.service';
import { Usuario } from '@models/usuario.model';
import { Pedido } from '@models/pedido.model';
import { Encomenda } from '@models/encomenda.model';

@Component({
  selector: 'app-minha-conta',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatTabsModule,
    MatIconModule,
    MatButtonModule,
    MatChipsModule
  ],
  templateUrl: './minha-conta.component.html',
  styleUrls: ['./minha-conta.component.scss']
})
export class MinhaContaComponent implements OnInit {
  usuario: Usuario | null = null;
  pedidos: Pedido[] = [];
  encomendas: Encomenda[] = [];
  loadingPedidos = true;
  loadingEncomendas = true;

  constructor(
    private authService: AuthService,
    private pedidoService: PedidoService,
    private encomendaService: EncomendaService,
    private router: Router
  ) {}

  ngOnInit() {
    this.usuario = this.authService.getCurrentUser();
    
    if (!this.usuario) {
      this.router.navigate(['/login']);
      return;
    }

    this.carregarPedidos();
    this.carregarEncomendas();
  }

  carregarPedidos() {
    if (!this.usuario) return;

    this.pedidoService.listarPorUsuario(this.usuario.id).subscribe({
      next: (pedidos) => {
        this.pedidos = pedidos;
        this.loadingPedidos = false;
      },
      error: (err) => {
        console.error('Erro ao carregar pedidos:', err);
        this.loadingPedidos = false;
      }
    });
  }

  carregarEncomendas() {
    if (!this.usuario) return;

    this.encomendaService.listarPorUsuario(this.usuario.id).subscribe({
      next: (encomendas) => {
        this.encomendas = encomendas;
        this.loadingEncomendas = false;
      },
      error: (err) => {
        console.error('Erro ao carregar encomendas:', err);
        this.loadingEncomendas = false;
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
      'CANCELADO': 'status-cancelado',
      'ORCAMENTO_ENVIADO': 'status-orcamento',
      'APROVADO': 'status-aprovado'
    };
    return statusMap[status] || 'status-default';
  }

  getStatusTexto(status: string): string {
    const statusMap: any = {
      'PENDENTE': 'Pendente',
      'CONFIRMADO': 'Confirmado',
      'EM_PREPARACAO': 'Em Preparação',
      'PRONTO': 'Pronto',
      'ENTREGUE': 'Entregue',
      'CANCELADO': 'Cancelado',
      'ORCAMENTO_ENVIADO': 'Orçamento Enviado',
      'APROVADO': 'Aprovado',
      'EM_PRODUCAO': 'Em Produção'
    };
    return statusMap[status] || status;
  }

  logout() {
    if (confirm('Deseja realmente sair?')) {
      this.authService.logout();
    }
  }
}