import { Categoria } from './categoria.model';

export interface Produto {
  id: string;
  nome: string;
  preco: number;
  ingredientes: string;
  imagemUrl: string;
  disponivel: boolean;
  destaque: boolean;
  categoria: Categoria;
}

export interface PaginatedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}