# 💈 Barbearia-App

Sistema completo para gestão de uma barbearia, com **API em ASP.NET Core** e **aplicativo Android em Jetpack Compose**. Permite o gerenciamento de barbeiros, clientes, serviços, produtos e agendamentos com integração entre front-end e back-end.

---

## 📦 Tecnologias Utilizadas

### Back-end (.NET 7)
- ASP.NET Core Web API
- Entity Framework Core
- SQL Server (banco remoto)
- Swagger para testes de API

### Front-end (Android)
- Jetpack Compose
- Ktor Client para chamadas HTTP
- Kotlinx Serialization
- MVVM Architecture

---

## 🧠 Funcionalidades

### 🔹 API RESTful
Cada entidade possui um CRUD completo:

- **Barbeiro**: cadastrar, listar, editar, excluir
- **Cliente**: cadastrar, listar, editar, excluir
- **Serviço**: cadastrar, listar, editar, excluir
- **Produto**: cadastrar, listar, editar, excluir
- **Agendamento**: cadastrar, listar, editar, excluir, atualizar status

### 🔹 Aplicativo Android
- Tela de login (em desenvolvimento)
- Listagem de agendamentos
- Cadastro de novos agendamentos
- Comunicação direta com a API via Ktor

---

## 🚀 Como rodar o projeto

### 🔧 Back-end

```bash
# Clone o repositório
git clone https://github.com/BUR1T1/Barbearia-App.git
cd Barbearia-App

# Configure a string de conexão no appsettings.json
# Exemplo:
"ConnectionStrings": {
  "RemoteConnection": "Server=SEU_SERVIDOR;Database=BarberTechDB;User Id=USUARIO;Password=SENHA;TrustServerCertificate=True;"
}

# Instale o EF CLI (se necessário)
dotnet tool install --global dotnet-ef --version 7.0.16

# Crie e aplique as migrations
dotnet ef migrations add InitialCreate
dotnet ef database update

# Rode a API
dotnet run
