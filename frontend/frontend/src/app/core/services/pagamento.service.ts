import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '@env/environment';

export interface PixResponse {
  pagamentoId: string;
  pedidoId: string;
  valor: number;
  status: string;
  pixCopiaCola: string;
  qrCodeBase64: string;
  dataExpiracao: string;
  dataCriacao: string;
}

export interface Pagamento {
  id: string;
  pedido: any;
  status: string;
  valor: number;
  metodoPagamento: string;
  pixCopiaCola: string;
  qrCodeBase64: string;
  dataCriacao: string;
  dataExpiracao: string;
  dataAprovacao?: string;
}

@Injectable({
  providedIn: 'root'
})
export class PagamentoService {
  private apiUrl = `${environment.apiUrl}/pagamentos`;

  constructor(private http: HttpClient) {}

  gerarPagamentoPix(pedidoId: string): Observable<PixResponse> {
    return this.http.post<PixResponse>(`${this.apiUrl}/pedido/${pedidoId}/pix`, {});
  }

  buscarPagamentoPorPedido(pedidoId: string): Observable<Pagamento> {
    return this.http.get<Pagamento>(`${this.apiUrl}/pedido/${pedidoId}`);
  }

  buscarPagamento(pagamentoId: string): Observable<Pagamento> {
    return this.http.get<Pagamento>(`${this.apiUrl}/${pagamentoId}`);
  }

  simularPagamento(pagamentoId: string): Observable<Pagamento> {
    return this.http.post<Pagamento>(`${this.apiUrl}/${pagamentoId}/simular-pagamento`, {});
  }
}