import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatListModule } from '@angular/material/list';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatToolbarModule } from '@angular/material/toolbar';
import { AuthService } from '@services/auth.service';

@Component({
  selector: 'app-admin-layout',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatSidenavModule,
    MatListModule,
    MatIconModule,
    MatButtonModule,
    MatToolbarModule
  ],
  template: `
    <div class="admin-layout">
      <mat-sidenav-container>
        <mat-sidenav mode="side" opened class="admin-sidebar">
          <div class="sidebar-header">
            <h2>Admin Panel</h2>
          </div>
          
          <mat-nav-list>
            <a mat-list-item routerLink="/admin/dashboard" routerLinkActive="active">
              <mat-icon>dashboard</mat-icon>
              Dashboard
            </a>
            <a mat-list-item routerLink="/admin/produtos" routerLinkActive="active">
              <mat-icon>inventory</mat-icon>
              Produtos
            </a>
            <a mat-list-item routerLink="/admin/pedidos" routerLinkActive="active">
              <mat-icon>shopping_cart</mat-icon>
              Pedidos
            </a>
            <a mat-list-item routerLink="/admin/encomendas" routerLinkActive="active">
              <mat-icon>cake</mat-icon>
              Encomendas
            </a>
            <mat-divider></mat-divider>
            <a mat-list-item routerLink="/">
              <mat-icon>store</mat-icon>
              Ver Loja
            </a>
            <a mat-list-item (click)="logout()">
              <mat-icon>logout</mat-icon>
              Sair
            </a>
          </mat-nav-list>
        </mat-sidenav>

        <mat-sidenav-content>
          <router-outlet></router-outlet>
        </mat-sidenav-content>
      </mat-sidenav-container>
    </div>
  `,
  styles: [`
    .admin-layout {
      height: 100vh;
    }

    mat-sidenav-container {
      height: 100%;
    }

    .admin-sidebar {
      width: 260px;
      background: #1a1a2e;
      color: white;

      .sidebar-header {
        padding: 20px;
        background: var(--primary-pink);
        text-align: center;

        h2 {
          margin: 0;
          font-size: 20px;
          font-weight: 700;
        }
      }

      mat-nav-list {
        padding-top: 10px;

        a {
          color: rgba(255,255,255,0.7);
          transition: all 0.3s;

          mat-icon {
            margin-right: 15px;
            color: rgba(255,255,255,0.5);
          }

          &:hover, &.active {
            background: rgba(233, 30, 140, 0.1);
            color: white;

            mat-icon {
              color: var(--primary-pink);
            }
          }
        }
      }
    }

    mat-sidenav-content {
      background: #f5f5f5;
      padding: 30px;
    }
  `]
})
export class AdminLayoutComponent {
  constructor(private authService: AuthService) {}

  logout() {
    this.authService.logout();
  }
}