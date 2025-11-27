import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { AuthService } from '@services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule
  ],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {
  loginForm: FormGroup;
  hidePassword = true;
  loading = false;
  errorMessage = '';

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      senha: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  onSubmit() {
    if (this.loginForm.valid) {
      this.loading = true;
      this.errorMessage = '';

      this.authService.login(this.loginForm.value).subscribe({
        next: (response) => {
          console.log('✅ Login concluído');
          
          // 🔹 AGUARDAR UM POUCO PARA O TOKEN SER PROCESSADO
          setTimeout(() => {
            const isAdmin = this.authService.isAdmin();
            
            if (isAdmin) {
              console.log('🎯 Redirecionando para admin...');
              this.router.navigate(['/admin/dashboard']).then(() => {
                console.log('✅ Navegação concluída');
                this.loading = false;
              });
            } else {
              console.log('🎯 Redirecionando para home...');
              this.router.navigate(['/']).then(() => {
                console.log('✅ Navegação concluída');
                this.loading = false;
              });
            }
          }, 500);
        },
        error: (error) => {
          console.error('❌ Erro no login:', error);
          this.errorMessage = 'Email ou senha inválidos';
          this.loading = false;
        }
      });
    }
  }
}
