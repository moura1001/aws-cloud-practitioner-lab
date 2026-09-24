# 10 — SQS (Simple Queue Service)

## Objetivo

Neste laboratório, o objetivo é entender o **Amazon Simple Queue Service (SQS)** como serviço de mensageria assíncrona, compreendendo:

* comunicação síncrona e assíncrona;
* desacoplamento entre componentes;
* produtores, consumidores, filas e mensagens;
* ciclo de vida de uma mensagem;
* Visibility Timeout;
* entrega **at-least-once**;
* idempotência;
* Standard Queue e FIFO Queue;
* Long Polling;
* Message Retention;
* Dead-Letter Queue (DLQ);
* integração conceitual com ECS/EC2;
* permissões IAM necessárias;
* custos e controle do laboratório.

O laboratório utiliza Terraform e segue a metodologia:

**entender → criar → testar → observar → documentar → destruir**

---

# 1. Diagnóstico

Antes da parte teórica, respondi às seguintes perguntas para identificar o conhecimento inicial sobre SQS.

## 1.1 O que é SQS e qual problema ele resolve?

### Minha resposta:

> O SQS é um sistema de filas, onde eu esqueci exatamente qual o nome da arquitetura em questão que resolve comunicação assíncrona entre sistemas

### Correção

A ideia principal está correta.

O Amazon SQS é um serviço gerenciado de **filas de mensagens** usado principalmente para permitir **comunicação assíncrona e desacoplamento entre componentes de uma aplicação**.

O conceito que estava faltando é **desacoplamento**.

Em vez de um serviço depender diretamente da disponibilidade imediata de outro serviço, ele pode colocar uma mensagem em uma fila e continuar seu processamento.

### Pegadinha CLF-C02

SQS não é um banco de dados nem simplesmente um mecanismo para armazenar informações permanentemente.

Seu objetivo principal é **transportar e armazenar temporariamente mensagens entre produtores e consumidores**.

---

## 1.2 Qual é a diferença entre comunicação síncrona e assíncrona?

### Minha resposta:

> Em comunicação síncrona quem abriu a comunicação fica esperando uma resposta para fechar o canal ou por timeout enquanto que numa comunicação assíncrona há um mecanismo de "conversa" onde quem iniciou não fica bloqueado e ele espera a resposta por outro por um canal separado, geralmente seguindo o coneito de fila. Um bom exemplo sçao os sistemas de ecomerce onde de uma compra a té o recebimento do pedido pode demorar dias/semanas/meses

### Correção

A ideia principal está correta.

Na comunicação **síncrona**, o componente que iniciou a operação normalmente aguarda uma resposta antes de continuar determinada etapa do processamento.

Na comunicação **assíncrona**, o produtor pode enviar uma mensagem para um intermediário, como uma fila, e continuar sem precisar aguardar o processamento do consumidor.

O exemplo de e-commerce é adequado: uma compra pode disparar várias tarefas independentes, como processamento do pagamento, atualização de estoque, envio de e-mail e geração da entrega.

### Pegadinha CLF-C02

Assíncrono não significa necessariamente "demorado".

O ponto principal é que **o produtor não precisa ficar bloqueado esperando o consumidor processar a mensagem**.

---

## 1.3 Por que colocar ECS → SQS → outro serviço?

### Minha resposta:

> Porque além do possíel isolamento de rede entre os sistemas, o outro sistema poderia está fora do ar e rejeitar a requisição ou apresentar erro, etc, sendo que com uma fila SQS o outro serviço poderia ir processando no seu tempo e com retries em caso de falha, só removendo da filaem caso de sucesso

### Correção

A resposta está correta.

A fila cria um **desacoplamento temporal** entre os componentes.

Por exemplo:

```text
ECS
 │
 │ SendMessage
 ▼
SQS
 │
 │ ReceiveMessage
 ▼
Worker
```

Se o worker estiver temporariamente indisponível, as mensagens podem permanecer na fila para serem processadas posteriormente.

Também é possível implementar novas tentativas quando ocorre uma falha.

### Pegadinha CLF-C02

SQS **não garante que o processamento será bem-sucedido**.

Ele fornece o mecanismo de entrega e armazenamento da mensagem. A aplicação consumidora continua sendo responsável pelo processamento correto.

---

## 1.4 O que é uma mensagem?

### Minha resposta:

> Message é um evento com um padrão bem definido e conhecido que servirá entre a comunicação entre os sistemas. Quem envia é o produtor e quem recebe é o consumidor

### Correção

A definição de produtor e consumidor está correta.

Uma **message** é o dado enviado para a fila.

Ela pode representar um evento, comando, tarefa ou simplesmente dados que outro componente precisa processar.

Não é obrigatório que tenha um padrão específico ou que seja necessariamente um evento.

Exemplo:

```text
{
  "orderId": 12345,
  "action": "PROCESS_PAYMENT"
}
```

Nesse caso:

* quem envia é o **producer**;
* SQS armazena a mensagem;
* quem recebe é o **consumer**.

---

## 1.5 O que acontece com uma mensagem depois que o consumidor a recebe?

### Minha resposta:

> Acredito que ela permanece na fila a menos que seja explicitamente excluída por causa do conceito de last once

### Correção

A primeira parte está correta.

O termo correto é **at-least-once delivery**, e não "last once".

Quando um consumidor recebe uma mensagem, ela **não é automaticamente excluída da fila**.

Durante o Visibility Timeout, a mensagem normalmente fica temporariamente invisível para outros consumidores. Depois que o processamento termina com sucesso, o consumidor deve executar:

```text
DeleteMessage
```

Se isso não acontecer, a mensagem poderá voltar a ficar disponível.

### Pegadinha CLF-C02

**ReceiveMessage não significa DeleteMessage.**

O fluxo é:

```text
SendMessage
     ↓
Queue
     ↓
ReceiveMessage
     ↓
Visibility Timeout
     ↓
Processamento
     ↓
DeleteMessage
```

---

## 1.6 O que é Visibility Timeout?

### Minha resposta:

> Visibility timeout é o conceito da mensagem reaparecer na fila para poder ser consumida novamente após o consumidor não ter removido ela com sucesso

### Correção

O conceito está correto.

O **Visibility Timeout** é o período durante o qual uma mensagem recebida fica temporariamente invisível para novos consumidores.

Se o consumidor processar a mensagem com sucesso, ele pode removê-la antes do timeout.

Se não remover, a mensagem poderá voltar a ficar disponível.

### Pegadinha CLF-C02

Visibility Timeout **não exclui a mensagem**.

Ele apenas controla temporariamente sua visibilidade após o recebimento.

---

## 1.7 Qual a diferença entre Standard e FIFO?

### Minha resposta:

> Standard não tem uma ordem bem definida de como as mensagens serão inseridas e entregues enquanto a FIFO sim, além da FIFO ter o conceito de deduplicação dos eventos que são inseridos na fila

### Correção

A resposta está correta.

### Standard

* alta capacidade de processamento;
* não fornece garantia de ordenação estrita;
* trabalha com entrega at-least-once;
* mensagens duplicadas podem ocorrer.

### FIFO

* preserva a ordem das mensagens;
* possui mecanismos de deduplicação;
* é utilizada quando a ordem de processamento é importante.

### Pegadinha CLF-C02

Não associar FIFO simplesmente a "mais segura".

A escolha depende do requisito da aplicação, principalmente **ordenação e deduplicação**.

---

## 1.8 Se forem produzidas 1.000 mensagens em poucos segundos e o consumidor processar 100/s, o que acontece?

### Minha resposta:

> A fila irá crescer e acumular várias mensagens que precisam ser processadas

### Correção

Correto.

Nesse cenário:

```text
Produção:   1000 msg/s
Consumo:     100 msg/s
                  ↓
             backlog cresce
```

Enquanto a taxa de produção permanecer maior que a taxa de consumo, o número de mensagens pendentes tende a crescer.

Esse é um dos usos importantes de filas: **absorver picos de demanda**.

### Pegadinha CLF-C02

SQS não aumenta automaticamente a capacidade do consumidor.

Para processar o backlog mais rapidamente, a arquitetura pode precisar aumentar a capacidade dos consumidores, por exemplo utilizando múltiplas tarefas ECS.

---

## 1.9 Onde o SQS aparece em uma arquitetura AWS?

### Minha resposta:

> Não tenho certeza mas seria em conjunto com as intâncias da ECS/EC2

### Correção

Correto.

SQS pode ficar entre diferentes componentes:

```text
Producer
   │
   ▼
 SQS
   │
   ▼
Consumer
```

O producer e consumer podem ser, por exemplo:

* ECS;
* EC2;
* Lambda;
* aplicações externas;
* outros serviços AWS.

Um exemplo com o conteúdo estudado anteriormente:

```text
ECS
 │
 │ envia mensagem
 ▼
SQS
 │
 │ consumidor recebe
 ▼
ECS / EC2
 │
 ▼
RDS
```

Nesse cenário:

* ECR pode armazenar a imagem;
* ECS executa a aplicação;
* SQS desacopla os serviços;
* RDS pode armazenar dados persistentes.

---

## 1.10 Como o SQS é cobrado?

### Minha resposta:

> Acredito que o SQS cobra por movimentações dentro da fila, seja de inserções, leituras ou exclusões

### Correção

A ideia está correta.

O modelo de cobrança do SQS está relacionado principalmente às **requisições/operações realizadas no serviço**.

Operações como envio e recebimento de mensagens fazem parte desse modelo.

Por isso, mesmo sendo um serviço relativamente simples, uma aplicação com grande volume de mensagens pode gerar custos.

### Pegadinha CLF-C02

Não pensar somente em:

> "Criar a fila custa X."

O volume de utilização também importa.

Para o laboratório, a estratégia continua sendo:

```text
CREATE
  ↓
TEST
  ↓
DOCUMENT
  ↓
DESTROY
```

---

# 2. Conteúdo teórico

## 2.1 O que é Amazon SQS?

Amazon SQS significa **Simple Queue Service**.

É um serviço gerenciado de filas de mensagens utilizado para comunicação entre componentes de sistemas distribuídos.

A ideia fundamental é:

> Um componente envia uma mensagem para uma fila e outro componente pode processá-la posteriormente.

Exemplo:

```text
┌─────────────┐
│   Producer  │
│    ECS      │
└──────┬──────┘
       │
       │ SendMessage
       ▼
┌─────────────┐
│     SQS     │
│    Queue    │
└──────┬──────┘
       │
       │ ReceiveMessage
       ▼
┌─────────────┐
│  Consumer   │
│    ECS      │
└─────────────┘
```

---

## 2.2 Desacoplamento

O principal benefício arquitetural do SQS é o **desacoplamento**.

Sem uma fila:

```text
Service A ───────────► Service B
```

O Service A depende diretamente do Service B.

Com SQS:

```text
Service A ─────► SQS ─────► Service B
```

Agora os serviços não precisam estar disponíveis exatamente no mesmo momento.

Isso reduz o acoplamento entre eles.

---

## 2.3 Comunicação síncrona x assíncrona

### Síncrona

```text
A ───── request ─────► B
A ◄──── response ───── B
```

A depende de uma resposta de B para continuar determinada operação.

### Assíncrona

```text
A ───── message ─────► SQS
                        │
                        ▼
                        B
```

A pode continuar seu processamento depois de colocar a mensagem na fila.

---

## 2.4 Producer, Consumer, Queue e Message

### Producer

É quem envia a mensagem.

Exemplo:

```text
ECS → SendMessage
```

### Queue

É a fila que armazena temporariamente as mensagens aguardando processamento.

### Message

É o conteúdo enviado pelo producer.

### Consumer

É quem recebe e processa a mensagem.

Exemplo:

```text
Producer → Queue → Consumer
```

---

# 3. Ciclo de vida de uma mensagem

O ciclo básico é:

```text
             SendMessage
                  │
                  ▼
             ┌─────────┐
             │  Queue  │
             └────┬────┘
                  │
            ReceiveMessage
                  │
                  ▼
         ┌─────────────────┐
         │ Visibility      │
         │ Timeout         │
         └────────┬────────┘
                  │
             Processamento
                  │
            ┌─────┴─────┐
            │           │
          sucesso      falha
            │           │
            ▼           ▼
     DeleteMessage   Timeout expira
                        │
                        ▼
                  Mensagem pode
                  reaparecer
```

Esse ciclo é fundamental para entender SQS.

---

# 4. At-least-once delivery

Uma característica importante do SQS Standard é o modelo de entrega **at-least-once**.

Isso significa que uma mensagem pode ser entregue mais de uma vez.

Por isso, consumidores devem ser projetados considerando a possibilidade de duplicação.

Por exemplo, se uma mensagem:

```text
PROCESS_PAYMENT
orderId = 123
```

for recebida duas vezes, o sistema não deveria processar o pagamento duas vezes de forma indevida.

Isso nos leva ao conceito de **idempotência**.

---

# 5. Idempotência

Uma operação é idempotente quando sua repetição não produz efeitos indevidos adicionais.

Por exemplo, o consumidor pode verificar:

```text
orderId = 123
```

antes de executar novamente uma operação.

A ideia é:

```text
Mensagem recebida
       ↓
Já processei essa operação?
       │
   ┌───┴───┐
  Sim     Não
   │        │
 Ignora   Processa
```

Esse conceito é especialmente importante quando trabalhamos com filas Standard.

---

# 6. Visibility Timeout

Quando uma mensagem é recebida, ela fica temporariamente invisível.

Exemplo:

```text
Mensagem na fila
      │
      ▼
ReceiveMessage
      │
      ▼
Invisible
      │
      ├── processamento + DeleteMessage
      │
      └── timeout expira
                  │
                  ▼
             Disponível novamente
```

O objetivo é evitar que vários consumidores processem simultaneamente a mesma mensagem durante o processamento normal.

Entretanto, como o SQS Standard trabalha com at-least-once delivery, a aplicação não deve assumir que duplicações são impossíveis durante esse período.

---

# 7. Message Retention

As mensagens possuem um período de retenção.

Isso determina por quanto tempo uma mensagem pode permanecer na fila antes de expirar.

No laboratório configuramos:

```hcl
message_retention_seconds = 86400
```

Ou seja:

```text
86400 segundos = 24 horas
```

Esse valor foi escolhido para manter o laboratório simples e evitar retenção desnecessariamente longa.

---

# 8. Long Polling

No laboratório configuramos:

```hcl
receive_wait_time_seconds = 10
```

Isso habilita **Long Polling**.

Em vez de consultar a fila repetidamente e receber imediatamente uma resposta vazia, o consumidor pode aguardar por mensagens durante determinado período.

No laboratório isso ficou evidente quando um `receive-message` demorou aproximadamente 10 segundos antes de retornar sem mensagem.

Conceitualmente:

```text
Short Polling:

Consumer → "Tem mensagem?"
SQS      → "Não"
Consumer → "Tem mensagem?"
SQS      → "Não"
...

Long Polling:

Consumer → "Tem mensagem?"
             │
             │ aguarda
             ▼
           SQS
```

Isso pode reduzir chamadas desnecessárias e melhorar a eficiência do consumo.

---

# 9. Standard Queue x FIFO Queue

## Standard

Características principais:

* alta capacidade de processamento;
* entrega at-least-once;
* não garante ordenação estrita;
* pode ocorrer duplicação.

É adequada para muitos cenários de processamento assíncrono.

## FIFO

FIFO significa **First-In, First-Out**.

Características principais:

* preservação da ordem;
* mecanismos de deduplicação;
* utilizada quando a ordem das mensagens é requisito da aplicação.

Exemplo:

```text
1 → Criar pedido
2 → Aprovar pagamento
3 → Separar pedido
```

Se a ordem for importante, uma FIFO pode ser necessária.

---

# 10. Dead-Letter Queue

Uma **Dead-Letter Queue (DLQ)** é utilizada para receber mensagens que não conseguem ser processadas após determinado número de tentativas.

Exemplo:

```text
Main Queue
    │
    ▼
Consumer
    │
    ├── sucesso → Delete
    │
    └── falha
          │
          ▼
      retry
          │
          ▼
      retry
          │
          ▼
       DLQ
```

A DLQ permite separar mensagens problemáticas das mensagens que continuam sendo processadas normalmente.

Neste laboratório, a DLQ foi estudada conceitualmente, mas não foi criada.

---

# 11. SQS x SNS

SQS e SNS são serviços relacionados, mas possuem papéis diferentes.

### SQS

É principalmente uma **fila**.

```text
Producer
   │
   ▼
 Queue
   │
   ▼
Consumer
```

### SNS

É principalmente um mecanismo de **publish/subscribe**.

Uma mensagem pode ser publicada e distribuída para múltiplos destinos.

Um padrão comum é:

```text
             SNS
              │
       ┌──────┼──────┐
       ▼      ▼      ▼
      SQS    SQS   Lambda
```

Assim, SNS pode realizar o fan-out e SQS pode fornecer o armazenamento/consumo assíncrono para cada consumidor.

---

# 12. SQS não é banco de dados

SQS não deve ser utilizado como substituto de:

* RDS;
* DynamoDB;
* outros bancos de dados.

A fila possui outro propósito:

```text
Banco de dados
→ persistência estruturada de dados

SQS
→ comunicação assíncrona e desacoplamento
```

---

# 13. SQS não escala o consumidor automaticamente

A fila consegue acumular mensagens, mas isso não significa que os consumidores serão automaticamente escalados.

Exemplo:

```text
Producer
1000 msg/s
    │
    ▼
   SQS
    │
    ▼
Consumer
100 msg/s
```

O backlog cresce.

Uma arquitetura poderia utilizar múltiplos consumidores:

```text
             SQS
              │
       ┌──────┼──────┐
       ▼      ▼      ▼
     ECS-1  ECS-2  ECS-3
```

Nesse cenário, serviços como ECS e Auto Scaling podem ser utilizados para aumentar a capacidade de processamento.

---

# 14. Integração com ECS e EC2

O SQS pode ser integrado com os serviços estudados anteriormente.

Um exemplo:

```text
                    ECR
                     │
                     ▼
                    ECS
                     │
                SendMessage
                     │
                     ▼
                    SQS
                     │
             ReceiveMessage
                     │
                     ▼
                  Worker
                  ECS/EC2
                     │
                     ▼
                    RDS
```

O IAM controla quais componentes podem interagir com a fila.

Por exemplo:

```text
Producer
   │
   └── sqs:SendMessage

Consumer
   │
   ├── sqs:ReceiveMessage
   ├── sqs:DeleteMessage
   └── sqs:ChangeMessageVisibility
```

---

# 15. IAM e princípio do menor privilégio

Durante o laboratório, a criação da fila inicialmente falhou porque o usuário não possuía:

```text
sqs:CreateQueue
```

A criação funcionou quando AdministratorAccess foi utilizada, mas essa não era a solução desejada para o laboratório.

Foi criada uma política específica para SQS contendo somente as ações necessárias.

Entre elas:

```text
sqs:CreateQueue
sqs:DeleteQueue
sqs:GetQueueAttributes
sqs:GetQueueUrl
sqs:ListQueueTags
sqs:TagQueue
sqs:UntagQueue
sqs:SendMessage
sqs:ReceiveMessage
sqs:DeleteMessage
sqs:ChangeMessageVisibility
```

A política foi anexada ao usuário por Terraform.

Isso permitiu validar na prática o princípio de **least privilege**.

---

# 16. Dependência entre IAM e SQS no Terraform

A fila foi configurada com:

```hcl
depends_on = [
  aws_iam_user_policy_attachment.sqs
]
```

Isso foi necessário porque a fila precisa ser criada somente depois que a permissão necessária estiver associada ao usuário.

A cadeia ficou:

```text
aws_iam_policy.sqs
        │
        ▼
aws_iam_user_policy_attachment.sqs
        │
        ▼
aws_sqs_queue.app
```

A primeira dependência é inferida pelo Terraform porque o attachment referencia:

```hcl
aws_iam_policy.sqs.arn
```

Já a fila não referencia diretamente o attachment. Por isso foi utilizada uma dependência explícita com `depends_on`.

---

# 17. Custos

O SQS possui cobrança relacionada principalmente ao volume de utilização e às requisições realizadas.

No laboratório, o controle de custos foi feito através de:

* poucos testes;
* retenção limitada;
* ausência de infraestrutura adicional;
* execução somente durante o estudo;
* `terraform destroy` ao finalizar.

A regra continua sendo:

```text
Criar
  ↓
Testar
  ↓
Observar
  ↓
Documentar
  ↓
Destruir
```

---

# 18. Hands-on

## 18.1 Criar a fila

A infraestrutura foi criada com Terraform.

Configurações relevantes:

```hcl
resource "aws_sqs_queue" "app" {
  name = "aws-cloud-practitioner-lab"

  visibility_timeout_seconds = 30
  message_retention_seconds  = 86400
  receive_wait_time_seconds  = 10
}
```

Resultado:

```text
aws_sqs_queue.app: Creation complete
Apply complete! Resources: 1 added, 0 changed, 0 destroyed.
```

A criação foi realizada sem AdministratorAccess após a política específica de SQS ser anexada ao usuário.

---

## 18.2 Obter a URL da fila

```bash
QUEUE_URL=$(aws sqs get-queue-url \
  --queue-name aws-cloud-practitioner-lab \
  --region sa-east-1 \
  --query 'QueueUrl' \
  --output text)
```

A variável `QUEUE_URL` passou a representar o endpoint da fila.

---

## 18.3 Enviar uma mensagem

```bash
aws sqs send-message \
  --queue-url "$QUEUE_URL" \
  --message-body "Mensagem de teste do AWS Cloud Practitioner Lab" \
  --region sa-east-1
```

A operação retornou um `MessageId`, confirmando que a mensagem foi aceita pela fila.

---

## 18.4 Receber uma mensagem

```bash
aws sqs receive-message \
  --queue-url "$QUEUE_URL" \
  --region sa-east-1
```

O retorno apresentou:

* `MessageId`;
* `ReceiptHandle`;
* `Body`.

O `ReceiptHandle` é utilizado para operações sobre aquela entrega específica, como a exclusão da mensagem.

---

## 18.5 Observar o comportamento do Standard Queue

Foram realizadas chamadas consecutivas de `receive-message`.

A mesma mensagem foi retornada novamente, com diferentes `ReceiptHandle`.

Isso foi importante porque demonstrou na prática que uma aplicação não deve assumir que uma mensagem Standard será entregue exatamente uma única vez.

A observação reforçou os conceitos de:

* at-least-once delivery;
* duplicação;
* idempotência;
* ReceiptHandle.

---

## 18.6 Receber e capturar o ReceiptHandle

Para excluir a mensagem recebida:

```bash
RECEIPT_HANDLE=$(aws sqs receive-message \
  --queue-url "$QUEUE_URL" \
  --region sa-east-1 \
  --query 'Messages[0].ReceiptHandle' \
  --output text)
```

Depois:

```bash
aws sqs delete-message \
  --queue-url "$QUEUE_URL" \
  --region sa-east-1 \
  --receipt-handle "$RECEIPT_HANDLE"
```

A operação não produziu saída no terminal, indicando que a chamada foi executada.

---

## 18.7 Validar que a mensagem foi removida

Foi executado novamente:

```bash
aws sqs receive-message \
  --queue-url "$QUEUE_URL" \
  --region sa-east-1
```

Não houve mensagem retornada.

Como o Long Polling estava configurado para 10 segundos, a chamada aguardou aproximadamente esse período antes de retornar vazia.

Isso demonstrou o fluxo:

```text
ReceiveMessage
      ↓
Processamento
      ↓
DeleteMessage
      ↓
Mensagem não está mais disponível
```

---

# 19. Teste de Visibility Timeout

Foi enviada uma nova mensagem:

```text
Teste de Visibility Timeout
```

Depois ela foi recebida, mas **não foi excluída**.

Após o período correspondente, a mensagem voltou a aparecer em uma nova chamada de `receive-message`.

Foi observado novamente o mesmo `MessageId`, acompanhado de outro `ReceiptHandle`.

Isso demonstrou:

```text
ReceiveMessage
      ↓
Mensagem fica invisível
      ↓
Consumer não executa DeleteMessage
      ↓
Visibility Timeout expira
      ↓
Mensagem pode ser recebida novamente
```

Esse comportamento é fundamental para implementar mecanismos de retry.

Após o teste, a mensagem foi limpa e o laboratório foi destruído.

---

# 20. Observações importantes do laboratório

### 1. SQS desacopla componentes

A fila permite que produtor e consumidor não precisem estar disponíveis simultaneamente.

### 2. Receive não remove a mensagem

É necessário executar `DeleteMessage` após o processamento bem-sucedido.

### 3. Standard pode entregar duplicadamente

O consumidor precisa ser preparado para isso.

### 4. Visibility Timeout não é exclusão

Ele apenas controla a visibilidade temporária da mensagem.

### 5. Long Polling foi observado diretamente

O comando sem mensagens aguardou aproximadamente 10 segundos antes de retornar.

### 6. IAM realmente influencia a criação do recurso

Sem `sqs:CreateQueue`, a criação retornou:

```text
403 AccessDenied
```

Após a criação da política específica e do attachment, o Terraform conseguiu criar a fila sem AdministratorAccess.

### 7. A fila não substitui o consumidor

Se a produção de mensagens for maior que a capacidade de processamento, o backlog aumenta.

---

# 21. Limpeza do ambiente

Após concluir os testes:

```bash
terraform destroy
```

O destroy foi executado com sucesso.

A infraestrutura criada para o laboratório foi removida para evitar custos desnecessários.

---

# 22. Resultado do laboratório

Neste laboratório foi possível:

* compreender o propósito do SQS;
* diferenciar comunicação síncrona e assíncrona;
* entender desacoplamento;
* identificar producer, consumer, queue e message;
* compreender o ciclo de vida de uma mensagem;
* observar Visibility Timeout;
* observar comportamento de entrega at-least-once;
* entender a importância da idempotência;
* diferenciar Standard e FIFO;
* compreender Long Polling;
* estudar Message Retention;
* estudar DLQ conceitualmente;
* diferenciar SQS de SNS;
* integrar conceitualmente SQS com ECS/EC2;
* aplicar IAM com least privilege;
* utilizar `depends_on` para controlar a ordem de criação;
* validar mensagens com AWS CLI;
* destruir a infraestrutura ao final do laboratório.

## Conceitos principais para o CLF-C02

```text
SQS
├── Message Queue
├── Asynchronous Communication
├── Decoupling
├── Producer / Consumer
├── At-least-once delivery
├── Visibility Timeout
├── Standard / FIFO
├── Long Polling
├── Message Retention
├── Dead-Letter Queue
├── Idempotency
└── ECS / EC2 integration
```

**Lab 10 — Amazon SQS: concluído.**
