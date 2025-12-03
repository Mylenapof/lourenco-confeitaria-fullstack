import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '@env/environment';
import { Pedido } from '@models/pedido.model';

@Injectable({
  providedIn: 'root'
})
export class PedidoService {
  private apiUrl = `${environment.apiUrl}/pedidos`;

  constructor(private http: HttpClient) {}

  listarPorUsuario(usuarioId: string): Observable<Pedido[]> {
    return this.http.get<Pedido[]>(`${this.apiUrl}/usuario/${usuarioId}`);
  }

  buscarPorId(id: string): Observable<Pedido> {
    return this.http.get<Pedido>(`${this.apiUrl}/${id}`);
  }

  criar(pedido: any): Observable<Pedido> {
    return this.http.post<Pedido>(this.apiUrl, pedido);
  }
  criarPedidoDoCarrinho(usuarioId: string, dados: any): Observable<Pedido> {
  return this.http.post<Pedido>(`${this.apiUrl}/do-carrinho/usuario/${usuarioId}`, dados);
}
}