import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatMenuModule } from '@angular/material/menu';
import { MatBadgeModule } from '@angular/material/badge';
import { MatDividerModule } from '@angular/material/divider';
import { AuthService } from '@services/auth.service';
import { CarrinhoService } from '@services/carrinho.service';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatIconModule,
    MatButtonModule,
    MatMenuModule,
    MatBadgeModule,
    MatDividerModule
  ],
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit {
  isLoggedIn = false;
  isAdmin = false;
  totalItens = 0;

  constructor(
    private authService: AuthService,
    private carrinhoService: CarrinhoService
  ) {}

  ngOnInit() {
    // Observar login
    this.authService.currentUser$.subscribe(user => {
      this.isLoggedIn = !!user;
      this.isAdmin = this.authService.isAdmin();
    });

    // Observar carrinho
    this.carrinhoService.carrinho$.subscribe(carrinho => {
      this.totalItens = carrinho?.totalItens || 0;
    });
  }

  logout() {
    this.authService.logout();
  }
}
