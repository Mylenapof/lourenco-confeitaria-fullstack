import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTabsModule } from '@angular/material/tabs';
import { MatTableModule } from '@angular/material/table';
import { MatSelectModule } from '@angular/material/select';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { RelatorioService } from '@services/relatorio.service';

@Component({
  selector: 'app-relatorios',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatTabsModule,
    MatTableModule,
    MatSelectModule,
    MatFormFieldModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatInputModule,
    MatProgressSpinnerModule
  ],
  templateUrl: './relatorios.component.html',
  styleUrl: './relatorios.component.scss'
})
export class RelatoriosComponent implements OnInit {
  loading = false;
  
  // Dados gerais
  faturamentoTotal = 0;
  faturamentoMesAtual = 0;
  ticketMedio = 0;
  produtosMaisVendidos: any[] = [];
  vendasUltimos30Dias: any[] = [];
  vendasPorPeriodo: any[] = [];
  
  // Formulário de filtros
  filtroForm: FormGroup;
  
  // Colunas das tabelas
  colunasProdutos = ['posicao', 'produto', 'categoria', 'quantidade', 'valorTotal'];
  colunasVendas = ['data', 'totalPedidos', 'valorTotal'];
  
  constructor(
    private relatorioService: RelatorioService,
    private fb: FormBuilder
  ) {
    this.filtroForm = this.fb.group({
      dataInicio: [''],
      dataFim: [''],
      limiteProdutos: [10]
    });
  }

  ngOnInit() {
    this.carregarTodosDados();
  }

  carregarTodosDados() {
    this.loading = true;

    // Faturamento Total
    this.relatorioService.getFaturamentoTotal().subscribe({
      next: (valor) => this.faturamentoTotal = valor,
      error: (err) => console.error('Erro ao carregar faturamento total:', err)
    });

    // Faturamento Mês Atual
    this.relatorioService.getFaturamentoMesAtual().subscribe({
      next: (valor) => this.faturamentoMesAtual = valor,
      error: (err) => console.error('Erro ao carregar faturamento mensal:', err)
    });

    // Ticket Médio
    this.relatorioService.getTicketMedio().subscribe({
      next: (valor) => this.ticketMedio = valor,
      error: (err) => console.error('Erro ao carregar ticket médio:', err)
    });

    // Produtos Mais Vendidos
    this.relatorioService.getProdutosMaisVendidos(10).subscribe({
      next: (produtos) => {
        this.produtosMaisVendidos = produtos;
        this.loading = false;
      },
      error: (err) => {
        console.error('Erro ao carregar produtos mais vendidos:', err);
        this.loading = false;
      }
    });

    // Vendas Últimos 30 Dias
    this.relatorioService.getVendasUltimos30Dias().subscribe({
      next: (vendas) => this.vendasUltimos30Dias = vendas,
      error: (err) => console.error('Erro ao carregar vendas:', err)
    });
  }

  buscarVendasPorPeriodo() {
    if (this.filtroForm.value.dataInicio && this.filtroForm.value.dataFim) {
      const dataInicio = this.formatarData(this.filtroForm.value.dataInicio);
      const dataFim = this.formatarData(this.filtroForm.value.dataFim);

      this.vendasPorPeriodo = this.vendasUltimos30Dias.filter(venda => {
        const dataVenda = new Date(venda.data);
        return dataVenda >= new Date(dataInicio) && dataVenda <= new Date(dataFim);
      });
    }
  }

  atualizarLimiteProdutos() {
    const limite = this.filtroForm.value.limiteProdutos || 10;
    this.relatorioService.getProdutosMaisVendidos(limite).subscribe({
      next: (produtos) => this.produtosMaisVendidos = produtos,
      error: (err) => console.error('Erro ao atualizar produtos:', err)
    });
  }

  formatarData(data: Date): string {
    return data.toISOString().split('T')[0];
  }

  // ✅ MÉTODOS DE CÁLCULO
  calcularTotalVendas(): number {
    return this.vendasUltimos30Dias.reduce((total, venda) => total + venda.valorTotal, 0);
  }

  calcularMediaDiaria(): number {
    const total = this.calcularTotalVendas();
    return this.vendasUltimos30Dias.length > 0 ? total / this.vendasUltimos30Dias.length : 0;
  }

  calcularTotalPedidos(): number {
    return this.vendasUltimos30Dias.reduce((total, venda) => total + venda.totalPedidos, 0);
  }
}