# 11 — CloudWatch

## Objetivo

Estudar o Amazon CloudWatch como serviço de observabilidade e monitoramento da AWS, entendendo:

* métricas;
* estatísticas;
* períodos de avaliação;
* dimensões;
* logs;
* alarmes;
* estados de alarmes;
* integração com SQS;
* diferença entre CloudWatch, CloudTrail e EventBridge;
* relação entre CloudWatch e Auto Scaling;
* cuidados com custos.

No laboratório prático, foi criado um alarme do CloudWatch baseado na quantidade aproximada de mensagens visíveis em uma fila SQS.

O laboratório seguiu o fluxo:

```text
entender → criar → testar → observar → documentar → destruir
```

---

# 1. Diagnóstico inicial

## 1.1 O que é o CloudWatch?

**Minha resposta:**

> É um sistema de armazenamento de métricas, logs e disparo de alarmes utilizado no contexto de monitoramento de aplicações

### Correção

A resposta está parcialmente correta.

O CloudWatch é principalmente um serviço de **monitoramento e observabilidade** da AWS. Ele permite coletar, visualizar, consultar e reagir a informações provenientes de recursos, aplicações e serviços.

Ele trabalha principalmente com:

* métricas;
* logs;
* alarmes;
* dashboards;
* consultas de logs;
* eventos e integrações com outros serviços.

O armazenamento de dados é parte da funcionalidade, mas definir CloudWatch apenas como um sistema de armazenamento reduz demais seu papel.

### Ponto de atenção para CLF-C02

CloudWatch está relacionado a **monitoramento e observabilidade**.

Não confundir com:

* **CloudTrail** → registro de atividades e chamadas de API;
* **EventBridge** → roteamento de eventos e automação baseada em eventos.

---

## 1.2 Qual a diferença entre métrica, log e evento?

**Minha resposta:**

> Não sei exatamente mas sei que existe o conceito de log group, eventos dentro de group e dentro do grupo é que teriam os logs

### Correção

A estrutura dos logs do CloudWatch é:

```text
Log Group
└── Log Stream
    └── Log Events
```

* **Log Group**: agrupa logs relacionados;
* **Log Stream**: sequência de eventos provenientes de uma fonte específica;
* **Log Event**: registro individual de log.

Já uma **métrica** representa dados numéricos normalmente organizados como uma série temporal.

Exemplo:

```text
CPUUtilization
10:00 → 25%
10:01 → 31%
10:02 → 42%
```

Um evento é um conceito diferente, especialmente quando falamos de serviços como EventBridge.

### Ponto de atenção

Não confundir:

```text
Log Event ≠ CloudWatch Metric ≠ EventBridge Event
```

---

## 1.3 O que podemos observar de uma instância EC2?

**Minha resposta:**

> É possível observar métricas de consumo de memória e CPU, além dos logs emitidos pela aplicação com filtros e intervalos de data, etc

### Correção

A ideia está correta, mas existe uma diferença importante.

O CloudWatch fornece algumas métricas padrão de EC2, como:

* CPU;
* tráfego de rede;
* operações de disco;
* status checks.

A **memória**, por outro lado, normalmente não é uma métrica padrão fornecida diretamente pela EC2. Para coletá-la é necessário instalar/configurar o **CloudWatch Agent**.

Isso é importante porque:

```text
EC2
 ├── métricas padrão → CloudWatch
 └── memória/processos/etc. → normalmente CloudWatch Agent
```

Os logs da aplicação também podem ser enviados para o CloudWatch Logs através de mecanismos apropriados, como o CloudWatch Agent ou integrações dos próprios serviços.

---

## 1.4 O que é CPUUtilization?

**Minha resposta:**

> CPUUtilization é a métrica de porcentagem de CPU utilizada pela instância. Também sei que existem variações como CPU alocada e mínimo, média e máximo

### Correção

A primeira parte está correta.

`CPUUtilization` é uma métrica que representa a utilização da CPU.

Porém:

```text
CPUUtilization
```

é a **métrica**.

Já:

```text
Minimum
Average
Maximum
```

são **estatísticas** utilizadas para analisar os valores dessa métrica dentro de um período.

Por exemplo:

```text
Métrica: CPUUtilization
Período: 5 minutos
Estatística: Average
```

Isso significa que o CloudWatch calculará a média dos valores observados naquele período.

---

## 1.5 Como investigaríamos erros de uma aplicação?

**Minha resposta:**

> Principalmente debbugando a partir da consulta aos logs emitidos pela aplicação no intervalo de tempo adequado vendo se é algum bug ou problema de comunicação com serviços externos, ou através das métricas para ver se é algum pico de processamento elevado, etc

### Correção

A abordagem está correta.

Logs são úteis para investigar:

* exceções;
* mensagens de erro;
* chamadas externas;
* comportamento da aplicação;
* informações de contexto.

Métricas ajudam a identificar padrões quantitativos, como:

* aumento de CPU;
* aumento de latência;
* aumento de requisições;
* crescimento de mensagens em uma fila.

Uma investigação pode combinar os dois:

```text
Métrica → identificar comportamento anormal
              ↓
Logs → investigar a causa
```

---

## 1.6 O que é um CloudWatch Alarm?

**Minha resposta:**

> É uma noticação caso um threshold pré definido de alguma métrica específica seja ultrapassado. É muito utilizado melhor acompanhamento em cenários atípicos que não normalmente não seriam esperados

### Correção

A ideia principal está correta.

Um alarm monitora uma métrica e avalia seus valores de acordo com condições configuradas.

Por exemplo:

```text
CPUUtilization >= 80%
```

durante determinado período.

O alarm pode mudar de estado e, dependendo da configuração, participar de ações ou notificações.

Os estados principais são:

```text
OK
ALARM
INSUFFICIENT_DATA
```

---

## 1.7 Qual a diferença entre Logs, Metrics e Alarms?

**Minha resposta:**

> Logs são dados emitidos pela aplicação que também podem servir de métricas que são uma coisa mensurável que pode ser configurado para emitir alarmes caso determinados thresholds sejam ultrapassados

### Correção

A relação está parcialmente correta.

Podemos pensar em:

```text
Logs
→ informações detalhadas sobre acontecimentos

Metrics
→ valores numéricos mensuráveis

Alarms
→ avaliação de métricas segundo determinadas condições
```

Logs não se tornam métricas automaticamente.

Porém, o CloudWatch pode utilizar **metric filters** para transformar determinados padrões encontrados em logs em métricas.

---

## 1.8 Como monitoraríamos o backlog de uma fila SQS?

**Minha resposta:**

> Emitindo um alarme a partir de uma métrica de x quantidade total de eventos na fila SQS ultrapassado

### Correção

A ideia está correta.

O CloudWatch possui métricas relacionadas às filas SQS, incluindo:

```text
ApproximateNumberOfMessagesVisible
```

Essa métrica pode ser utilizada para detectar crescimento do backlog.

Exemplo:

```text
ApproximateNumberOfMessagesVisible >= 100
                    ↓
                ALARM
```

Isso permite detectar uma situação em que consumidores podem não estar processando mensagens suficientemente rápido.

---

## 1.9 O CloudWatch corrige o problema?

**Minha resposta:**

> O CloudWatch não faz nada, ele apenas monitora e envia notificações. Mas por exemplo outras aplicações poderiam utilizar esses alertas para tomar alguma decisão como um auto scaling, etc

### Correção

A ideia está correta, com uma pequena ressalva.

O CloudWatch não é, por si só, o componente responsável por corrigir o problema da aplicação.

Ele pode:

1. observar;
2. avaliar;
3. alterar o estado de um alarm;
4. participar de ações configuradas.

Outros serviços podem utilizar essas informações para executar ações.

Por exemplo:

```text
CloudWatch Alarm
       ↓
Auto Scaling
       ↓
aumentar capacidade
```

Portanto, é importante separar:

```text
CloudWatch → monitoramento/observabilidade
Auto Scaling → gerenciamento de capacidade
```

---

## 1.10 O CloudWatch possui custos?

**Minha resposta:**

> Sim, se eu não me engano ele cobra 0.3 a 0.5 centavos de dolar para cada log enviado ou métricas configuradas

### Correção

A ideia de que existem custos está correta, mas o modelo não é simplesmente uma cobrança fixa por log ou métrica.

Os custos podem depender de fatores como:

* ingestão de logs;
* armazenamento;
* consultas;
* métricas customizadas;
* dashboards;
* outras funcionalidades.

Por isso, para o laboratório, a abordagem utilizada foi criar recursos pequenos, testar e destruir.

---

# 2. Teoria

## 2.1 O que é o Amazon CloudWatch?

O Amazon CloudWatch é o serviço de monitoramento e observabilidade da AWS.

Ele permite acompanhar recursos e aplicações através de:

* métricas;
* logs;
* alarmes;
* dashboards;
* consultas;
* integrações com outros serviços.

Uma visão simplificada:

```text
AWS Resources / Applications
            ↓
       CloudWatch
       ├── Metrics
       ├── Logs
       ├── Alarms
       ├── Dashboards
       └── Insights
```

---

# 3. CloudWatch Metrics

Uma métrica representa um valor numérico associado a determinado contexto e normalmente observado ao longo do tempo.

Exemplo:

```text
CPUUtilization
```

pode apresentar:

```text
10:00 → 25%
10:01 → 31%
10:02 → 42%
```

Métricas podem possuir:

* namespace;
* nome;
* valor;
* timestamp;
* dimensões.

---

## 3.1 Namespace

O namespace organiza as métricas.

No laboratório foi utilizado:

```text
AWS/SQS
```

Esse namespace contém métricas relacionadas ao Amazon SQS.

---

## 3.2 Dimensions

Dimensions fornecem contexto adicional para uma métrica.

No laboratório:

```text
QueueName = aws-cloud-practitioner-lab-cloudwatch
```

Isso permite que a métrica seja associada à fila específica.

---

## 3.3 Statistics

As estatísticas permitem analisar os valores observados.

Exemplos:

```text
Minimum
Average
Maximum
Sum
SampleCount
```

No laboratório foi utilizado:

```text
Statistic = Maximum
```

---

## 3.4 Period

O período determina o intervalo utilizado para avaliação dos dados.

No laboratório:

```text
Period = 60 segundos
```

Isso não significa necessariamente que o estado do alarm mudará exatamente 60 segundos após uma alteração na fila.

Existe também o tempo necessário para que a métrica seja publicada e avaliada pelo CloudWatch.

---

# 4. CloudWatch Logs

A estrutura conceitual utilizada pelo CloudWatch Logs é:

```text
Log Group
└── Log Stream
    └── Log Events
```

### Log Group

Agrupa logs relacionados.

### Log Stream

Representa uma sequência de eventos proveniente de uma fonte.

### Log Event

É uma entrada individual de log.

---

# 5. CloudWatch Agent

Algumas informações não são disponibilizadas como métricas padrão.

Um exemplo é a utilização de memória de uma instância EC2.

Nesses casos, o CloudWatch Agent pode ser utilizado para coletar informações adicionais e enviá-las ao CloudWatch.

---

# 6. CloudWatch Logs Insights

O CloudWatch Logs Insights permite consultar e analisar logs utilizando consultas.

Isso facilita investigações como:

```text
Quais erros ocorreram?
Quando começaram?
Qual serviço apresentou o erro?
Existe algum padrão?
```

---

# 7. CloudWatch Alarms

Um alarm acompanha uma métrica e verifica se determinada condição foi atingida.

Exemplo:

```text
Metric:
ApproximateNumberOfMessagesVisible

Condition:
>= 1

Period:
60 seconds
```

O alarm pode apresentar três estados principais:

```text
OK
ALARM
INSUFFICIENT_DATA
```

### OK

A métrica está dentro da condição normal configurada.

### ALARM

A condição configurada foi atingida.

### INSUFFICIENT_DATA

Não existem dados suficientes para determinar o estado.

---

# 8. CloudWatch + SQS

O Amazon SQS disponibiliza métricas para acompanhamento das filas.

Uma delas é:

```text
ApproximateNumberOfMessagesVisible
```

Ela representa aproximadamente a quantidade de mensagens disponíveis para consumo.

Essa métrica pode ser utilizada para detectar crescimento de backlog.

No laboratório:

```text
Mensagens visíveis >= 1
             ↓
        CloudWatch
             ↓
          ALARM
```

---

# 9. CloudWatch × CloudTrail × EventBridge

É importante diferenciar os serviços.

| Serviço     | Função principal                         |
| ----------- | ---------------------------------------- |
| CloudWatch  | Monitoramento e observabilidade          |
| CloudTrail  | Registro de atividades e chamadas de API |
| EventBridge | Roteamento e processamento de eventos    |

Uma forma simplificada de lembrar:

```text
CloudWatch  → "Como os recursos estão se comportando?"
CloudTrail  → "Quem fez o quê na AWS?"
EventBridge → "O que aconteceu e o que deve reagir a isso?"
```

---

# 10. CloudWatch × Auto Scaling

O CloudWatch pode fornecer as informações utilizadas para decisões de escalabilidade.

Exemplo conceitual:

```text
CPU alta
   ↓
CloudWatch Metric
   ↓
CloudWatch Alarm
   ↓
Auto Scaling
   ↓
mais capacidade
```

O CloudWatch não deve ser confundido com o próprio mecanismo de Auto Scaling.

---

# 11. Laboratório prático

## 11.1 Objetivo

Criar:

* uma fila SQS;
* um CloudWatch Alarm;
* uma policy IAM específica para o laboratório.

Arquitetura:

```text
SQS Queue
    │
    │ AWS/SQS
    ↓
CloudWatch Metric
    │
    ↓
CloudWatch Alarm
```

Fila:

```text
aws-cloud-practitioner-lab-cloudwatch
```

Alarm:

```text
aws-cloud-practitioner-lab-sqs-messages
```

---

## 11.2 Princípio de least privilege

A policy criada para o laboratório não utilizou `AdministratorAccess`.

Foram concedidas apenas as permissões necessárias para:

### SQS

* `CreateQueue`
* `DeleteQueue`
* `GetQueueAttributes`
* `GetQueueUrl`
* `ListQueueTags`
* `TagQueue`
* `UntagQueue`
* `SendMessage`
* `ReceiveMessage`
* `DeleteMessage`
* `ChangeMessageVisibility`

### CloudWatch

* `PutMetricAlarm`
* `DescribeAlarms`
* `DeleteAlarms`
* `ListTagsForResource`

A necessidade de adicionar:

```text
cloudwatch:ListTagsForResource
```

foi descoberta durante a execução do Terraform.

O primeiro `apply` falhou porque essa permissão não estava na policy.

Isso demonstrou uma situação real de least privilege:

```text
Terraform precisa executar uma operação
        ↓
AWS retorna AccessDenied
        ↓
identificar exatamente a permissão necessária
        ↓
adicionar somente essa permissão
        ↓
executar novamente
```

---

# 12. Configuração do Alarm

O alarm utilizou:

```text
Namespace:
AWS/SQS

Metric:
ApproximateNumberOfMessagesVisible

Statistic:
Maximum

Period:
60 seconds

Evaluation periods:
1

Threshold:
1

Comparison:
GreaterThanOrEqualToThreshold

Treat missing data:
notBreaching
```

A dimensão utilizada foi:

```text
QueueName =
aws-cloud-practitioner-lab-cloudwatch
```

O alarm não possuía ações ou notificações configuradas.

Isso foi intencional: o objetivo do laboratório era observar a mudança de estado do alarm.

---

# 13. Teste do Alarm

Após a criação, o alarm inicialmente apresentou:

```text
INSUFFICIENT_DATA
```

Isso ocorreu porque ainda não havia dados suficientes para determinar o estado.

Posteriormente, quando a métrica apresentou:

```text
0
```

o alarm passou para:

```text
OK
```

---

## 13.1 Gerando o ALARM

Foi enviada uma mensagem para a fila:

```text
Teste de CloudWatch Alarm
```

A fila passou a apresentar aproximadamente:

```text
1 mensagem
```

A métrica do CloudWatch:

```text
ApproximateNumberOfMessagesVisible
```

passou a representar esse crescimento.

O alarm posteriormente mudou para:

```text
ALARM
```

com o motivo:

```text
Threshold Crossed:
1 datapoint [1.0] was greater than or equal to threshold (1.0).
```

---

## 13.2 Removendo a mensagem

A mensagem foi recebida e removida da fila.

Depois:

```text
ApproximateNumberOfMessages = 0
```

O alarm não voltou imediatamente para `OK`.

Após a próxima avaliação da métrica, ele retornou para:

```text
OK
```

com o datapoint:

```text
0.0
```

não atingindo o threshold de `1`.

---

# 14. Ciclo observado

O laboratório demonstrou o ciclo completo:

```text
INSUFFICIENT_DATA
        ↓
       OK
        ↓
      ALARM
        ↓
       OK
```

Isso foi importante para visualizar que um CloudWatch Alarm possui **estado**, e não é simplesmente uma notificação instantânea.

---

# 15. Observações importantes

## 15.1 Métrica do CloudWatch ≠ atributo da API SQS

Durante o laboratório foi inicialmente utilizado:

```text
ApproximateNumberOfMessagesVisible
```

diretamente em:

```bash
aws sqs get-queue-attributes
```

A operação falhou porque esse é o nome de uma **métrica do CloudWatch**, não um atributo válido dessa API do SQS.

Para consultar a fila através da API SQS foi utilizado:

```text
ApproximateNumberOfMessages
```

Portanto:

```text
SQS API
→ ApproximateNumberOfMessages

CloudWatch Metric
→ ApproximateNumberOfMessagesVisible
```

Essa diferença é importante.

---

## 15.2 O período de 60 segundos não significa uma transição exata em 60 segundos

O alarm foi configurado com:

```text
Period = 60
```

Mesmo assim, a mudança de estado não ocorreu necessariamente exatamente 60 segundos depois da alteração da fila.

Isso acontece porque existe diferença entre:

```text
evento na aplicação/SQS
        ↓
publicação da métrica
        ↓
avaliação pelo CloudWatch
        ↓
mudança do estado do alarm
```

Portanto, métricas e alarmes podem apresentar uma pequena latência.

---

## 15.3 Alarm sem ação

O alarm não tinha:

```text
AlarmActions
OKActions
InsufficientDataActions
```

configurados.

O objetivo foi observar o mecanismo de monitoramento, sem introduzir SNS ou outras ações no laboratório.

Em um cenário real, o alarm poderia ser integrado a outros mecanismos de automação ou notificação.

---

# 16. Limpeza

Após concluir os testes:

```bash
terraform destroy
```

foi executado com sucesso.

Os recursos temporários do laboratório foram destruídos.

Isso mantém o padrão:

```text
create
   ↓
test
   ↓
observe
   ↓
document
   ↓
destroy
```

---

# 17. Resultado do laboratório

Ao final do Day 11, foi possível demonstrar na prática:

* criação de um CloudWatch Alarm com Terraform;
* utilização de uma métrica nativa do SQS;
* configuração de namespace e dimensão;
* utilização de estatística `Maximum`;
* configuração de período de avaliação;
* interpretação dos estados `INSUFFICIENT_DATA`, `OK` e `ALARM`;
* observação da transição de estado causada por mensagens na fila;
* retorno para `OK` após o processamento da mensagem;
* diferença entre uma métrica do CloudWatch e um atributo da API SQS;
* impacto da latência de publicação/avaliação;
* necessidade de permissões IAM específicas para Terraform;
* aplicação prática de least privilege.

O laboratório também mostrou como o CloudWatch pode servir como camada de observabilidade para os demais componentes que serão utilizados no projeto final:

```text
ECS
RDS
SQS
EC2
        ↓
    CloudWatch
```
