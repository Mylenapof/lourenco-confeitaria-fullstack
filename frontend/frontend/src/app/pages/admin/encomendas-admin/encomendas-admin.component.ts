import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { EncomendaService } from '@services/encomenda.service';
import { Encomenda } from '@models/encomenda.model';

@Component({
  selector: 'app-encomendas-admin',
  standalone: true,
  imports: [
    CommonModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatChipsModule,
    MatSelectModule,
    MatSnackBarModule
  ],
  templateUrl: './encomendas-admin.component.html',
  styleUrls: ['./encomendas-admin.component.scss']
})
export class EncomendasAdminComponent implements OnInit {
  encomendas: Encomenda[] = [];
  loading = true;
  displayedColumns = ['id', 'cliente', 'tipo', 'dataEntrega', 'status', 'valor', 'acoes'];

  statusOptions = [
    'PENDENTE',
    'ORCAMENTO_ENVIADO',
    'APROVADO',
    'EM_PRODUCAO',
    'PRONTO',
    'ENTREGUE',
    'CANCELADO'
  ];

  constructor(
    private encomendaService: EncomendaService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit() {
    this.carregarEncomendas();
  }

  carregarEncomendas() {
    this.encomendaService.listarTodas().subscribe({
      next: (encomendas) => {
        this.encomendas = encomendas;
        this.loading = false;
      },
      error: (err) => {
        console.error('Erro ao carregar encomendas:', err);
        this.loading = false;
      }
    });
  }

  atualizarStatus(id: string, novoStatus: string) {
    this.encomendaService.atualizarStatus(id, novoStatus).subscribe({
      next: () => {
        this.snackBar.open('Status atualizado!', 'OK', {
          duration: 3000,
          panelClass: ['snackbar-success']
        });
        this.carregarEncomendas();
      },
      error: (err) => {
        console.error('Erro ao atualizar status:', err);
        this.snackBar.open('Erro ao atualizar status', 'OK', {
          duration: 3000,
          panelClass: ['snackbar-error']
        });
      }
    });
  }

  getStatusClass(status: string): string {
    const map: any = {
      'PENDENTE': 'status-pendente',
      'ORCAMENTO_ENVIADO': 'status-orcamento',
      'APROVADO': 'status-aprovado',
      'EM_PRODUCAO': 'status-producao',
      'PRONTO': 'status-pronto',
      'ENTREGUE': 'status-entregue',
      'CANCELADO': 'status-cancelado'
    };
    return map[status] || '';
  }

  getStatusTexto(status: string): string {
    const map: any = {
      'PENDENTE': 'Pendente',
      'ORCAMENTO_ENVIADO': 'Orçamento Enviado',
      'APROVADO': 'Aprovado',
      'EM_PRODUCAO': 'Em Produção',
      'PRONTO': 'Pronto',
      'ENTREGUE': 'Entregue',
      'CANCELADO': 'Cancelado'
    };
    return map[status] || status;
  }

  // MÉTODO NOVO — EVITA O ERRO DO ANGULAR
  getPendentes(): number {
    return this.encomendas?.filter(e => e.status === 'PENDENTE').length ?? 0;
  }
}