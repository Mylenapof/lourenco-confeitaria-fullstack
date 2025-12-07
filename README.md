# 🍰 Sistema de Gerenciamento - Lourenço Confeitaria

Sistema completo de e-commerce para confeitaria com gestão de pedidos, encomendas personalizadas, pagamento via PIX e painel administrativo.


---

## 📋 Índice

- [Sobre o Projeto](#sobre-o-projeto)
- [Funcionalidades](#funcionalidades)
- [Tecnologias Utilizadas](#tecnologias-utilizadas)
- [Arquitetura](#arquitetura)
- [Pré-requisitos](#pré-requisitos)
- [Instalação](#instalação)
- [Configuração](#configuração)
- [Executando o Projeto](#executando-o-projeto)
- [Estrutura do Projeto](#estrutura-do-projeto)
- [API Endpoints](#api-endpoints)
- [Testes](#testes)
- [Deploy](#deploy)
- [Contribuindo](#contribuindo)
- [Licença](#licença)
- [Contato](#contato)

---

## 📖 Sobre o Projeto

Sistema desenvolvido para modernizar e otimizar a gestão de uma confeitaria artesanal, permitindo que clientes façam pedidos online, solicitem encomendas personalizadas e realizem pagamentos via PIX, enquanto administradores gerenciam todo o negócio através de um painel completo.

### 🎯 Objetivos

- Facilitar o processo de compra para clientes
- Automatizar o controle de pedidos e encomendas
- Fornecer relatórios gerenciais em tempo real
- Integrar pagamentos digitais (PIX)
- Melhorar a experiência do usuário

---

## ✨ Funcionalidades

### 👥 Área do Cliente

#### 🔐 Autenticação e Cadastro
- Registro com validação de CPF
- Login seguro com JWT
- Recuperação de senha

#### 🛒 Carrinho de Compras
- Adicionar/remover produtos
- Ajustar quantidades
- Persistência de dados

#### 💳 Checkout e Pagamento
- Pagamento via PIX
- QR Code e Copia e Cola
- Confirmação automática
- Tempo de expiração (30 minutos)

#### 🎂 Encomendas Personalizadas
- Solicitar bolos customizados
- Definir decoração e sabor
- Receber orçamento
- Acompanhar produção

#### 📦 Acompanhamento de Pedidos
- Status em tempo real
- Histórico completo
- Notificações de atualização

### 👨‍💼 Área Administrativa

#### 📊 Dashboard Gerencial
- Estatísticas de vendas
- Faturamento total e mensal
- Ticket médio
- Gráficos e indicadores

#### 🍰 Gestão de Produtos
- CRUD completo
- Upload de imagens
- Controle de disponibilidade
- Produtos em destaque

#### 📋 Gestão de Pedidos
- Visualizar todos os pedidos
- Atualizar status
- Filtros por status
- Detalhes completos

#### 🎂 Gestão de Encomendas
- Receber solicitações
- Enviar orçamentos
- Acompanhar produção
- Prazos de entrega

#### 📈 Relatórios
- Produtos mais vendidos
- Vendas por período
- Análise de faturamento
- Exportação de dados

#### 🔔 Sistema de Notificações
- Notificações automáticas
- Alertas de novos pedidos
- Atualizações de status

---

## 🚀 Tecnologias Utilizadas

### Backend
- **Java 17** - Linguagem de programação
- **Spring Boot 3.x** - Framework principal
- **Spring Security** - Autenticação e autorização
- **Spring Data JPA** - ORM
- **Spring Validation** - Validação de dados
- **PostgreSQL** - Banco de dados relacional
- **JWT** - Autenticação via tokens
- **Lombok** - Redução de boilerplate
- **Maven** - Gerenciamento de dependências

### Frontend
- **Angular 18** - Framework JavaScript
- **TypeScript** - Linguagem tipada
- **Angular Material** - Componentes UI
- **RxJS** - Programação reativa
- **SCSS** - Pré-processador CSS
- **Standalone Components** - Arquitetura moderna

### DevOps & Tools
- **Git** - Controle de versão
- **Docker** (opcional) - Containerização
- **Postman** - Testes de API

---
---

## 📋 Pré-requisitos

### Backend
- Java JDK 17 ou superior
- Maven 3.8+
- PostgreSQL 15+
- IDE (IntelliJ IDEA, Eclipse, VSCode)

### Frontend
- Node.js 18+ e npm
- Angular CLI 18

---

## 🔧 Instalação

### 1. Clone o Repositório
### 2. Backend Setup
#### Configure o Banco de Dados
Crie um banco PostgreSQL:
#### Configure o application.properties
#### Instale as Dependências
### 3. Frontend Setup

#### Configure o Ambiente
Edite `src/environments/environment.ts`:

## ▶️ Executando o Projeto

### Iniciar o Backend
O backend estará disponível em: [**http://localhost:8080**](http://localhost:8080)

### Iniciar o Frontend
O frontend estará disponível em: [**http://localhost:4200**](http://localhost:4200)

### Acessar o Sistema
- **URL:** http://localhost:4200
- **Admin:** Criar via endpoint `/auth/register` com role: "ADMIN"
- **Cliente:** Cadastrar pela interface

---
## 📁 Estrutura do Projeto
lourenco-confeitaria/
│
├── backend/
│ ├── src/
│ │ ├── main/
│ │ │ ├── java/com/lourenco/backend/
│ │ │ │ ├── controlador/
│ │ │ │ ├── serviço/
│ │ │ │ ├── repositório/
│ │ │ │ ├── modelo/
│ │ │ │ ├── dto/
│ │ │ │ ├── segurança/
│ │ │ │ ├── config/
│ │ │ │ └── exceção/
│ │ │ └── recursos/
│ │ │ └── application.properties
│ │ └── test/
│ └── pom.xml
│
├── frontend/
│ ├── src/
│ │ ├── app/
│ │ │ ├── core/
│ │ │ │ ├── guardas/
│ │ │ │ ├── interceptadores/
│ │ │ │ ├── serviços/
│ │ │ │ └── modelos/
│ │ │ ├── páginas/
│ │ │ │ ├── home/
│ │ │ │ ├── cardapio/
│ │ │ │ ├──carrinho/
│ │ │ │ ├── checkout/
│ │ │ │ ├── admin/
│ │ │ │ └── ...
│ │ │ └── components/
│ │ ├── assets/
│ │ └── environments/
│ └── angular.json
│
├── docs/
│ ├── manual-usuario.md
│ └── api-docs.md
│
└── README.md
---

## 🌐 API Endpoints

### Autenticação
| Método | Endpoint | Descrição | Auth |
|--------|----------|-----------|------|
| POST | `/auth/register` | Cadastrar usuário | Não |
| POST | `/auth/login` | Login | Não |

### Produtos
| Método | Endpoint | Descrição | Auth |
|--------|----------|-----------|------|
| GET | `/produtos` | Listar todos | Não |
| GET | `/produtos/{id}` | Buscar por ID | Não |
| POST | `/produtos` | Criar produto | Admin |
| PUT | `/produtos/{id}` | Atualizar produto | Admin |
| DELETE | `/produtos/{id}` | Deletar produto | Admin |

### Carrinho
| Método | Endpoint | Descrição | Auth |
|--------|----------|-----------|------|
| GET | `/carrinho/usuario/{id}` | Obter carrinho | User |
| POST | `/carrinho/usuario/{id}/item` | Adicionar item | User |
| PUT | `/carrinho/usuario/{id}/item/{itemId}` | Atualizar item | User |
| DELETE | `/carrinho/usuario/{id}/item/{itemId}` | Remover item | User |

### Pedidos
| Método | Endpoint | Descrição | Auth |
|--------|----------|-----------|------|
| GET | `/pedidos` | Listar todos | Admin |
| GET | `/pedidos/usuario/{id}` | Pedidos do usuário | User |
| POST | `/pedidos/do-carrinho/usuario/{id}` | Criar do carrinho | User |
| PATCH | `/pedidos/{id}/status` | Atualizar status | Admin |

### Encomendas
| Método | Endpoint | Descrição | Auth |
|--------|----------|-----------|------|
| GET | `/encomendas` | Listar todas | Admin |
| GET | `/encomendas/usuario/{id}` | Do usuário | User |
| POST | `/encomendas` | Criar encomenda | Público |
| PATCH | `/encomendas/{id}/status` | Atualizar status | Admin |

### Pagamentos
| Método | Endpoint | Descrição | Auth |
|--------|----------|-----------|------|
| POST | `/pagamentos/pedido/{id}/pix` | Gerar PIX | User |
| POST | `/pagamentos/{id}/simular-pagamento` | Simular pagamento | User |

### Relatórios
| Método | Endpoint | Descrição | Auth |
|--------|----------|-----------|------|
| GET | `/relatorios/dashboard` | Dashboard completo | Admin |
| GET | `/relatorios/produtos-mais-vendidos` | Top produtos | Admin |
| GET | `/relatorios/vendas/ultimos-30-dias` | Vendas 30 dias | Admin |


