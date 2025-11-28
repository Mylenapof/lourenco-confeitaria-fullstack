import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { HttpClient } from '@angular/common/http';
import { environment } from '@env/environment';
import { Encomenda, StatusEncomenda } from '@models/encomenda.model';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';

interface StatusOption {
  value: StatusEncomenda;
  label: string;
  color: string;
}

@Component({
  selector: 'app-encomendas-admin',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    ReactiveFormsModule,
    MatButtonModule,
    MatIconModule,
    MatChipsModule,
    MatSnackBarModule,
    MatExpansionModule,
    MatFormFieldModule,
    MatInputModule
  ],
  templateUrl: './encomendas-admin.component.html',
  styleUrls: ['./encomendas-admin.component.scss']
})
export class EncomendasAdminComponent implements OnInit {
  encomendas: Encomenda[] = [];
  encomendasFiltradas: Encomenda[] = [];
  loading = true;
  filtroAtual: string = 'TODOS';
  orcamentoForm: FormGroup;
  encomendaOrcamento: Encomenda | null = null;

  statusOptions: StatusOption[] = [
    { value: StatusEncomenda.PENDENTE, label: 'Pendente', color: 'warning' },
    { value: StatusEncomenda.ORCAMENTO_ENVIADO, label: 'Orçamento Enviado', color: 'info' },
    { value: StatusEncomenda.APROVADO, label: 'Aprovado', color: 'success' },
    { value: StatusEncomenda.EM_PRODUCAO, label: 'Em Produção', color: 'primary' },
    { value: StatusEncomenda.PRONTO, label: 'Pronto', color: 'success' },
    { value: StatusEncomenda.ENTREGUE, label: 'Entregue', color: 'success' },
    { value: StatusEncomenda.CANCELADO, label: 'Cancelado', color: 'error' }
  ];

  constructor(
    private http: HttpClient,
    private snackBar: MatSnackBar,
    private fb: FormBuilder
  ) {
    this.orcamentoForm = this.fb.group({
      valorEstimado: ['', [Validators.required, Validators.min(0.01)]]
    });
  }

  ngOnInit() {
    this.carregarEncomendas();
  }

 carregarEncomendas() {
  this.loading = true;
  const token = localStorage.getItem('token');

  this.http.get<Encomenda[]>(`${environment.apiUrl}/encomendas`, {
    headers: { Authorization: `Bearer ${token}` }
  })
  .subscribe({
    next: (encomendas) => {
      this.encomendas = encomendas.sort((a, b) =>
        new Date(b.dataEntrega).getTime() - new Date(a.dataEntrega).getTime()
      );
      this.aplicarFiltro('TODOS');
      this.loading = false;
    },
    error: (err) => {
      console.error('❌ Erro ao carregar encomendas:', err);
      this.snackBar.open('Erro ao carregar encomendas', 'OK', { duration: 3000 });
      this.loading = false;
    }
  });
}

  aplicarFiltro(status: string) {
    this.filtroAtual = status;
    if (status === 'TODOS') {
      this.encomendasFiltradas = this.encomendas;
    } else {
      this.encomendasFiltradas = this.encomendas.filter(e => e.status === status);
    }
  }

  contarPorStatus(status: string): number {
    if (status === 'TODOS') return this.encomendas.length;
    return this.encomendas.filter(e => e.status === status).length;
  }

  abrirFormularioOrcamento(encomenda: Encomenda) {
    this.encomendaOrcamento = encomenda;
    this.orcamentoForm.patchValue({
      valorEstimado: encomenda.valorEstimado || ''
    });
  }

  enviarOrcamento() {
    if (this.orcamentoForm.valid && this.encomendaOrcamento) {
      const valorEstimado = this.orcamentoForm.value.valorEstimado;
      
      this.http.put(`${environment.apiUrl}/encomendas/${this.encomendaOrcamento.id}`, {
        ...this.encomendaOrcamento,
        valorEstimado: parseFloat(valorEstimado),
        status: StatusEncomenda.ORCAMENTO_ENVIADO
      }).subscribe({
        next: () => {
          this.snackBar.open('Orçamento enviado com sucesso!', 'OK', { duration: 3000 });
          this.encomendaOrcamento = null;
          this.orcamentoForm.reset();
          this.carregarEncomendas();
        },
        error: (err) => {
          console.error('Erro ao enviar orçamento:', err);
          this.snackBar.open('Erro ao enviar orçamento', 'OK', { duration: 3000 });
        }
      });
    }
  }

  atualizarStatus(encomenda: Encomenda, novoStatus: StatusEncomenda) {
    this.http.patch(`${environment.apiUrl}/encomendas/${encomenda.id}/status`, { status: novoStatus })
      .subscribe({
        next: () => {
          const index = this.encomendas.findIndex(e => e.id === encomenda.id);
          if (index !== -1) {
            this.encomendas[index].status = novoStatus;
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
      'ORCAMENTO_ENVIADO': 'status-orcamento',
      'APROVADO': 'status-aprovado',
      'EM_PRODUCAO': 'status-producao',
      'PRONTO': 'status-pronto',
      'ENTREGUE': 'status-entregue',
      'CANCELADO': 'status-cancelado'
    };
    return statusMap[status] || '';
  }

  getStatusLabel(status: string): string {
    const statusMap: any = {
      'PENDENTE': 'Pendente',
      'ORCAMENTO_ENVIADO': 'Orçamento Enviado',
      'APROVADO': 'Aprovado',
      'EM_PRODUCAO': 'Em Produção',
      'PRONTO': 'Pronto',
      'ENTREGUE': 'Entregue',
      'CANCELADO': 'Cancelado'
    };
    return statusMap[status] || status;
  }

  calcularDiasRestantes(dataEntrega: string): number {
    const hoje = new Date();
    const entrega = new Date(dataEntrega);
    const diff = entrega.getTime() - hoje.getTime();
    return Math.ceil(diff / (1000 * 3600 * 24));
  }

  getUrgenciaClass(dias: number): string {
    if (dias < 0) return 'urgencia-atrasado';
    if (dias <= 2) return 'urgencia-urgente';
    if (dias <= 5) return 'urgencia-proximo';
    return 'urgencia-normal';
  }
}