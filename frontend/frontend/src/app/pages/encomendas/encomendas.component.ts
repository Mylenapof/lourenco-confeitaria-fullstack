import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, FormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { EncomendaService } from '@services/encomenda.service';
import { AuthService } from '@services/auth.service';


@Component({
  selector: 'app-encomendas',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    ReactiveFormsModule,
    FormsModule,              // ⬅️ ADICIONE ESTA LINHA
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatIconModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatSnackBarModule
  ],
  templateUrl: './encomendas.component.html',
  styleUrls: ['./encomendas.component.scss']
})
export class EncomendasComponent {
  
  // ============================================
  // CONTROLE DA ABA SELECIONADA
  // ============================================
  tipoSelecionado: string = 'personalizada';

  // ============================================
  // FORMULÁRIO PERSONALIZADO
  // ============================================
  encomendaForm: FormGroup;
  loading = false;

  tiposProduto = [
    'Bolo de Aniversário',
    'Bolo de Casamento',
    'Torta',
    'Cupcakes',
    'Docinhos',
    'Salgados',
    'Outro'
  ];

  tamanhos = ['Pequeno', 'Médio', 'Grande'];
  minDate = new Date();

  // ============================================
  // DADOS PARA CENTOS DE SALGADOS
  // ============================================
  centos = {
    fritos: 0,
    assados: 0
  };

  saboresFritos = [
    "Coxinha de Frango",
    "Risole de Carne",
    "Risole de Queijo",
    "Bolinha de Queijo",
    "Pastel de Carne",
    "Pastel de Queijo",
    "Kibe",
    "Coxinha de Catupiry"
  ];

  saboresAssados = [
    "Empada de Frango",
    "Folhado de Queijo",
    "Empada de Palmito",
    "Enroladinho de Salsicha",
    "Esfiha de Carne",
    "Esfiha de Queijo",
    "Quiche Mini",
    "Pão de Queijo"
  ];

  selecionadosFritos: string[] = [];
  selecionadosAssados: string[] = [];

  obsSalgados: string = "";   // ← NOVO
  dataEntregaSalg: Date | null = null;

  constructor(
    private fb: FormBuilder,
    private encomendaService: EncomendaService,
    private authService: AuthService,
    private snackBar: MatSnackBar,
    private router: Router
  ) {
    this.minDate.setDate(this.minDate.getDate() + 3);

    this.encomendaForm = this.fb.group({
      tipoProduto: ['', Validators.required],
      tamanho: [''],
      sabor: [''],
      decoracao: [''],
      observacoes: [''],
      dataEntrega: ['', Validators.required],
      valorEstimado: ['']
    });
  }

  // ============================================
  // CONTROLE DE QUANTIDADES
  // ============================================
  alterarQtd(tipo: 'fritos' | 'assados', valor: number) {
    if (this.centos[tipo] + valor >= 0) {
      this.centos[tipo] += valor;
    }
  }

  // ============================================
  // SELEÇÃO DE SABORES
  // ============================================
  toggleSabor(tipo: 'fritos' | 'assados', sabor: string, event: any) {
    const lista = tipo === 'fritos' ? this.selecionadosFritos : this.selecionadosAssados;

    if (event.target.checked) {
      lista.push(sabor);
    } else {
      const index = lista.indexOf(sabor);
      if (index !== -1) lista.splice(index, 1);
    }
  }

  // ============================================
  // VALOR TOTAL
  // ============================================
  calcularValorTotal() {
    return this.centos.fritos * 85 + this.centos.assados * 95;
  }

  // ============================================
  // SUBMIT PERSONALIZADO OU SALGADOS
  // ============================================
  onSubmit() {
    const user = this.authService.getCurrentUser();

    if (!user) {
      this.snackBar.open('Faça login para continuar.', 'OK', { duration: 3000 });
      this.router.navigate(['/login']);
      return;
    }

    this.loading = true;

 // ======================
// SALGADOS
// ======================
if (this.tipoSelecionado === 'salgados') {

  const data = {
    usuario: { id: user.id },

    // 🟢 CAMPO OBRIGATÓRIO PARA O BANCO (ANTES ESTAVA FALTANDO)
    tipoProduto: "Centos de Salgados",

    tipo: "CENTOS_DE_SALGADOS",

    // Campos que só existem na encomenda personalizada, então deixamos null
    tamanho: null,
    sabor: null,
    decoracao: null,

    fritos: {
      quantidade: this.centos.fritos,
      sabores: this.selecionadosFritos
    },

    assados: {
      quantidade: this.centos.assados,
      sabores: this.selecionadosAssados
    },

    observacoes: this.obsSalgados,
    valorEstimado: this.calcularValorTotal(),
    dataEntrega: this.dataEntregaSalg
  };

  this.enviar(data);
  return;
}

    // ======================
    // ENCOMENDA PERSONALIZADA
    // ======================
    if (this.encomendaForm.invalid) {
      Object.values(this.encomendaForm.controls).forEach(c => c.markAsTouched());
      this.loading = false;
      return;
    }

    const dataPersonalizada = {
      usuario: { id: user.id },
      ...this.encomendaForm.value,
      tipo: "PERSONALIZADA"
    };

    this.enviar(dataPersonalizada);
  }

  // ============================================
  // FUNÇÃO GENÉRICA DE ENVIO
  // ============================================
  enviar(payload: any) {
    this.encomendaService.criar(payload).subscribe({
      next: () => {
        this.snackBar.open('Encomenda enviada com sucesso!', 'OK', {
          duration: 4000,
          panelClass: ['snackbar-success']
        });
        this.loading = false;
      },
      error: () => {
        this.snackBar.open('Erro ao enviar. Tente novamente.', 'OK', {
          duration: 3000,
          panelClass: ['snackbar-error']
        });
        this.loading = false;
      }
    });
  }
}