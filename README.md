# Sample API - Arquitetura Hexagonal com Virtual Threads

Este projeto demonstra a implementação de uma arquitetura hexagonal (Ports and Adapters) usando Spring Boot 3.5.0 e Java 21 com Virtual Threads para paralelismo sem reatividade.

## 🏗️ Arquitetura

### Estrutura do Projeto

```
src/main/java/guichafy/sample_api/
├── application/
│   ├── ports/
│   │   ├── input/          # Interfaces de casos de uso
│   │   └── output/         # Interfaces para adaptadores externos
│   └── usecases/           # Implementação dos casos de uso
├── domain/
│   ├── entities/           # Entidades de domínio
│   └── valueobjects/       # Objetos de valor
└── infrastructure/
    ├── adapters/
    │   ├── input/web/      # Controladores REST
    │   └── output/         # Adaptadores para APIs externas
    └── config/             # Configurações do Spring
```

### Camadas da Arquitetura

#### 🎯 **Domain (Domínio)**
- **Entities**: `Route`, `User`
- **Value Objects**: `RouteId`, `UserId`
- Contém a lógica de negócio pura, sem dependências externas

#### 🔄 **Application (Aplicação)**
- **Input Ports**: `GetRouteUseCase`, `GetRoutesUseCase`
- **Output Ports**: `RouteApiPort`, `UserApiPort`
- **Use Cases**: `RouteService`, `UserService`
- Orquestra a lógica de negócio e define contratos

#### 🌐 **Infrastructure (Infraestrutura)**
- **Input Adapters**: `SitemapController`, `UserController`
- **Output Adapters**: `ExternalRouteApiAdapter`
- **Configuration**: `ApplicationConfig`
- Implementa detalhes técnicos e integrações

## 🚀 Virtual Threads

### Configuração

O projeto utiliza Virtual Threads do Java 21 para paralelismo:

```java
@Bean("virtualThreadTaskExecutor")
public AsyncTaskExecutor virtualThreadTaskExecutor() {
    return new TaskExecutorAdapter(Executors.newVirtualThreadPerTaskExecutor());
}
```

### Uso no RouteService

```java
public Optional<Route> getRouteById(RouteId routeId) {
    // Execução paralela usando CompletableFuture com Virtual Threads
    CompletableFuture<Optional<Route>> routeFuture = routeApiPort.findRouteByIdAsync(routeId);
    CompletableFuture<Boolean> statusFuture = routeApiPort.isRouteActiveAsync(routeId);
    
    // Aguarda ambas as operações completarem
    CompletableFuture.allOf(routeFuture, statusFuture).join();
    
    // Processa resultados...
}
```

### Vantagens dos Virtual Threads

- ✅ **Simplicidade**: Código síncrono familiar
- ✅ **Performance**: Milhares de threads com baixo overhead
- ✅ **Compatibilidade**: Funciona com APIs síncronas existentes
- ✅ **Debugging**: Stack traces mais claros que reactive streams

## 📡 Sistema de Sitemap

### Endpoints Disponíveis

```http
# Buscar rota por ID
GET /api/sitemap/routes/{id}

# Listar todas as rotas com filtros
GET /api/sitemap/routes?page=0&size=10&method=GET&active=true

# Buscar apenas rotas ativas
GET /api/sitemap/routes/active

# Buscar rotas por método HTTP
GET /api/sitemap/routes/method/{method}

# Buscar rotas por tags
GET /api/sitemap/routes/tags?tags=api,users
```

### Exemplo de Resposta

```json
{
  "id": "route-123",
  "path": "/api/users",
  "name": "User Management",
  "description": "Endpoints for user operations",
  "method": "GET",
  "tags": ["api", "users"],
  "metadata": {
    "version": "1.0",
    "public": true
  },
  "active": true,
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-01-15T14:20:00"
}
```

## 🛠️ Tecnologias

- **Java 21** - Virtual Threads
- **Spring Boot 3.5.0** - Framework principal
- **Spring Web** - REST APIs
- **Spring Validation** - Validação de dados
- **JUnit 5** - Testes unitários
- **Mockito** - Mocks para testes
- **Spring Test** - Testes de integração

## 🚦 Como Executar

### Pré-requisitos

- Java 21+
- Maven 3.8+

### Executar a Aplicação

```bash
# Compilar o projeto
mvn clean compile

# Executar testes
mvn test

# Executar a aplicação
mvn spring-boot:run
```

### Configuração

Edite o arquivo `application.yml`:

```yaml
app:
  external-api:
    base-url: ${EXTERNAL_API_URL:https://api.example.com}
    timeout:
      connect: 10s
      read: 30s

spring:
  threads:
    virtual:
      enabled: true
```

## 🧪 Testes

### Executar Testes

```bash
# Todos os testes
mvn test

# Apenas testes unitários
mvn test -Dtest="*Test"

# Apenas testes de integração
mvn test -Dtest="*IntegrationTest"
```

### Cobertura de Testes

- ✅ Testes unitários para `RouteService`
- ✅ Testes de integração para `SitemapController`
- ✅ Mocks para adaptadores externos
- ✅ Validação de contratos entre camadas

## 📚 Conceitos Demonstrados

### Arquitetura Hexagonal

1. **Separação de Responsabilidades**: Cada camada tem uma responsabilidade específica
2. **Inversão de Dependência**: O domínio não depende da infraestrutura
3. **Testabilidade**: Fácil criação de mocks e testes isolados
4. **Flexibilidade**: Fácil troca de implementações

### Virtual Threads vs Reactive

| Aspecto | Virtual Threads | Reactive |
|---------|----------------|----------|
| **Sintaxe** | Síncrona familiar | Assíncrona complexa |
| **Debugging** | Stack traces claros | Difícil rastreamento |
| **Curva de Aprendizado** | Baixa | Alta |
| **Performance** | Excelente para I/O | Excelente para streams |
| **Compatibilidade** | APIs síncronas | Requer APIs reativas |

## 🔄 Fluxo de Execução

1. **Request** → `SitemapController` (Input Adapter)
2. **Controller** → `RouteService` (Use Case)
3. **Service** → `RouteApiPort` (Output Port)
4. **Port** → `ExternalRouteApiAdapter` (Output Adapter)
5. **Adapter** → API Externa (Virtual Threads)
6. **Response** ← Caminho inverso

## 🎯 Benefícios da Implementação

- **Manutenibilidade**: Código organizado e testável
- **Performance**: Paralelismo eficiente com Virtual Threads
- **Escalabilidade**: Arquitetura preparada para crescimento
- **Flexibilidade**: Fácil adição de novos adaptadores
- **Simplicidade**: Sem complexidade desnecessária do modelo reativo

## 📖 Próximos Passos

- [ ] Implementar cache com Redis
- [ ] Adicionar métricas com Micrometer
- [ ] Implementar circuit breaker
- [ ] Adicionar documentação OpenAPI
- [ ] Implementar autenticação JWT