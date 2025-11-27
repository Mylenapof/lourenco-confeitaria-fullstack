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

// 🔹 ATUALIZADO: Adicionar nome e role
export interface LoginResponse {
  message: string;
  token: string;
  nome?: string;      // 🔹 ADICIONAR
  role?: string;      // 🔹 ADICIONAR
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