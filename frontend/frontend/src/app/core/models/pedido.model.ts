export interface Pedido {
  id: string;
  usuario: any;
  itens: ItemPedido[];
  dataPedido: string;
  status: StatusPedido;
  valorTotal: number;
  enderecoEntrega: string;
  observacoes?: string;
}

export interface ItemPedido {
  produtoId: string;
  nomeProduto: string;
  quantidade: number;
  precoUnitario: number;
  subtotal: number;
}

export enum StatusPedido {
  PENDENTE = 'PENDENTE',
  CONFIRMADO = 'CONFIRMADO',
  EM_PREPARACAO = 'EM_PREPARACAO',
  PRONTO = 'PRONTO',
  ENTREGUE = 'ENTREGUE',
  CANCELADO = 'CANCELADO'
}

export interface CriarPedidoRequest {
  enderecoEntrega: string;
  observacoes?: string;
}