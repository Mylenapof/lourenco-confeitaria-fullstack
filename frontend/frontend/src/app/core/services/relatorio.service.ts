import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '@env/environment';

@Injectable({
  providedIn: 'root'
})
export class RelatorioService {
  private apiUrl = `${environment.apiUrl}/relatorios`;

  constructor(private http: HttpClient) {}

  getDashboard(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/dashboard`);
  }

  getFaturamentoTotal(): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/faturamento/total`);
  }

  getFaturamentoMesAtual(): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/faturamento/mes-atual`);
  }

  getTicketMedio(): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/ticket-medio`);
  }

  getProdutosMaisVendidos(limite: number = 10): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/produtos-mais-vendidos`, {
      params: { limite: limite.toString() }
    });
  }

  getVendasUltimos30Dias(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/vendas/ultimos-30-dias`);
  }
}