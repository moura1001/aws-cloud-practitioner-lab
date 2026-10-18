# Projeto Final — AWS Cloud Practitioner Lab

## 1. Objetivo

Este projeto tem como objetivo consolidar, por meio de um laboratório prático, os conhecimentos adquiridos durante os módulos de preparação para a certificação **AWS Certified Cloud Practitioner (CLF-C02)**.

A aplicação utilizada como base é um microsserviço desenvolvido com Java 25 e Spring Boot 4, organizado em dois módulos independentes:

- **`base-api`:** disponibiliza uma API REST para receber eventos e publicá-los em filas Amazon SQS.
- **`base-consumer`:** consome mensagens do SQS, gerencia o estado e a idempotência com Redis e encaminha os eventos para um serviço externo.

A aplicação também utiliza mecanismos de resiliência, como Circuit Breaker, e possui recursos de observabilidade.

O projeto permitirá estudar como esses componentes podem ser executados e integrados na AWS, explorando computação, mensageria, redes, segurança, monitoramento, disponibilidade, escalabilidade e controle de custos.

## 2. Diagnóstico inicial

Antes de iniciar a implementação, foram realizadas dez questões para avaliar os conhecimentos sobre os serviços AWS e sua aplicação na arquitetura proposta.

As respostas originais foram preservadas, seguidas das correções e dos conceitos relevantes para a prova CLF-C02.

### Questão 1 — Amazon SQS e desacoplamento

**Pergunta:**

Por que utilizar uma fila SQS entre a API e o consumer, em vez de a API chamar diretamente o consumer por HTTP?

**Minha resposta:**

> por causa de resiliência, o consumer na ponta final se comunica com uma aplicação externa que pode está fora do ar, e ela tem um circuit breaker para parar de ler novos eventos e tentar enviar para essa aplicação externa, enquanto a api continua funcionando normalmente e inserindo os outros eventos na fila para posterior processamento

**Correção e conceitos importantes:**

A resposta está correta. O Amazon SQS permite desacoplar a entrada de eventos do processamento, possibilitando que a API continue recebendo eventos enquanto o consumer enfrenta dificuldades para se comunicar com o serviço externo.

A fila funciona como um mecanismo de armazenamento temporário das mensagens, permitindo que os consumidores processem os eventos de forma assíncrona.

O Circuit Breaker complementa essa arquitetura ao controlar chamadas a um serviço externo que apresenta falhas. Entretanto, sua abertura não significa necessariamente que o consumer deixará de receber mensagens do SQS: esse comportamento depende da implementação da aplicação.

Também é necessário considerar o período de retenção das mensagens, o visibility timeout e a configuração das tentativas de processamento.

**Conceitos do CLF-C02:**
- Desacoplamento entre componentes.
- Processamento assíncrono.
- Resiliência.
- Amazon SQS.
- Circuit Breaker.
- Visibility timeout e retenção de mensagens.

### Questão 2 — ECS, Auto Scaling e processamento de mensagens

**Pergunta:**

Imagine que o serviço externo volte a funcionar, mas a fila acumule 50 mil mensagens durante a indisponibilidade.

Como você utilizaria o Amazon ECS e o Auto Scaling para ajudar a processar esse backlog mais rapidamente?

**Minha resposta:**

> subindo mais instâncias para ler a fila sqs em paralelo e melhorar o consumo das mensagens consequentemente diminuindo o volume

**Correção e conceitos importantes:**

A resposta está correta. Aumentar a quantidade de consumidores permite processar mais mensagens simultaneamente, desde que a aplicação e os serviços envolvidos suportem essa concorrência.

No Amazon ECS, podemos aumentar a quantidade de tarefas executando o `base-consumer`. Uma política de escalabilidade pode utilizar métricas do CloudWatch para ajustar a quantidade de tarefas conforme a demanda.

Uma métrica importante é `ApproximateNumberOfMessagesVisible`, que indica aproximadamente quantas mensagens estão disponíveis para consumo.

Entretanto, o aumento da concorrência precisa respeitar a capacidade do serviço externo. Se o número de requisições ultrapassar sua capacidade de atendimento, poderão ocorrer novas falhas e a abertura do Circuit Breaker.

**Conceitos do CLF-C02:**
- Amazon ECS.
- Amazon EC2 Auto Scaling.
- Elasticidade e escalabilidade horizontal.
- Amazon CloudWatch.
- Métricas do Amazon SQS.
- Dimensionamento da capacidade de processamento.

### Questão 3 — IAM e princípio do menor privilégio

**Pergunta:**

Como conceder ao `base-consumer` permissão para consumir mensagens do SQS seguindo o princípio do menor privilégio?

**Minha resposta:**

> adicionar uma policy que tenha apenas as funcionalidades necessárias do sqs, como leitura, escrita e exclusão da fila

**Correção e conceitos importantes:**

A resposta está parcialmente correta. A ideia de limitar a política às necessidades da aplicação está correta, mas precisamos diferenciar as operações sobre mensagens das operações administrativas sobre filas.

Para receber e excluir mensagens, o consumer pode precisar de permissões como:

- `sqs:ReceiveMessage`: receber mensagens.
- `sqs:DeleteMessage`: excluir mensagens após o processamento.
- `sqs:GetQueueAttributes`: consultar atributos da fila, quando necessário.
- `sqs:ChangeMessageVisibility`: ajustar o visibility timeout, se necessário.

O consumer não precisa de permissões para criar ou excluir filas se essas operações não fizerem parte de sua responsabilidade.

No ECS com Fargate, a **Task Role** fornece permissões AWS ao código da aplicação. Já a **Task Execution Role** permite ao ECS realizar operações como baixar imagens privadas do ECR e enviar logs ao CloudWatch.

Em ECS com EC2, também precisamos considerar a função da instância EC2 e garantir que as permissões da aplicação sejam concedidas por meio da Task Role, sem depender indevidamente das permissões do host.

A política deve restringir o acesso ao ARN da fila necessária, evitando permissões desnecessariamente abrangentes.

**Conceitos do CLF-C02:**
- AWS IAM.
- Princípio do menor privilégio.
- Políticas IAM e recursos.
- IAM Roles.
- Amazon ECS Task Role.
- Amazon ECS Task Execution Role.

### Questão 4 — Alta disponibilidade e VPC

**Pergunta:**

Por que distribuir tarefas do ECS entre duas zonas de disponibilidade melhora a disponibilidade da aplicação? Você colocaria esses serviços em sub-redes públicas ou privadas?

**Minha resposta:**

> melhora por causa de possibilidade de falhas regionais nas zonas de disponibilidade. Todos os serviços ficariam nas sub-redes privadas, onde apenas as 2 tasks da api teriam um alb na frente deles para distribuir as requisições, e esse alb é que seria público

**Correção e conceitos importantes:**

A resposta está correta em relação à distribuição entre zonas de disponibilidade e à proposta de rede, com um ajuste terminológico.

Uma zona de disponibilidade (AZ) não é uma região. A região `sa-east-1` contém várias zonas de disponibilidade. Distribuir tarefas entre duas AZs ajuda a reduzir o impacto da falha de uma única zona, mas não protege automaticamente contra uma indisponibilidade que afete toda a região.

A arquitetura proposta pode utilizar:

- Um Application Load Balancer público.
- Tarefas da API em sub-redes privadas.
- Tarefas do consumer em sub-redes privadas.
- SQS para comunicação assíncrona.
- Redis acessível somente pelos componentes autorizados.

O ALB distribui requisições entre as tarefas da API. Os Security Groups devem permitir somente os fluxos necessários, como o tráfego do ALB para a porta da aplicação.

Tarefas em sub-redes privadas podem precisar de conectividade de saída para acessar serviços externos ou determinados serviços AWS. Essa conectividade deve ser planejada de acordo com as necessidades reais da aplicação.

**Conceitos do CLF-C02:**
- Amazon VPC.
- Sub-redes públicas e privadas.
- Availability Zones.
- Alta disponibilidade.
- Application Load Balancer.
- Security Groups.
- Conectividade de rede.

### Questão 5 — Conectividade e custos

**Pergunta:**

Como permitir que tarefas em sub-redes privadas baixem imagens do ECR, acessem o SQS e alcancem um serviço externo sem expô-las diretamente à internet? Quais custos devem ser avaliados?

**Minha resposta:**

> como tudo está no ecossistema da AWS, não tem nada a mais do que se fazer para possibilitar baixar as imagens do ECR nem em ter acesso as filas SQS, apenas configurar as permissões necessárias das policies

**Correção e conceitos importantes:**

A resposta está parcialmente correta. As permissões IAM são necessárias, mas não substituem a conectividade de rede.

É importante distinguir:

- **IAM:** determina quais operações uma identidade pode executar.
- **Rede:** determina se existe um caminho de comunicação até o destino.

Para acessar o ECR a partir de sub-redes privadas, podemos avaliar VPC Endpoints apropriados ou outras formas de conectividade. O download das camadas das imagens também precisa ser considerado no planejamento.

Para acessar o SQS, precisamos garantir um caminho de rede até seu endpoint. Um VPC Endpoint de interface pode permitir esse acesso sem depender de um NAT Gateway.

Já o acesso a um serviço externo disponível pela internet pública pode exigir conectividade de saída, por exemplo, por meio de um NAT Gateway.

As alternativas precisam ser comparadas considerando as necessidades do laboratório e os custos envolvidos. Um NAT Gateway possui cobrança por hora e volume processado, enquanto VPC Endpoints de interface também possuem cobranças. A escolha deve considerar quais serviços serão acessados e por quanto tempo.

**Conceitos do CLF-C02:**
- IAM versus conectividade de rede.
- VPC Endpoints.
- NAT Gateway.
- Acesso a serviços AWS em sub-redes privadas.
- Controle de custos.

### Questão 6 — CloudWatch e monitoramento

**Pergunta:**

Como investigar o acúmulo de mensagens no SQS e identificar se o problema está na entrada de eventos, na capacidade de processamento do consumer ou nas falhas do serviço externo? Como utilizar essas informações para decidir quando escalar?

**Minha resposta:**

> as falhas com o serviço externo daria para ver pelos logs da aplicação enviadas para o CloudWatch, e o problema do acúmulo de mensagens daria para configurar alertas de quantidade de mensagens total na fila

**Correção e conceitos importantes:**

A resposta está correta. O CloudWatch Logs pode centralizar os logs da aplicação, permitindo investigar falhas no serviço externo, exceções, tentativas de processamento e abertura do Circuit Breaker.

As métricas do SQS também permitem acompanhar o comportamento da fila:

| Métrica | Finalidade |
|---|---|
| `ApproximateNumberOfMessagesVisible` | Estimar quantas mensagens estão disponíveis para consumo. |
| `ApproximateNumberOfMessagesNotVisible` | Estimar quantas mensagens estão temporariamente invisíveis, normalmente em processamento. |
| `ApproximateAgeOfOldestMessage` | Identificar a idade aproximada da mensagem mais antiga na fila. |
| `NumberOfMessagesSent` | Acompanhar o envio de mensagens. |
| `NumberOfMessagesReceived` | Acompanhar o recebimento de mensagens pelos consumidores. |

Alarmes do CloudWatch podem detectar o crescimento do backlog. Para aumentar automaticamente a capacidade do consumer, podemos associar métricas e políticas de escalabilidade ao serviço ECS.

Um alarme, por si só, não aumenta a quantidade de tarefas. Essa ação depende da configuração de escalabilidade.

**Conceitos do CLF-C02:**
- Amazon CloudWatch Logs.
- Métricas do SQS.
- Alarmes do CloudWatch.
- Monitoramento de aplicações.
- Escalabilidade automática no ECS.

### Questão 7 — Dead-Letter Queue (DLQ)

**Pergunta:**

Qual é a finalidade de uma DLQ e por que ela é importante em uma arquitetura baseada em processamento assíncrono?

**Minha resposta:**

> ela é importante para não perder eventos e retentar o processamento depois que o ecossistema estiver normalizado

**Correção e conceitos importantes:**

A resposta está parcialmente correta. A DLQ ajuda a preservar mensagens que não foram processadas com sucesso dentro das condições configuradas, permitindo investigá-las e realizar o reprocessamento posteriormente.

Entretanto, uma mensagem enviada à DLQ não é automaticamente reprocessada quando o sistema volta ao normal.

O fluxo típico é:

1. A mensagem chega à fila principal.
2. O consumer tenta processá-la.
3. Se o processamento falhar, a mensagem pode voltar a ficar disponível após o visibility timeout.
4. Ao ultrapassar o limite configurado de recebimentos, a mensagem pode ser encaminhada para a DLQ.
5. A mensagem permanece disponível para investigação e eventual reprocessamento por meio de um processo de redrive.

A DLQ ajuda a isolar mensagens problemáticas para que elas não continuem interferindo no processamento normal da fila principal.

**Conceitos do CLF-C02:**
- Amazon SQS.
- Dead-Letter Queue.
- Redrive policy.
- Visibility timeout.
- Recuperação de falhas.
- Processamento assíncrono.

### Questão 8 — Segurança do Redis

**Pergunta:**

Quais medidas devem ser adotadas para impedir que o Redis fique exposto publicamente e para limitar quais componentes podem acessá-lo?

**Minha resposta:**

> regras de security groups para liberar acesso apenas vindo de endereços e portas pre definidas

**Correção e conceitos importantes:**

A resposta está correta. Os Security Groups podem restringir as conexões ao Redis, permitindo acesso somente dos componentes autorizados.

Por exemplo, se o Redis utilizar a porta TCP `6379`, podemos permitir conexões nessa porta somente a partir dos Security Groups das tarefas da API e do consumer.

É preferível referenciar os Security Groups dos recursos autorizados, em vez de liberar intervalos amplos de endereços IP.

O Redis deve ficar em uma sub-rede privada, sem exposição direta à internet.

Também precisamos considerar autenticação, proteção das credenciais e criptografia, conforme a solução escolhida.

Os Security Groups são stateful, enquanto as Network ACLs são stateless e atuam no nível das sub-redes.

**Conceitos do CLF-C02:**
- Security Groups.
- Network ACLs.
- Sub-redes privadas.
- Controle de tráfego.
- Defesa em profundidade.

### Questão 9 — Persistência e disponibilidade do Redis

**Pergunta:**

Quais riscos existem ao executar o Redis em uma instância EC2? O que considerar em relação à persistência dos dados, à disponibilidade e à recuperação de falhas?

**Minha resposta:**

> teria o risco de se perder dados caso a instância do EC2 fosse terminada

**Correção e conceitos importantes:**

A resposta está parcialmente correta. Existe risco de perda de dados, mas isso depende do armazenamento utilizado e da configuração do Redis.

Uma instância EC2 pode utilizar:

- **Amazon EBS:** volumes persistentes que podem continuar existindo após o encerramento da instância, dependendo da configuração.
- **Instance Store:** armazenamento local temporário, cujos dados não persistem após determinados eventos do ciclo de vida da instância.
- **Memória:** os dados mantidos exclusivamente na memória do Redis são perdidos quando o processo ou a instância para.

Mesmo com armazenamento persistente, executar o Redis em uma única instância cria um ponto único de falha.

Como o Redis mantém informações de idempotência e estado compartilhado na aplicação, perder esses dados pode permitir que eventos já processados sejam considerados novos e enviados novamente ao serviço externo.

Portanto, precisamos avaliar persistência, backups, recuperação, disponibilidade e o impacto da perda dos registros de idempotência.

**Conceitos do CLF-C02:**
- Amazon EC2.
- Amazon EBS.
- Instance Store.
- Persistência de dados.
- Disponibilidade.
- Recuperação de falhas.

### Questão 10 — ECS com EC2 ou Fargate

**Pergunta:**

Você escolheria ECS com Fargate ou ECS com instâncias EC2 para executar os dois módulos? Por quê?

**Minha resposta:**

> escolheria ECS com instâncias EC2, porque é a arquiterura que já estou familiarizado do trabalho e poderíamos escolher as máquinas mais baratas

**Correção e conceitos importantes:**

A escolha é coerente com os objetivos do laboratório, pois permite aprofundar conhecimentos sobre ECS, EC2, capacidade do cluster e escalabilidade, além de aproveitar a experiência profissional existente.

No ECS com EC2, somos responsáveis por administrar as instâncias que fornecem capacidade computacional ao cluster. No ECS com Fargate, a AWS administra a infraestrutura subjacente e a cobrança considera os recursos alocados às tarefas conforme o modelo aplicável.

Entretanto, escolher uma instância barata não garante que o custo total será menor. É necessário considerar a memória e o processamento exigidos pela API, pelo consumer, pelo Redis e pelos demais componentes.

A decisão final deverá considerar o dimensionamento, o tempo de execução e os objetivos de aprendizagem.

**Conceitos do CLF-C02:**
- Amazon ECS.
- Amazon EC2.
- AWS Fargate.
- Modelos de cobrança.
- Dimensionamento de recursos.
- Controle de custos.

## 3. Principais aprendizados do diagnóstico

O diagnóstico identificou conhecimentos prévios importantes e alguns pontos que merecem atenção durante a implementação.

| Tema | Aprendizado principal |
|---|---|
| Mensageria | O SQS desacopla a API do processamento assíncrono. |
| Escalabilidade | A quantidade de consumidores pode ser ajustada conforme a demanda. |
| IAM | As permissões devem ser específicas para cada função e recurso. |
| Redes | IAM não substitui a conectividade necessária entre os serviços. |
| Alta disponibilidade | Distribuir recursos entre AZs reduz o impacto de falhas isoladas. |
| Observabilidade | Logs e métricas ajudam a identificar falhas e gargalos. |
| Recuperação | DLQs isolam mensagens problemáticas, mas não garantem reprocessamento automático. |
| Segurança | Security Groups devem permitir somente o tráfego necessário. |
| Persistência | A perda do estado de idempotência pode causar processamento duplicado. |
| Custos | A arquitetura deve ser dimensionada para o laboratório e ter recursos temporários removidos ao final. |

## 4. Arquitetura inicial proposta

A arquitetura será definida progressivamente durante o laboratório, mas a proposta inicial considera os seguintes componentes:

| Componente | Responsabilidade |
|---|---|
| Amazon ECR | Armazenar as imagens Docker da API e do consumer. |
| Amazon ECS com EC2 | Executar os containers da aplicação. |
| Amazon SQS | Transportar os eventos entre os módulos. |
| Dead-Letter Queue | Isolar mensagens que não foram processadas com sucesso. |
| Redis | Manter o estado compartilhado e os registros de idempotência. |
| Amazon VPC | Organizar e isolar os recursos de rede. |
| Security Groups | Controlar o tráfego entre os componentes. |
| AWS IAM | Autorizar o acesso aos serviços necessários. |
| Amazon CloudWatch | Centralizar logs e acompanhar métricas. |
| Terraform | Provisionar e gerenciar a infraestrutura como código. |

A arquitetura definitiva dependerá da validação da aplicação e das alternativas de conectividade e armazenamento disponíveis.

## 5. Restrições e cuidados do laboratório

O projeto seguirá os seguintes princípios:

- Utilizar a região AWS `sa-east-1`.
- Reaproveitar a VPC existente, sem recriá-la.
- Priorizar recursos gratuitos ou de baixo custo quando forem suficientes para o aprendizado.
- Avaliar os custos antes de criar recursos potencialmente cobrados.
- Aplicar o princípio do menor privilégio nas políticas IAM.
- Evitar exposição pública desnecessária.
- Utilizar Terraform para provisionar a infraestrutura.
- Documentar os testes e os resultados observados.
- Destruir os recursos temporários ao final do laboratório.
- Verificar se permaneceram recursos cobrados após a limpeza.

O fluxo de trabalho será:

**Entender → Planejar → Criar → Testar → Observar → Documentar → Destruir**

## 6. Próxima etapa

Antes de iniciar a implementação, será necessário confirmar como a aplicação está configurada para se conectar aos serviços externos.

A principal questão pendente é verificar se a aplicação está preparada para utilizar o Amazon SQS real e um Redis acessível pela rede AWS, ou se atualmente depende do LocalStack e do Valkey/Redis definidos no Docker Compose local.

Essa informação determinará quais ajustes de configuração serão necessários antes do provisionamento da infraestrutura.

## 7. Resultado esperado

Ao concluir o projeto, espera-se ter uma aplicação funcional integrada à AWS e uma documentação que demonstre, de forma prática, os principais conceitos do CLF-C02.

O foco não será construir uma arquitetura de produção completa, mas compreender as responsabilidades dos serviços, as decisões de arquitetura, os mecanismos de segurança e os impactos de custo de cada escolha.
