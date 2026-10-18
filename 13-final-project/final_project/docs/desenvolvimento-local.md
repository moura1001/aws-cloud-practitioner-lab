# Guia de Desenvolvimento Local

## Pré-requisitos

- **Java 25** — [Eclipse Temurin JDK 25](https://adoptium.net/)
- **Docker** e **Docker Compose** — para subir a infraestrutura local
- **Maven Wrapper** (`./mvnw`) — já incluso no projeto, não requer instalação do Maven

## Subindo os Serviços de Infraestrutura

### Modo Docker (tudo em containers)

```bash
cd clone_projeto/docker/dev
docker compose up -d
```

Este modo sobe todos os serviços em containers Docker, incluindo `base-api` e `base-consumer`. Os serviços se comunicam via rede `microservice-network`.

### Modo Host (network_mode: host)

```bash
cd clone_projeto/docker/dev
docker compose -f docker-compose.host.yml up -d
```

Neste modo, apenas a infraestrutura externa sobe em containers com `network_mode: host`. Os módulos Java (`base-api` e `base-consumer`) devem ser executados diretamente na máquina host (via IDE ou `./mvnw spring-boot:run`). As portas mapeadas são as mesmas do modo Docker, mas os serviços acessam `localhost` diretamente.

---

## Estrutura de Serviços

| Serviço                  | Container                  | Porta(s)           | Descrição                              |
|--------------------------|----------------------------|--------------------|----------------------------------------|
| **localstack**           | `localstack/localstack`    | 4566, 4576         | Simula AWS (SQS, SNS, S3, CloudWatch)  |
| **sqs-admin**            | `pacovk/sqs-admin`         | 3999               | Interface web para gerenciar filas SQS |
| **valkey**               | `valkey/valkey:7`          | 6379               | Cache/dados em memória (compatível Redis) |
| **redis-insight**        | `redis/redisinsight`       | 5540               | Interface web para inspecionar Redis/Valkey |
| **mysql**                | `mysql:8.0`                | 3306               | Banco de dados relacional              |
| **adminer**              | `adminer`                  | 8090               | Interface web para gerenciar MySQL     |
| **grafana**              | `grafana/grafana`          | 3000               | Dashboards e visualização              |
| **loki**                 | `grafana/loki:2.9.0`       | 3100               | Agregação de logs                      |
| **tempo**                | `grafana/tempo`            | 3200, 4318         | Tracing distribuído (OTLP)             |
| **external-service-mock**| `mendhak/http-https-echo`  | 8888               | Mock de serviço externo                |
| **base-api**             | (build local)              | 8080               | Módulo API REST                        |
| **base-consumer**        | (build local)              | 8082               | Módulo consumidor de filas SQS         |

### Credenciais

| Serviço  | Usuário             | Senha    | Database     |
|----------|---------------------|----------|--------------|
| MySQL    | `root`              | `root123`| -            |
| MySQL    | `microservice_user` | `senha123`| `microservice`|
| AWS      | `test` (Access Key) | `test` (Secret Key) | `us-east-1` |

---

## Filas SQS

As filas são criadas automaticamente pelo Localstack na inicialização:

| Fila                  | Descrição                                  |
|-----------------------|--------------------------------------------|
| `input-queue`         | Fila de entrada para processamento         |
| `output-queue`        | Fila de saída com resultados               |
| `dlq-queue`           | Dead Letter Queue para mensagens com falha |
| `notification-queue`  | Fila de notificações                       |

**URLs das filas**:

```
http://sqs.us-east-1.localhost.localstack.cloud:4566/000000000000/input-queue
http://sqs.us-east-1.localhost.localstack.cloud:4566/000000000000/output-queue
http://sqs.us-east-1.localhost.localstack.cloud:4566/000000000000/dlq-queue
http://sqs.us-east-1.localhost.localstack.cloud:4566/000000000000/notification-queue
```

---

## Construindo e Executando Localmente

### Compilar o projeto

```bash
./mvnw clean package -DskipTests
```

### Executar a API

```bash
# Com infraestrutura em modo host
./mvnw -pl base-api spring-boot:run -Dspring-boot.run.profiles=local
```

A API sobe na porta **8080**.

### Executar o Consumer

```bash
# Com infraestrutura em modo host
./mvnw -pl base-consumer spring-boot:run -Dspring-boot.run.profiles=local
```

O Consumer sobe na porta **8082**.

### Variáveis de Ambiente (modo host)

Antes de executar, certifique-se de que as seguintes variáveis estejam definidas (ou use os defaults do `application-local.yml`):

```bash
export VALKEY_URL=redis://localhost:6379
export VALKEY_HOST=localhost
export VALKEY_PORT=6379
export AWS_LOCALSTACK_ENDPOINT=http://localhost:4566
export MANAGEMENT_OTLP_TRACING_ENDPOINT=http://localhost:4318/v1/traces
export MANAGEMENT_TRACING_ENABLED=true
export AMBIENTE=local
```

---

## Acessando as Ferramentas

| Ferramenta        | URL                                  |
|-------------------|--------------------------------------|
| **base-api**      | http://localhost:8080                |
| **base-consumer** | http://localhost:8082                |
| **Grafana**       | http://localhost:3000                |
| **sqs-admin**     | http://localhost:3999                |
| **Adminer**       | http://localhost:8090                |
| **Redis Insight** | http://localhost:5540                |

### Actuator Endpoints

| Endpoint                    | Descrição                |
|-----------------------------|--------------------------|
| `GET /health`               | Health básico            |
| `GET /health/live`          | Liveness (vivo/morto)    |
| `GET /health/ready`         | Readiness (pronto)       |
| `GET /actuator/health`      | Health detalhado         |
| `GET /actuator/prometheus`  | Métricas para Prometheus |
| `GET /actuator/metrics`     | Lista de métricas        |
| `GET /actuator/info`        | Informações da aplicação |

---

## Dicas de Debug

### Verificar Logs

```bash
# Logs da API
docker compose -f docker-compose.host.yml logs -f base-api

# Logs do Consumer
docker compose -f docker-compose.host.yml logs -f base-consumer

# Ou, se executando via Maven, os logs aparecem no stdout
```

### Testar a API com curl

```bash
# Health check
curl http://localhost:8080/health

# Health do Actuator
curl http://localhost:8080/actuator/health | jq

# Métricas Prometheus
curl http://localhost:8080/actuator/prometheus

# Enviar uma requisição para a API (exemplo)
curl -X POST http://localhost:8080/api/process \
  -H "Content-Type: application/json" \
  -H "X-API-Key: dev-secret" \
  -d '{"id": "test-123", "data": "payload de teste"}'
```

### Inspecionar Filas SQS

Acesse **http://localhost:3999** (sqs-admin) para visualizar e gerenciar as filas:

- Ver mensagens em cada fila
- Enviar mensagens manualmente
- Purgar filas

### Inspecionar Redis/Valkey

Acesse **http://localhost:5540** (Redis Insight) para:

- Visualizar chaves armazenadas
- Monitorar o circuito (chave `app:external-service:circuit`)
- Executar comandos Redis manualmente

### Verificar Traces no Grafana

1. Acesse **http://localhost:3000** → Dashboard **"JWatcher Service Overview"**
2. Expanda a seção **Tracing (Tempo)**: veja o **Span Rate** e o **Waterfall API + Consumer**
3. Use a textbox **Correlation ID** para filtrar traces/logs por correlationId
4. Ou vá em **Explore** → datasource **Tempo** e busque via TraceQL: `{span.correlationId =~ ".*xxx.*"}`

### Verificar Logs no Grafana

1. Acesse **http://localhost:3000** → Dashboard → **Rastreamento de Requisição**
2. Digite um correlationId na textbox do topo para ver os logs do fluxo
3. Ou em **Explore** → datasource **Loki**: `{app="base-api"} |= "correlationId"`

---

## Fluxo de Exemplo

### 1. Enviar uma requisição para a API

```bash
curl -X POST http://localhost:8080/api/process \
  -H "Content-Type: application/json" \
  -H "X-API-Key: dev-secret" \
  -d '{"id": "fluxo-teste-001", "data": "exemplo de processamento"}'
```

### 2. Verificar a fila de entrada

Acesse **http://localhost:3999**, selecione a fila `input-queue` e veja a mensagem publicada.

### 3. Verificar o processamento pelo Consumer

```bash
# Acompanhe os logs do consumer
docker compose -f docker-compose.host.yml logs -f base-consumer
```

Você verá logs com o `correlationId` e `traceId` da requisição.

### 4. Verificar a fila de saída

No sqs-admin, selecione `output-queue` para ver o resultado do processamento.

### 5. Rastrear no Grafana

1. Copie o `correlationId` dos logs do consumer
2. No Grafana → Dashboard → **Tracing (Tempo)**, digite o correlationId na textbox
3. O painel Waterfall mostra os spans da API e do Consumer para aquele correlationId
4. Clique no **TraceID** para abrir o waterfall detalhado de spans no Explore