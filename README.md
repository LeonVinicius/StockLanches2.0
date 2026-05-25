# 🍔 StockLanches 2.0 - Gestão de Estoque Inteligente com Microsserviços

> **Status do Projeto:** 🚧 **Em desenvolvimento** - Versão 2.0 em andamento (ambiente local)

O **StockLanches 2.0** é a evolução do sistema original de controle de estoque, agora migrando para uma **arquitetura baseada em microsserviços**. O projeto nasceu de uma necessidade real identificada em uma lanchonete local em Osasco/SP, e esta nova versão traz mais robustez e recursos avançados para gerenciamento de estoque em ambiente local.

---

## 📸 Demonstração Visual (Versão 1.0)

Confira abaixo as principais telas do sistema original (Autenticação, Gestão de Produtos e Auditoria):

<p align="center">
  <img 
    src="https://raw.githubusercontent.com/LeonVinicius/StockLanches/main/assets/tela_login.png" 
    width="45%" 
  />
  <img 
    src="https://raw.githubusercontent.com/LeonVinicius/StockLanches/main/assets/tela_dashboard.png" 
    width="45%" 
  />
</p>

<p align="center">
  <img 
    src="https://raw.githubusercontent.com/LeonVinicius/StockLanches/main/assets/tela_cadastro.png" 
    width="45%" 
  />
  <img 
    src="https://raw.githubusercontent.com/LeonVinicius/StockLanches/main/assets/tela_historico.png" 
    width="45%" 
  />
</p>

---

## 🚀 Novidades da Versão 2.0 (Em Desenvolvimento)

A nova versão está sendo construída com arquitetura de microsserviços, trazendo:

- 🔐 **Microsserviço de Autenticação (Login)** - Isolado e seguro
- 📨 **Microsserviço de Mensageria** - Comunicação assíncrona entre serviços
- 🤖 **Microsserviço de Captcha** - Proteção contra bots
- 🔄 **Integração com PDV** - Baixa automática em tempo real no estoque
- 📏 **Suporte a Múltiplas Unidades de Medida** - Kg, litros, unidades, etc.

### 🎯 Próximos Passos do Desenvolvimento

- [ ] Finalizar integração entre microsserviços
- [ ] Implementar filas RabbitMQ/Kafka para mensageria
- [ ] Testes de carga e performance
- [ ] Documentação da API (Swagger/OpenAPI)

---

## ✨ Funcionalidades (Versão 1.0 - Estável)

- **Autenticação:** Login seguro integrado ao banco de dados MySQL.
- **Gestão de Produtos (CRUD):** Cadastro, edição, consulta e exclusão de itens.
- **Monitoramento de Nível Crítico:** Alertas visuais automáticos (Normal, Baixo, Esgotado).
- **Histórico de Movimentações:** Log de auditoria que registra o tipo de ação, produto, quantidade e o usuário responsável.
- **Filtros Avançados:** Busca dinâmica por nome, categoria e ordenação.

---

## 🛠️ Tecnologias

### Versão 1.0 (Estável)
- **Linguagem:** Java 21
- **Framework:** Spring Boot 3
- **Persistência:** Spring Data JPA / Hibernate
- **Banco de Dados:** MySQL 8.0 (local)
- **Interface:** Thymeleaf & CSS3

### Versão 2.0 (Em desenvolvimento)
- **Arquitetura:** Microsserviços com Spring Boot
- **Comunicação:** REST APIs + Mensageria (RabbitMQ/Kafka)
- **API Gateway:** Spring Cloud Gateway (previsto)
- **Ambiente:** Local (sem cloud/containers por enquanto)

---

## 📺 Demonstração em Vídeo

Para ver o sistema em funcionamento e entender o contexto real da solução, assista ao vídeo no LinkedIn:

[![Assista ao vídeo do sistema](https://img.shields.io/badge/LinkedIn-Vídeo%20do%20Projeto-blue?style=for-the-badge&logo=linkedin)](https://www.linkedin.com/feed/update/urn:li:ugcPost:7416573786327715840/)

---

## 🎯 Contexto do Projeto

Este sistema faz parte de um **trabalho extensionista** com os integrantes:
- Eduardo Amorim Ramos
- Leon Vinicius
- Ricardo Frazão

**Comunidade atendida:** Pequenos empreendimentos formais (MEI, ME)  
**ODS:** 8 - Trabalho Decente e Crescimento Econômico

---

## 🚀 Como executar o projeto (Local)

### Versão 1.0 (Estável)
1. **Configuração do Banco:**
   - Crie o banco: `CREATE DATABASE stocklanches;`
   - Configure o `DB_USER` e `DB_PASSWORD` no seu ambiente ou no `application.properties`.

2. **Rodar a aplicação:**
   - Importe como projeto Maven.
   - Execute a classe `DemoApplication.java`.
   - Acesse: `http://localhost:8080`

### Versão 2.0 (Em desenvolvimento)
```bash
# Clone o repositório
git clone https://github.com/LeonVinicius/StockLanches2.0.git
cd StockLanches2.0

# Execute cada microsserviço individualmente (em desenvolvimento)
# Exemplo:
cd auth-service
mvn spring-boot:run
