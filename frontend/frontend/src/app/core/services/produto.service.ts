import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '@env/environment';
import { Produto, PaginatedResponse } from '@models/produto.model';

@Injectable({
  providedIn: 'root'
})
export class ProdutoService {
  private apiUrl = `${environment.apiUrl}/produtos`;

  constructor(private http: HttpClient) {}

  listarTodos(): Observable<Produto[]> {
    return this.http.get<Produto[]>(this.apiUrl);
  }

  listarDisponiveis(): Observable<Produto[]> {
    return this.http.get<Produto[]>(`${this.apiUrl}/disponiveis`);
  }

  listarDestaques(): Observable<Produto[]> {
    return this.http.get<Produto[]>(`${this.apiUrl}/destaques`);
  }

  listarPaginado(page: number = 0, size: number = 10): Observable<PaginatedResponse<Produto>> {
    return this.http.get<PaginatedResponse<Produto>>(`${this.apiUrl}/page`, {
      params: { page: page.toString(), size: size.toString() }
    });
  }

  buscarPorId(id: string): Observable<Produto> {
    return this.http.get<Produto>(`${this.apiUrl}/${id}`);
  }

  criar(produto: any): Observable<Produto> {
    return this.http.post<Produto>(this.apiUrl, produto);
  }

  atualizar(id: string, produto: any): Observable<Produto> {
    return this.http.put<Produto>(`${this.apiUrl}/${id}`, produto);
  }

  toggleDestaque(id: string): Observable<Produto> {
    return this.http.patch<Produto>(`${this.apiUrl}/${id}/destaque`, {});
  }

  toggleDisponibilidade(id: string): Observable<Produto> {
    return this.http.patch<Produto>(`${this.apiUrl}/${id}/disponibilidade`, {});
  }

  deletar(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}