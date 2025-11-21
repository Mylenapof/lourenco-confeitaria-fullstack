import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
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
  encomendaForm: FormGroup;
  loading = false;
  tipoSelecionado: string = 'personalizada';

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

  constructor(
    private fb: FormBuilder,
    private encomendaService: EncomendaService,
    private authService: AuthService,
    private snackBar: MatSnackBar,
    private router: Router
  ) {
    this.minDate.setDate(this.minDate.getDate() + 3); // Mínimo 3 dias

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

  onSubmit() {
    if (this.encomendaForm.valid) {
      const user = this.authService.getCurrentUser();

      if (!user) {
        this.snackBar.open('Faça login para fazer uma encomenda!', 'OK', { duration: 3000 });
        this.router.navigate(['/login']);
        return;
      }

      this.loading = true;

      const encomendaData = {
        usuario: { id: user.id },
        ...this.encomendaForm.value
      };

      this.encomendaService.criar(encomendaData).subscribe({
        next: () => {
          this.snackBar.open('✓ Encomenda enviada com sucesso! Entraremos em contato em breve.', 'OK', {
            duration: 5000,
            panelClass: ['snackbar-success']
          });
          this.encomendaForm.reset();
          this.loading = false;
        },
        error: (err) => {
          console.error('Erro ao enviar encomenda:', err);
          this.snackBar.open('Erro ao enviar encomenda. Tente novamente.', 'OK', {
            duration: 3000,
            panelClass: ['snackbar-error']
          });
          this.loading = false;
        }
      });
    } else {
      Object.keys(this.encomendaForm.controls).forEach(key => {
        this.encomendaForm.get(key)?.markAsTouched();
      });
    }
  }
}