# 🍰 Sistema de Gerenciamento - Lourenço Confeitaria

## 🥇 Visão Geral

Sistema completo de e-commerce desenvolvido para a Lourenço Confeitaria, oferecendo gestão de pedidos, a possibilidade de encomendas personalizadas, checkout com pagamento via PIX (simulado) e um painel administrativo robusto para gerenciar todo o negócio.



---
## 📋 Índice

1.  [Sobre o Projeto](#-sobre-o-projeto)
2.  [Funcionalidades](#-funcionalidades)
3.  [Tecnologias Utilizadas](#-tecnologias-utilizadas)
4.  [Arquitetura](#-arquitetura)
5.  [Pré-requisitos](#-pr-requisitos)
6.  [Instalação](#-instalao)
7.  [Executando o Projeto](#-executando-o-projeto)
8.  [API Endpoints](#-api-endpoints)
9.  [Testes](#-testes)
10. [Deploy](#-deploy)
11. [Roadmap](#-roadmap)
12. [Contribuindo](#-contribuindo)
13. [Licença](#-licena)
14. [Contato](#-contato)

---

## ✨ Funcionalidades

### 👥 Área do Cliente

| Recurso | Detalhes |
| :--- | :--- |
| **Autenticação** | Registro com validação de CPF, Login seguro com JWT e recuperação de senha. |
| **Carrinho** | Adicionar/remover produtos e persistência de dados. |
| **Checkout PIX** | Pagamento via **PIX** (QR Code/Copia e Cola), confirmação automática (simulada), expiração (30 minutos). |
| **Encomendas** | Solicitar bolos customizados, definir decoração/sabor, receber orçamento e acompanhar produção. |
| **Acompanhamento** | Status de pedidos em tempo real, histórico completo. |

### 👨‍💼 Área Administrativa

| Recurso | Detalhes |
| :--- | :--- |
| **Dashboard** | Estatísticas de vendas, faturamento total/mensal e indicadores. |
| **Gestão de Produtos** | **CRUD** completo, upload de imagens e controle de disponibilidade. |
| **Gestão de Pedidos** | Visualizar todos os pedidos, atualizar status e filtros por status. |
| **Gestão de Encomendas** | Receber solicitações, enviar orçamentos e acompanhar produção. |
| **Relatórios** | Produtos mais vendidos, vendas por período e exportação de dados. |
| **Notificações** | Alertas de novos pedidos e atualizações de status.

---
## ✨ Funcionalidades

### 👥 Área do Cliente

| Recurso | Detalhes |
| :--- | :--- |
| **Autenticação** | Registro com validação de CPF, Login seguro com JWT e recuperação de senha. |
| **Carrinho** | Adicionar/remover produtos e persistência de dados. |
| **Checkout PIX** | Pagamento via **PIX** (QR Code/Copia e Cola), confirmação automática (simulada), expiração (30 minutos). |
| **Encomendas** | Solicitar bolos customizados, definir decoração/sabor, receber orçamento e acompanhar produção. |
| **Acompanhamento** | Status de pedidos em tempo real, histórico completo. |

### 👨‍💼 Área Administrativa

| Recurso | Detalhes |
| :--- | :--- |
| **Dashboard** | Estatísticas de vendas, faturamento total/mensal e indicadores. |
| **Gestão de Produtos** | **CRUD** completo, upload de imagens e controle de disponibilidade. |
| **Gestão de Pedidos** | Visualizar todos os pedidos, atualizar status e filtros por status. |
| **Gestão de Encomendas** | Receber solicitações, enviar orçamentos e acompanhar produção. |
| **Relatórios** | Produtos mais vendidos, vendas por período e exportação de dados. |
| **Notificações** | Alertas de novos pedidos e atualizações de status.

---
## 📋 Pré-requisitos

* **Backend:** Java JDK 17+, Maven 3.8+, PostgreSQL 15+
* **Frontend:** Node.js 18+, npm, Angular CLI 18

---

## 🔧 Instalação

### 1. Clone o Repositório

```bash
git clone [https://github.com/seu-usuario/lourenco-confeitaria.git](https://github.com/seu-usuario/lourenco-confeitaria.git)
cd lourenco-confeitaria
cd backend
# 1. Crie o banco PostgreSQL (confeitaria_db) e configure o usuário (confeitaria_user).
# 2. Edite src/main/resources/application.properties com suas credenciais e chave JWT.
mvn clean instal

cd ../frontend
npm install
# Edite src/environments/environment.ts para confirmar a apiUrl.
### 8. Execução e Endpoints

```markdown
## ▶️ Executando o Projeto

### Iniciar o Backend
```bash
cd backend
mvn spring-boot:run
### 9. Testes e Deploy

```markdown
## 🧪 Testes

### Backend
```bash
cd backend
mvn test
### 10. Contribuição, Licença e Contato

```markdown
## 🔮 Roadmap

* Integração com gateway de pagamento real.
* Desenvolvimento de App mobile (React Native).
* Sistema de cupons de desconto e programa de fidelidade.

### 🐛 Problemas Conhecidos

* PIX é simulado (não integrado com gateway real).
* Upload de imagens funciona apenas localmente.
* Notificações em tempo real não implementadas.

---

## 🤝 Contribuindo

Contribuições são bem-vindas!
1.  Fork o projeto.
2.  Crie uma branch (`git checkout -b feature/MinhaFeature`).
3.  Commit suas mudanças (`git commit -m 'feat: Adiciona MinhaFeature'`).
4.  Abra um **Pull Request**.

---

## 📝 Licença

Este projeto está sob a **licença MIT**. Veja o arquivo `LICENSE` para mais detalhes.

---

## 👨‍💻 Contato

| Item | Detalhe |
| :--- | :--- |
| **Autor** | Seu Nome |
| **GitHub** | [@seu-usuario](https://github.com/seu-usuario) |
| **Email** | seu.email@example.com |
