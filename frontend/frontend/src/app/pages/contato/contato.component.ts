import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-contato',
  standalone: true,
  imports: [
    CommonModule,
    MatIconModule,
    MatButtonModule
  ],
  templateUrl: './contato.component.html',
  styleUrls: ['./contato.component.scss']
})
export class ContatoComponent {

  contatos = {
    telefone: '(11) 98765-4321',
    email: 'contato@lourenco.com.br',
    endereco: 'Rua das Flores, 123 - São Paulo, SP',
    horario: 'Segunda à Sábado: 8h às 18h',
    instagram: '@lourencoconfeitaria',
    facebook: 'Lourenço Confeitaria'
  };

  // 🔹 Método para gerar o link 'tel:' corretamente
  getTelefoneLimpo(): string {
    if (this.contatos?.telefone) {
      return 'tel:' + this.contatos.telefone.replace(/\D/g, '');
    }
    return '';
  }

  // 🔹 Método para gerar o link 'mailto:'
  getEmailLink(): string {
    return 'mailto:' + this.contatos.email;
  }
}
