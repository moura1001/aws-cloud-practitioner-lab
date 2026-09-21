# 09 — ECS (Elastic Container Service)

**Status:** Concluído

**Resultado:** Bom entendimento dos conceitos fundamentais do Amazon ECS, especialmente a relação entre ECR, ECS Cluster, Task Definition, Task, Service e Fargate. O hands-on também demonstrou na prática o uso de IAM Least Privilege, CloudWatch Logs e o comportamento de self-healing de um ECS Service.

---

## Objetivo

Estudar o Amazon ECS como serviço de orquestração de containers e compreender como ele se relaciona com os conceitos estudados anteriormente:

```text
Docker
   ↓
ECR
   ↓
ECS
   ↓
Fargate
   ↓
Container
```

O laboratório também teve como objetivos:

* compreender ECS Cluster;
* diferenciar Task Definition, Task e Service;
* entender Fargate;
* executar um container armazenado no ECR;
* utilizar CloudWatch Logs;
* praticar IAM Least Privilege;
* validar o comportamento de substituição automática de Tasks;
* utilizar Terraform;
* destruir os recursos temporários ao final.

---

## Diagnóstico inicial

Antes da explicação, foram respondidas perguntas sobre ECS, containers, Task Definition, Task, Service, ECR, Fargate e disponibilidade.

### 1. O que é o Amazon ECS?

**Minha resposta:**

> O ECS é um serviço de gerenciamento de containeres facilitando a operação de clusteres de máquinas

**Avaliação:** Parcialmente correta.

O ECS é um serviço de **orquestração de containers**. Ele permite gerenciar a execução, implantação e operação de containers sem que o conceito principal seja simplesmente administrar clusters de máquinas.

A ideia mais importante para a CLF-C02 é:

```text
ECS
↓
Orquestração de containers
```

---

### 2. Qual é a relação entre ECS e ECR?

**Minha resposta:**

> ESC gerencia conteineres Docker configurados com templates das imagens armazenadas no ECR

**Avaliação:** Parcialmente correta.

A relação geral estava correta, mas é importante separar as responsabilidades:

```text
ECR
↓
Armazena e distribui imagens

ECS
↓
Gerencia a execução dos containers
```

O ECR não executa containers.

---

### 3. O que é um ECS Cluster?

**Minha resposta:**

> Um cluster é um conjunto de máquinas nas quais os containeres Docker serão executados

**Avaliação:** Parcialmente correta.

Essa definição funciona melhor quando ECS utiliza EC2 como capacidade computacional, mas é incompleta porque ECS também pode utilizar **Fargate**.

O Cluster é melhor entendido como um agrupamento lógico de recursos e workloads ECS.

---

### 4. O que é uma Task Definition?

**Minha resposta:**

> Task Definition é um template com as informações de como o container será executado em tempo de execução como quantidade de memória e cpu, variáveis de ambiente, etc

**Avaliação:** Correta.

A Task Definition é o template que especifica como uma Task deve ser executada.

Entre suas configurações podem estar:

* imagem;
* CPU;
* memória;
* modo de rede;
* roles;
* configuração de logs;
* variáveis de ambiente;
* containers.

---

### 5. O que é uma Task?

**Minha resposta:**

> Task é o template com as informações que o Container seguirá para executar

**Avaliação:** Incorreta.

A Task Definition é o template.

Uma **Task é uma instância em execução de uma Task Definition**.

```text
Task Definition
       ↓
     Task
       ↓
  Container
```

Essa diferença é importante para a CLF-C02.

---

### 6. O que é um ECS Service?

**Minha resposta:**

> Seria um conceito parecido co os services do arquivo de docker compose, onde cada um deles seria um container diferente executando isoladamente

**Avaliação:** Parcialmente correta.

A comparação com Docker Compose ajuda a criar uma intuição, mas o ECS Service possui uma responsabilidade diferente.

Um Service mantém uma quantidade desejada de Tasks em execução.

Por exemplo:

```text
desired_count = 2

Service
├── Task
└── Task
```

Se uma Task falhar, o Service pode iniciar outra para manter a quantidade desejada.

---

### 7. Onde fica armazenada a imagem utilizada pelo ECS?

**Minha resposta:**

> A imagem é armazenada no ECR

**Avaliação:** Correta.

O ECR funciona como Container Registry.

---

### 8. O ECS exige EC2?

**Minha resposta:**

> Aparentemente não pois existem outros tipos de serviço como o Fargate

**Avaliação:** Parcialmente correta.

A conclusão estava correta, mas Fargate não é outro serviço de orquestração.

Fargate é uma **opção de capacidade computacional** que pode ser utilizada pelo ECS.

---

### 9. O que é Fargate?

**Minha resposta:**

> Fargate seria uma alternativa ao EC2, provavelmente gerenciado pela AWS, mas não sei exatamente a diferença dele para o EC2

**Avaliação:** Parcialmente correta.

A ideia principal estava correta.

Com Fargate, a AWS gerencia a infraestrutura necessária para executar os containers. Não precisamos administrar diretamente as instâncias EC2 que executam os containers.

```text
ECS + EC2
→ nós gerenciamos as instâncias

ECS + Fargate
→ AWS gerencia a infraestrutura computacional
```

---

### 10. O que acontece se uma Task de um Service com duas Tasks parar?

**Minha resposta:**

> A outra instância continuará em execução e outra duplicata será criada para manter a configuração de 2 mínimas desejada

**Avaliação:** Correta.

Esse é exatamente o comportamento que foi validado posteriormente no hands-on.

---

### 11. Qual a diferença entre executar um container diretamente em EC2 e utilizar ECS?

**Minha resposta:**

> Não sei exatamente

**Avaliação:** Não respondida.

A principal diferença está no nível de gerenciamento.

Executar diretamente em EC2 significa que precisamos administrar a máquina e a execução dos containers.

Com ECS, o serviço fornece a camada de orquestração responsável por gerenciar os containers.

---

### 12. Quais componentes existiriam entre Internet, ECS e RDS?

**Minha resposta:**

> Não entendi com o que seria necessário preencher

**Avaliação:** A pergunta não ficou suficientemente clara.

O objetivo era introduzir uma arquitetura mais completa, que será desenvolvida posteriormente com outros módulos.

Não foi considerada uma falha conceitual do usuário.

---

## Conceitos estudados

### ECS

Amazon ECS é um serviço de orquestração de containers.

No laboratório:

```text
ECS
│
├── Cluster
│
├── Service
│
└── Task
```

---

## ECR x ECS

O modelo mental consolidado entre os dois módulos é:

```text
Docker
   ↓
Image
   ↓
ECR
   ↓
ECS
   ↓
Task
   ↓
Container
```

O ECR armazena e distribui a imagem.

O ECS gerencia a execução do container.

---

## Cluster

O ECS Cluster fornece um agrupamento lógico para os workloads ECS.

Ele não deve ser interpretado simplesmente como um conjunto de máquinas EC2.

O ECS pode utilizar diferentes opções de capacidade computacional, incluindo:

```text
ECS
├── EC2
└── Fargate
```

---

## Task Definition

A Task Definition é o template que descreve como uma Task deve ser executada.

No laboratório, ela especificou:

```text
Family:
aws-cloud-practitioner-lab

Launch Type:
FARGATE

Network Mode:
awsvpc

CPU:
256

Memory:
512
```

Também foram configurados:

* IAM Execution Role;
* imagem do ECR;
* container;
* CloudWatch Logs.

---

## Task

Uma Task é uma instância em execução de uma Task Definition.

O relacionamento é:

```text
Task Definition
       ↓
     Task
       ↓
   Container
```

Durante o laboratório, uma Task foi executada utilizando a imagem:

```text
234644232681.dkr.ecr.sa-east-1.amazonaws.com/aws-cloud-practitioner-lab:1.0
```

---

## Service

O ECS Service mantém o estado desejado das Tasks.

No laboratório:

```text
desired_count = 1
```

Portanto, o objetivo era manter uma Task em execução.

Quando a Task foi parada manualmente, o ECS iniciou outra automaticamente.

Isso demonstrou na prática:

```text
Service
   ↓
Desired Count = 1
   ↓
Task parada
   ↓
Nova Task iniciada
```

---

## Fargate

Fargate é uma opção de computação serverless para containers utilizada por serviços como ECS.

A principal diferença observada no laboratório é:

```text
ECS + EC2
→ gerenciamento das instâncias EC2

ECS + Fargate
→ AWS gerencia a infraestrutura subjacente
```

Isso permitiu realizar o laboratório sem criar e administrar uma instância EC2 dedicada para executar os containers.

---

## Networking

O ECS Service utilizou:

```text
VPC:
aws-cloud-practitioner-lab-vpc

Subnets:
public-a
public-b

Network Mode:
awsvpc

Public IP:
enabled
```

Foi utilizado um Security Group específico para o ECS.

O Security Group não possuía regras de entrada.

Isso foi suficiente para o objetivo deste laboratório porque ainda não havia um endpoint HTTP público ou Application Load Balancer.

O container foi utilizado principalmente para validar:

* execução;
* estado da Task;
* logs;
* substituição automática da Task.

Uma arquitetura com ALB será tratada posteriormente no projeto final.

---

# Hands-on

## 1. Reutilização do ECR

O módulo anterior criou o repository:

```text
aws-cloud-practitioner-lab
```

e a imagem:

```text
1.0
```

O ECS utilizou essa imagem em vez de criar uma nova imagem.

Isso demonstrou a relação entre os dois módulos:

```text
Day 8
Docker → ECR

Day 9
ECR → ECS
```

---

## 2. CloudWatch Log Group

Foi criado o Log Group:

```text
/aws/ecs/aws-cloud-practitioner-lab
```

com retenção de:

```text
1 day
```

A retenção curta foi utilizada para reduzir o acúmulo desnecessário de logs durante o laboratório.

---

## 3. IAM Execution Role

Foi criada a Role:

```text
aws-cloud-practitioner-lab-ecs-task-execution
```

Sua Trust Policy permite que:

```text
ecs-tasks.amazonaws.com
```

assuma a Role.

A Role recebeu:

```text
AmazonECSTaskExecutionRolePolicy
```

Essa Role é utilizada pela infraestrutura ECS para tarefas como acesso à imagem e envio de logs.

Ela não é a mesma identidade utilizada pelo Terraform.

---

## 4. ECS Cluster

Foi criado o Cluster:

```text
aws-cloud-practitioner-lab
```

---

## 5. Task Definition

A Task Definition foi criada com:

```text
Family:
aws-cloud-practitioner-lab

CPU:
256

Memory:
512

Launch Type:
FARGATE

Network Mode:
awsvpc
```

O container utilizou:

```text
aws-cloud-practitioner-lab:1.0
```

e enviou logs para:

```text
/aws/ecs/aws-cloud-practitioner-lab
```

---

## 6. ECS Service

Foi criado um Service:

```text
aws-cloud-practitioner-lab
```

com:

```text
Desired Count:
1

Launch Type:
FARGATE
```

As Tasks foram executadas nas subnets públicas existentes.

---

# Problemas encontrados durante o laboratório

## Service-Linked Role

A primeira tentativa de criação do ECS Service falhou porque a Service-Linked Role necessária para o ECS ainda não existia.

O erro indicou a necessidade de:

```text
iam:CreateServiceLinkedRole
```

A policy IAM específica do laboratório foi ajustada para permitir a criação da Service-Linked Role do ECS, restringindo a ação ao serviço ECS.

Depois do ajuste, o `terraform apply` foi concluído com sucesso:

```text
Apply complete!
Resources: 7 added, 0 changed, 0 destroyed.
```

### Aprendizado

Assim como ocorreu no RDS, uma operação de um serviço AWS pode exigir permissões IAM auxiliares para que a própria AWS configure componentes necessários ao serviço.

---

## CloudWatch Logs

Durante a observação dos logs, a primeira tentativa de consultar os Log Streams retornou `AccessDenied`.

Foi necessário adicionar:

```text
logs:DescribeLogStreams
```

à policy do usuário.

Depois disso, foi possível consultar o Log Stream e recuperar os eventos.

### Aprendizado

Existe uma diferença importante entre:

```text
criar um recurso
```

e:

```text
observar / consultar um recurso
```

Uma policy Least Privilege precisa considerar também as operações de observabilidade utilizadas durante o laboratório.

---

# Validação

## ECS Service

O Service foi validado com:

```text
Status:
ACTIVE

Desired:
1

Running:
1

Pending:
0
```

Isso confirmou que o Service estava mantendo uma Task em execução.

---

## ECS Task

A primeira Task executada foi:

```text
4a423f6ccf7047e5b24c1bdc52e93715
```

Ela apresentou:

```text
Desired Status:
RUNNING

Last Status:
RUNNING

Launch Type:
FARGATE

CPU:
256

Memory:
512
```

O container também estava:

```text
RUNNING
```

A imagem utilizada foi:

```text
aws-cloud-practitioner-lab:1.0
```

---

## CloudWatch Logs

Depois da correção da permissão `logs:DescribeLogStreams`, foi possível encontrar o Log Stream:

```text
ecs/aws-cloud-practitioner-lab/4a423f6ccf7047e5b24c1bdc52e93715
```

Os logs mostraram a inicialização do Nginx, incluindo:

```text
/docker-entrypoint.sh
nginx/1.31.6
start worker processes
```

Isso confirmou que não apenas a Task estava marcada como `RUNNING`: o container efetivamente iniciou o processo da aplicação.

---

# Teste de self-healing

Um dos principais experimentos do módulo foi parar manualmente a Task.

Foi utilizado:

```bash
aws ecs stop-task \
  --cluster aws-cloud-practitioner-lab \
  --task 4a423f6ccf7047e5b24c1bdc52e93715 \
  --region sa-east-1
```

A Task original passou para:

```text
Desired Status:
STOPPED

Last Status:
STOPPED

Stop Code:
UserInitiated
```

Porém, o ECS Service rapidamente iniciou uma nova Task para manter:

```text
desired_count = 1
```

A nova Task foi:

```text
81dd7f0c63de4fa9883badd621e347b1
```

e ficou:

```text
Last Status:
RUNNING

Desired Status:
RUNNING
```

### O que esse teste demonstrou

```text
Service
   │
   ├── Desired = 1
   │
   └── Task A
          ↓
       STOPPED
          ↓
   ECS detecta diferença
          ↓
       Task B
          ↓
       RUNNING
```

Esse foi um dos principais aprendizados práticos do módulo: o Service não representa simplesmente um container; ele mantém o estado desejado das Tasks.

---

# Terraform

O módulo foi implementado com Terraform.

Os principais recursos utilizados foram:

```text
aws_cloudwatch_log_group
aws_iam_role
aws_iam_role_policy_attachment
aws_security_group
aws_ecs_cluster
aws_ecs_task_definition
aws_ecs_service
```

Também foram utilizados Data Sources para descobrir recursos existentes:

```text
VPC
Subnets públicas
ECR Repository
```

Isso evitou hardcode desnecessário dos recursos criados nos módulos anteriores.

---

## Validação final do Terraform

Depois dos testes e da substituição automática da Task:

```bash
terraform plan
```

retornou:

```text
No changes.
Your infrastructure matches the configuration.
```

Isso confirmou que o comportamento dinâmico do ECS não gerou alterações indevidas no Terraform State.

---

# IAM e Least Privilege

O laboratório utilizou uma policy específica:

```text
aws-cloud-practitioner-lab-ecs
```

A policy foi utilizada pelo usuário:

```text
aws-cloud-practitioner-lab
```

Ela foi ajustada durante o laboratório conforme as operações reais exigiram.

Entre as permissões relevantes identificadas estavam:

```text
ECS management
IAM Role management
CloudWatch Logs
Security Groups
iam:PassRole
iam:CreateServiceLinkedRole
logs:DescribeLogStreams
```

Durante o cleanup também foi necessário considerar permissões diferentes das utilizadas durante a criação.

Entre elas esteve:

```text
iam:ListInstanceProfilesForRole
```

Também ocorreu uma falha envolvendo:

```text
iam:DetachUserPolicy
```

durante o processo de destruição da policy gerenciada pelo próprio Terraform.

O cleanup foi concluído com sucesso após os ajustes necessários.

### Principal aprendizado

```text
Create
  ≠
Observe
  ≠
Destroy
```

Cada operação pode exigir permissões diferentes.

Isso foi observado novamente neste módulo, reforçando o conceito de Least Privilege estudado desde IAM.

---

# Arquitetura do laboratório

A arquitetura final utilizada no Day 9 foi:

```text
                    AWS
                     │
              ┌──────▼──────┐
              │     ECR     │
              │   Image     │
              │     1.0     │
              └──────┬──────┘
                     │
                     ▼
              ┌─────────────┐
              │ ECS Cluster │
              └──────┬──────┘
                     │
                ECS Service
               desired = 1
                     │
                     ▼
              ┌─────────────┐
              │   Fargate   │
              │     Task    │
              └──────┬──────┘
                     │
                     ▼
                Container
                  Nginx
                     │
                     ▼
             CloudWatch Logs
```

Rede:

```text
VPC 10.0.0.0/16
│
├── public-a
│
└── public-b
       │
       ▼
   Fargate Tasks
```

---

# Controle de custos

O Fargate possui cobrança enquanto as Tasks estão em execução, portanto o recurso não foi mantido após o laboratório.

Também foram utilizadas medidas para reduzir custos:

* apenas uma Task;
* CPU `256`;
* memória `512`;
* retenção de logs de apenas 1 dia;
* nenhuma NAT Gateway;
* nenhuma EC2 dedicada ao ECS;
* nenhum Load Balancer;
* nenhum recurso adicional desnecessário.

Após os testes:

```text
ECS resources
     ↓
terraform destroy
     ↓
removidos
```

O ECR utilizado pelo laboratório também foi posteriormente limpo e destruído no módulo correspondente.

A estratégia de custo permaneceu:

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

# Pegadinhas importantes para a CLF-C02

* **ECR armazena imagens; ECS executa e gerencia containers.**
* **Task Definition é um template.**
* **Task é uma instância de uma Task Definition.**
* **Service mantém o número desejado de Tasks.**
* **Fargate é uma opção de computação para containers, não um substituto do ECS como serviço de orquestração.**
* **ECS pode utilizar EC2 ou Fargate.**
* **Fargate reduz a necessidade de administrar servidores/instâncias subjacentes.**
* **Cluster não deve ser confundido simplesmente com um conjunto de máquinas EC2.**
* **Uma Task parada por falha pode ser substituída pelo Service para manter o desired count.**
* **Security Group controla tráfego de rede; IAM controla permissões AWS.**
* **CloudWatch Logs permite observar a execução dos containers.**
* **Execution Role do ECS Task não é a mesma coisa que a identidade IAM utilizada pelo Terraform.**

---

# Checklist

* [x] Estudar ECS
* [x] Entender ECS Cluster
* [x] Entender Task Definition
* [x] Entender Task
* [x] Entender ECS Service
* [x] Entender Fargate
* [x] Relacionar ECS e ECR
* [x] Criar ECS Cluster com Terraform
* [x] Criar Task Definition
* [x] Criar ECS Service
* [x] Executar container utilizando imagem do ECR
* [x] Configurar CloudWatch Logs
* [x] Validar Task em estado `RUNNING`
* [x] Observar logs reais do container
* [x] Testar substituição automática de Task
* [x] Praticar Least Privilege
* [x] Corrigir permissões de Service-Linked Role
* [x] Corrigir permissão de observabilidade
* [x] Validar `terraform plan`
* [x] Destruir os recursos temporários
* [x] Consolidar conceitos da CLF-C02

---

# Conclusão

O Day 9 consolidou a relação entre os conceitos de Docker e ECR estudados anteriormente e o gerenciamento de containers pelo ECS.

O principal modelo mental aprendido foi:

```text
Docker
   ↓
Image
   ↓
ECR
   ↓
ECS
   ↓
Service
   ↓
Task
   ↓
Container
```

Também foi demonstrado na prática que o ECS Service pode manter o estado desejado das Tasks, substituindo automaticamente uma Task que foi interrompida.

Além disso, o laboratório reforçou uma lição recorrente do projeto: **Least Privilege precisa considerar não apenas a criação dos recursos, mas também observabilidade, atualização e destruição**.

**Day 9 — ECS: concluído.**

**Próximo módulo: Day 10 — SQS.**
