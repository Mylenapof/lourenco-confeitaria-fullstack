import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatStepperModule } from '@angular/material/stepper';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatDividerModule } from '@angular/material/divider';

import { CarrinhoService } from '@services/carrinho.service';
import { PedidoService } from '@services/pedido.service';
import { PagamentoService, PixResponse } from '@services/pagamento.service';
import { AuthService } from '@services/auth.service';

import { Carrinho } from '@models/carrinho.model';

@Component({
  selector: 'app-checkout',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatStepperModule,
    MatSnackBarModule,
    MatProgressSpinnerModule,
    MatDividerModule
  ],
  templateUrl: './checkout.component.html',
  styleUrls: ['./checkout.component.scss']
})
export class CheckoutComponent implements OnInit {
  
  carrinho: Carrinho | null = null;
  enderecoForm: FormGroup;
  loading = false;
  processandoPagamento = false;

  // PIX
  pixData: PixResponse | null = null;
  pedidoId: string | null = null;
  copiado = false;

  constructor(
    private fb: FormBuilder,
    private carrinhoService: CarrinhoService,
    private pedidoService: PedidoService,
    private pagamentoService: PagamentoService,
    private authService: AuthService,
    private router: Router,
    private snackBar: MatSnackBar
  ) {
    this.enderecoForm = this.fb.group({
      enderecoEntrega: ['', [Validators.required, Validators.minLength(10)]],
      observacoes: ['']
    });
  }

  ngOnInit() {
    this.carregarCarrinho();
  }

  carregarCarrinho() {
    const user = this.authService.getCurrentUser();
    if (!user) {
      this.router.navigate(['/login']);
      return;
    }

    this.loading = true;

    this.carrinhoService.obterCarrinho(user.id).subscribe({
      next: (carrinho) => {
        this.carrinho = carrinho;
        this.loading = false;

        if (!carrinho || carrinho.itens.length === 0) {
          this.snackBar.open('Seu carrinho está vazio!', 'OK', { duration: 3000 });
          this.router.navigate(['/cardapio']);
        }
      },
      error: (err) => {
        console.error('Erro ao carregar carrinho:', err);
        this.loading = false;
      }
    });
  }

  finalizarPedido() {
    if (this.enderecoForm.invalid) {
      Object.keys(this.enderecoForm.controls).forEach(key => {
        this.enderecoForm.get(key)?.markAsTouched();
      });
      return;
    }

    const user = this.authService.getCurrentUser();
    if (!user) return;

    this.processandoPagamento = true;

    const dados = {
      enderecoEntrega: this.enderecoForm.value.enderecoEntrega,
      observacoes: this.enderecoForm.value.observacoes || ''
    };

    this.pedidoService.criarPedidoDoCarrinho(user.id, dados).subscribe({
      next: (pedido) => {
        console.log('✅ Pedido criado:', pedido);
        this.pedidoId = pedido.id;

        // Agora gera PIX
        this.gerarPagamentoPix(pedido.id);
      },
      error: (err) => {
        console.error('❌ Erro ao criar pedido:', err);
        this.snackBar.open('Erro ao criar pedido.', 'OK', { duration: 3000 });
        this.processandoPagamento = false;
      }
    });
  }

  gerarPagamentoPix(pedidoId: string) {
    this.pagamentoService.gerarPagamentoPix(pedidoId).subscribe({
      next: (pixResponse) => {
        this.pixData = pixResponse;
        this.processandoPagamento = false;

        console.log('📌 PIX:', pixResponse);

        this.snackBar.open(
          '✓ Pedido criado! Efetue o pagamento via PIX',
          'OK',
          { duration: 4000 }
        );
      },
      error: (err) => {
        console.error('❌ Erro ao gerar pagamento PIX:', err);
        this.snackBar.open('Erro ao gerar pagamento PIX.', 'OK', { duration: 3000 });
        this.processandoPagamento = false;
      }
    });
  }

  copiarCodigoPix() {
    if (!this.pixData) return;

    navigator.clipboard.writeText(this.pixData.pixCopiaCola).then(() => {
      this.copiado = true;

      this.snackBar.open('✓ Código PIX copiado!', 'OK', { duration: 2000 });

      setTimeout(() => (this.copiado = false), 2500);
    });
  }

  simularPagamento() {
    if (!this.pixData) return;

    this.processandoPagamento = true;

    this.pagamentoService.simularPagamento(this.pixData.pagamentoId).subscribe({
      next: () => {
        this.snackBar.open('Pagamento aprovado!', 'OK', { duration: 2000 });

        setTimeout(() => {
          this.router.navigate(['/minha-conta']);
        }, 2000);
      },
      error: (err) => {
        console.error('Erro ao simular pagamento:', err);
        this.snackBar.open('Erro ao simular pagamento.', 'OK', { duration: 2000 });
        this.processandoPagamento = false;
      }
    });
  }

  calcularTempoRestante(): string {
    if (!this.pixData) return '';

    const expiracao = new Date(this.pixData.dataExpiracao);
    const agora = new Date();

    const diff = expiracao.getTime() - agora.getTime();

    if (diff <= 0) return 'Expirado';

    const min = Math.floor(diff / 60000);
    const sec = Math.floor((diff % 60000) / 1000);

    return `${min}:${sec.toString().padStart(2, '0')}`;
  }
}