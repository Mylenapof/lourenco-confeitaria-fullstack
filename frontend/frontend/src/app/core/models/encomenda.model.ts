export interface Encomenda {
  id: string;
  usuario: any;
  tipoProduto: string;
  tamanho?: string;
  sabor?: string;
  decoracao?: string;
  observacoes?: string;
  dataEntrega: string;
  status: StatusEncomenda;
  valorEstimado?: number;
}

export enum StatusEncomenda {
  PENDENTE = 'PENDENTE',
  ORCAMENTO_ENVIADO = 'ORCAMENTO_ENVIADO',
  APROVADO = 'APROVADO',
  EM_PRODUCAO = 'EM_PRODUCAO',
  PRONTO = 'PRONTO',
  ENTREGUE = 'ENTREGUE',
  CANCELADO = 'CANCELADO'
}

export interface CriarEncomendaRequest {
  usuarioId?: string;
  tipoProduto: string;
  tamanho?: string;
  sabor?: string;
  decoracao?: string;
  observacoes?: string;
  dataEntrega: string;
  valorEstimado?: number;
}