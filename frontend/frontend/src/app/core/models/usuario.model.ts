export interface Usuario {
  id: string;
  nome: string;
  email: string;
  cpf?: string;
  role: string;
  telefone?: string;
  endereco?: string;
  ativo: boolean;
}

export interface LoginRequest {
  email: string;
  senha: string;
}

export interface LoginResponse {
  message: string;
  token: string;
}

export interface RegistroRequest {
  nome: string;
  email: string;
  senha: string;
  cpf: string;
  telefone?: string;
  endereco?: string;
  role?: string;
}