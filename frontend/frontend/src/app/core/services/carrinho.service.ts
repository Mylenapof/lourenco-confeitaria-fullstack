import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { environment } from '@env/environment';
import { Carrinho, AdicionarItemRequest, AtualizarItemRequest } from '@models/carrinho.model';

@Injectable({
  providedIn: 'root'
})
export class CarrinhoService {
  private apiUrl = `${environment.apiUrl}/carrinho`;
  private carrinhoSubject = new BehaviorSubject<Carrinho | null>(null);
  public carrinho$ = this.carrinhoSubject.asObservable();

  constructor(private http: HttpClient) {}

  obterCarrinho(usuarioId: string): Observable<Carrinho> {
    return this.http.get<Carrinho>(`${this.apiUrl}/usuario/${usuarioId}`).pipe(
      tap(carrinho => this.carrinhoSubject.next(carrinho))
    );
  }

  adicionarItem(usuarioId: string, item: AdicionarItemRequest): Observable<Carrinho> {
    return this.http.post<Carrinho>(`${this.apiUrl}/usuario/${usuarioId}/item`, item).pipe(
      tap(carrinho => this.carrinhoSubject.next(carrinho))
    );
  }

  atualizarItem(usuarioId: string, itemId: string, data: AtualizarItemRequest): Observable<Carrinho> {
    return this.http.put<Carrinho>(`${this.apiUrl}/usuario/${usuarioId}/item/${itemId}`, data).pipe(
      tap(carrinho => this.carrinhoSubject.next(carrinho))
    );
  }

  removerItem(usuarioId: string, itemId: string): Observable<Carrinho> {
    return this.http.delete<Carrinho>(`${this.apiUrl}/usuario/${usuarioId}/item/${itemId}`).pipe(
      tap(carrinho => this.carrinhoSubject.next(carrinho))
    );
  }

  limparCarrinho(usuarioId: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/usuario/${usuarioId}`).pipe(
      tap(() => this.carrinhoSubject.next(null))
    );
  }

  getCarrinhoAtual(): Carrinho | null {
    return this.carrinhoSubject.value;
  }

  getTotalItens(): number {
    return this.carrinhoSubject.value?.totalItens || 0;
  }
}