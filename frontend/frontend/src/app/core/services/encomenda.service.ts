import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '@env/environment';
import { Encomenda } from '@models/encomenda.model';

@Injectable({
  providedIn: 'root'
})
export class EncomendaService {
  private apiUrl = `${environment.apiUrl}/encomendas`;

  constructor(private http: HttpClient) {}

  criar(encomenda: any): Observable<Encomenda> {
    return this.http.post<Encomenda>(this.apiUrl, encomenda);
  }

  listarPorUsuario(usuarioId: string): Observable<Encomenda[]> {
    return this.http.get<Encomenda[]>(`${this.apiUrl}/usuario/${usuarioId}`);
  }

  buscarPorId(id: string): Observable<Encomenda> {
    return this.http.get<Encomenda>(`${this.apiUrl}/${id}`);
  }
}