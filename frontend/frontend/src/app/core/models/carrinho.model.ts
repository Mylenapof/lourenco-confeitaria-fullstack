export interface Carrinho {
  carrinhoId: string;
  usuarioId: string;
  itens: ItemCarrinho[];
  totalItens: number;
  valorTotal: number;
  dataAtualizacao: string;
}

export interface ItemCarrinho {
  itemId: string;
  produtoId: string;
  produtoNome: string;
  produtoImagemUrl: string;
  precoUnitario: number;
  quantidade: number;
  subtotal: number;
  produtoDisponivel: boolean;
}

export interface AdicionarItemRequest {
  produtoId: string;
  quantidade: number;
}

export interface AtualizarItemRequest {
  quantidade: number;
}