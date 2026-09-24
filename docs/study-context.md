# AWS Cloud Practitioner Lab — Contexto Mestre

## 1. Objetivo do projeto

Este repositório é um laboratório prático criado para estudar e consolidar os conhecimentos necessários para a certificação **AWS Certified Cloud Practitioner (CLF-C02)**.

O objetivo não é apenas decorar conceitos para a prova. O estudo combina:

* fundamentos de Cloud Computing;
* conceitos da AWS;
* serviços AWS;
* segurança;
* custos;
* arquitetura;
* prática com AWS;
* Terraform;
* documentação;
* construção gradual de um projeto final.

O laboratório também deve servir como um projeto profissional para portfólio no GitHub.

---

## 2. Estrutura atual do projeto

```text
aws-cloud-practitioner-lab/

├── 01-cloud-concepts/
├── 02-global-infrastructure/
├── 03-iam/
├── 04-vpc/
├── 05-ec2/
├── 06-s3/
├── 07-rds/
├── 08-ecr/
├── 09-ecs/
├── 10-sqs/
├── 11-cloudwatch/
├── 12-auto-scaling/
├── 13-final-project/
├── docs/
│   ├── architecture/
│   ├── cost-control/
│   ├── notes/
│   ├── learning-log.md
│   └── study-context.md
└── README.md
```

---

## 3. Plano dos módulos

| #  | Lab / Topic           | Hands-on | Status        |
| -- | --------------------- | -------- | ------------- |
| 01 | Cloud Concepts        | N/A      | ✅ Completed   |
| 02 | Global Infrastructure | N/A      | ✅ Completed   |
| 03 | IAM                   | Hands-on | ✅ Completed   |
| 04 | VPC                   | Hands-on | ✅ Completed   |
| 05 | EC2                   | Hands-on | ✅ Completed   |
| 06 | S3                    | Hands-on | ✅ Completed   |
| 07 | RDS                   | Hands-on | ✅ Completed   |
| 08 | ECR                   | Hands-on | ✅ Completed   |
| 09 | ECS                   | Hands-on | ✅ Completed   |
| 10 | SQS                   | Hands-on | ✅ Completed   |
| 11 | CloudWatch            | Hands-on | ⬜ Not started |
| 12 | Auto Scaling          | Hands-on | ⬜ Not started |
| 13 | Final Project         | Hands-on | ⬜ Not started |

**Próximo módulo: Day 11 — CloudWatch.**

---

## 4. Metodologia das aulas

Cada dia de estudo deve seguir, preferencialmente, esta ordem:

### Etapa 1 — Perguntas / diagnóstico

Antes do conteúdo detalhado, apresentar algumas perguntas sobre o assunto do dia.

Objetivos:

* descobrir o conhecimento prévio;
* estimular raciocínio antes da explicação;
* identificar conceitos que precisam de maior atenção;
* aproximar o estudo do formato da prova CLF-C02.

As perguntas devem ser respondidas pelo aluno antes da explicação completa.

Depois das respostas:

1. corrigir cada questão;
2. indicar o que estava correto;
3. corrigir eventuais erros;
4. explicar o motivo;
5. destacar conceitos importantes para a prova.

Não entregar simplesmente o gabarito sem explicação.

---

### Etapa 2 — Explicação dos conceitos

Depois das perguntas, explicar os conceitos necessários para compreender o tema.

A explicação deve ser:

* didática;
* progressiva;
* prática;
* relacionada à AWS;
* relacionada à prova CLF-C02;
* suficientemente detalhada para criar entendimento real.

Evitar explicações excessivamente acadêmicas ou longas quando uma analogia ou exemplo simples resolver.

Sempre que possível, estabelecer relações entre os serviços e conceitos já estudados.

---

### Etapa 3 — Hands-on

Quando o assunto permitir, realizar uma prática na AWS e/ou Terraform.

A abordagem deve ser incremental:

```text
entender → criar → testar → observar → documentar → destruir
```

O aluno deve entender o motivo de cada comando e recurso criado.

Antes de criar recursos que possam gerar cobrança, explicar:

* se existe custo;
* qual é o risco;
* como verificar;
* como remover o recurso.

---

## 5. Regra de segurança de custos

O laboratório utiliza uma conta AWS pessoal.

Portanto, **controle de custos é prioridade**.

Sempre:

* preferir recursos gratuitos quando suficientes para o aprendizado;
* evitar recursos pagos desnecessários;
* explicar custos antes de criar recursos potencialmente cobrados;
* usar uma única região, atualmente `sa-east-1`;
* destruir recursos temporários ao terminar o laboratório;
* nunca deixar recursos pagos ativos sem necessidade.

Regra prática:

```text
create → test → document → destroy
```

Quando um recurso precisar permanecer para um módulo futuro, explicar explicitamente por quê.

---

## 6. Terraform

Terraform deve ser utilizado sempre que fizer sentido para o laboratório.

Princípios:

* infraestrutura como código;
* configuração versionada no Git;
* evitar credenciais hardcoded;
* utilizar as credenciais configuradas no ambiente AWS CLI;
* nunca colocar Access Key ou Secret Access Key no repositório;
* manter `.terraform/`, `terraform.tfstate` e arquivos de variáveis sensíveis fora do Git.

O aluno utiliza AWS CLI localmente para autenticação.

Os módulos devem evitar hardcode de IDs de infraestrutura existente quando for possível descobrir os recursos através de tags ou outros mecanismos do Terraform.

## Metodologia obrigatória de cada módulo

Cada módulo de estudo deve seguir a mesma sequência:

1. **Diagnóstico inicial**

   * Antes de qualquer explicação, apresentar perguntas de diagnóstico.
   * O usuário responde sem pesquisar, mesmo que não tenha certeza.
   * Não corrigir ou explicar antes de receber as respostas.

2. **Correção do diagnóstico**

   * Reproduzir a resposta original do usuário utilizando o formato:
     **Minha resposta:**
   * Avaliar cada resposta individualmente.
   * Indicar o que estava correto, parcialmente correto ou incorreto.
   * Explicar a correção e destacar possíveis pegadinhas da CLF-C02.
   * Não substituir a resposta original por uma resposta "melhorada".

3. **Explicação**

   * Somente depois do diagnóstico e das correções, explicar os conceitos do módulo.
   * Relacionar os conceitos novos com módulos anteriores quando houver conexão relevante.
   * Priorizar o entendimento do "por quê" antes dos comandos.

4. **Hands-on**

   * Seguir o fluxo:
     **entender → criar → testar → observar → documentar → destruir**
   * Utilizar Terraform quando fizer sentido para o laboratório.
   * Aplicar least privilege nas permissões IAM.
   * Considerar custos antes da criação de recursos potencialmente cobrados.

5. **Documentação**

   * Cada módulo deve possuir seu próprio `README.md`.
   * O README deve registrar:

     * objetivo;
     * diagnóstico completo;
     * respostas originais do usuário;
     * correções;
     * conceitos estudados;
     * arquitetura;
     * implementação;
     * validação;
     * controle de custos;
     * pontos de atenção para CLF-C02;
     * conclusão/status.
   * `docs/learning-log.md` deve receber um resumo conciso do módulo, sem simplesmente copiar o README.
   * `docs/study-context.md` deve registrar o estado atual do curso e informações necessárias para continuar em outro chat.

6. **Continuidade entre chats**

   * Antes de iniciar um novo módulo, utilizar `docs/study-context.md` e `docs/learning-log.md` como fonte de contexto.
   * Não inferir informações que não estejam nesses arquivos ou no contexto atual.
   * Se houver dúvida sobre como um módulo anterior foi conduzido, **perguntar antes de prosseguir**.
   * Preservar as respostas originais do usuário nos READMEs dos módulos.

---

## 7. Segurança

Nunca solicitar ao aluno:

* Access Key;
* Secret Access Key;
* senha;
* token;
* credenciais;
* dados sensíveis.

Quando for necessário mostrar uma saída que contenha informações sensíveis, utilizar placeholders.

---

## 8. Documentação de cada módulo

Cada módulo deve possuir seu próprio:

```text
README.md
```

O README deve registrar, conforme aplicável:

* objetivo;
* conceitos estudados;
* comandos;
* recursos criados;
* testes realizados;
* resultados;
* conceitos importantes para a CLF-C02;
* limpeza dos recursos;
* conclusão.

---

## 9. Learning Log

Ao finalizar cada dia, atualizar:

```text
docs/learning-log.md
```

O registro deve conter:

* dia/módulo;
* conteúdos estudados;
* principais aprendizados;
* hands-on realizado;
* resultados dos testes;
* conceitos importantes;
* recursos criados;
* informações relevantes de custo;
* checklist de conclusão.

O learning log deve ser um histórico conciso, não uma cópia integral do README do módulo.

---

## 10. Git

Após concluir um módulo:

1. verificar `git status`;
2. verificar se não existem credenciais ou arquivos sensíveis;
3. revisar as alterações;
4. fazer commit com mensagem clara;
5. enviar para o GitHub.

Padrão:

```text
tipo: descrição curta em inglês
```

Tipos utilizados:

* `docs:` → documentação, anotações, atualização de status e learning log;
* `feat:` → implementação de novos recursos ou infraestrutura do laboratório.

Não criar commits duplicados sem necessidade.

Antes de novos commits:

```bash
git status
git log --oneline --decorate -n 10
```

---

## 11. Relação entre os módulos

O curso deve ser progressivo.

Não tratar cada serviço como um assunto completamente isolado.

A ideia é construir gradualmente a arquitetura final.

Arquitetura planejada:

```text
Internet

   ↓

ALB

   ↓

ECS / Fargate

   ├── SQS
   │
   └── RDS

        ↓

CloudWatch

Docker → ECR → ECS
```

A relação entre os serviços deve ser apresentada conforme eles forem estudados.

Exemplos:

```text
IAM Role
    ↓
EC2

ECR
    ↓
ECS

VPC / Private Subnets
    ↓
RDS

ECS
    ↓
SQS

ECS / RDS / SQS
    ↓
CloudWatch
```

---

## 12. Como iniciar um novo dia

Quando o aluno disser algo como:

> "Vamos continuar"

ou:

> "Podemos fazer o próximo dia?"

Deve-se:

1. consultar o estado atual do plano;
2. identificar o último módulo concluído;
3. iniciar o próximo módulo;
4. não repetir módulos já concluídos sem necessidade;
5. começar pelas perguntas;
6. depois explicar/corrigir;
7. realizar o hands-on;
8. produzir as anotações;
9. atualizar o README;
10. atualizar o `docs/learning-log.md`;
11. atualizar a tabela de Labs;
12. indicar claramente o próximo módulo.

**Estado atual: Day 8 — ECR concluído.**

O próximo módulo é:

**Day 9 — ECS.**

---

## 13. Estado atual do curso

### Day 1 — Cloud Concepts

Concluído.

Principais conceitos:

* Cloud Computing;
* CapEx vs OpEx;
* Pay-as-you-go;
* Scalability;
* Elasticity;
* High Availability;
* Fault Tolerance;
* Region;
* Availability Zone;
* Edge Location.

---

### Day 2 — Global Infrastructure

Concluído.

Principais conceitos:

* AWS Regions;
* Availability Zones;
* Edge Locations;
* AWS Global Infrastructure;
* AWS Well-Architected Framework;
* Operational Excellence;
* Security;
* Reliability;
* Performance Efficiency;
* Cost Optimization;
* Sustainability.

---

### Day 3 — IAM

Concluído.

Principais conceitos:

* Authentication vs Authorization;
* IAM User;
* IAM Policy;
* Least Privilege;
* Root User;
* MFA;
* Access Keys;
* IAM Role;
* Trust Policy;
* Permissions Policy;
* credenciais temporárias.

Hands-on:

* criação do IAM User `aws-cloud-practitioner-lab`;
* configuração do AWS CLI;
* utilização do Terraform;
* criação de policy de Least Privilege;
* teste de `AccessDenied`;
* criação da Role `aws-cloud-practitioner-lab-ec2-s3-read`;
* configuração de `AmazonS3ReadOnlyAccess`;
* validação da Trust Policy;
* validação da Permissions Policy.

A Role permanece para utilização no módulo de EC2.

---

### Day 4 — VPC

Concluído.

Infraestrutura principal:

```text
VPC:
10.0.0.0/16

sa-east-1a
├── public-a  → 10.0.1.0/24
└── private-a → 10.0.11.0/24

sa-east-1b
├── public-b  → 10.0.2.0/24
└── private-b → 10.0.12.0/24
```

Foram estudados:

* VPC;
* CIDR;
* Subnets;
* Route Tables;
* Internet Gateway;
* Security Groups;
* subnet pública vs privada;
* NAT Gateway;
* Availability Zones;
* Least Privilege.

NAT Gateway não foi criado devido ao custo.

---

### Day 5 — EC2

Concluído.

Foram estudados:

* EC2;
* AMI;
* Instance Type;
* EBS;
* Instance Store;
* Security Group;
* IP privado e público;
* IAM Role;
* Instance Profile;
* Stop vs Start;
* Stop vs Terminate;
* `DeleteOnTermination`;
* Terraform State;
* `terraform apply -refresh-only`;
* Least Privilege.

Hands-on:

```text
Instance Type: t3.nano
AMI: Amazon Linux 2023 Minimal
AZ: sa-east-1a
Subnet: public-a
EBS: gp3 / 2 GiB
```

A EC2 utilizou a Role:

```text
aws-cloud-practitioner-lab-ec2-s3-read
```

Os recursos temporários foram destruídos.

---

### Day 6 — S3

Concluído.

Foram estudados:

* S3 como Object Storage;
* Bucket;
* Object;
* Object Key;
* Bucket names globalmente únicos;
* S3 vs EBS vs EFS;
* Storage Classes;
* Versioning;
* Delete Marker;
* IAM Policy;
* Bucket permissions vs Object permissions;
* Block Public Access;
* Least Privilege.

Hands-on:

* criação de bucket com Terraform;
* Block Public Access;
* upload;
* download;
* list;
* delete;
* Versioning;
* Delete Marker;
* recuperação de versão;
* cleanup automático com `force_destroy`.

O bucket e seus objetos foram destruídos com sucesso.

Policy utilizada:

```text
aws-cloud-practitioner-lab-s3
```

---

### Day 7 — RDS

Concluído.

Foram estudados:

* RDS como serviço gerenciado de banco relacional;
* MySQL;
* DB Subnet Group;
* RDS em subnet privada;
* Security Group;
* Multi-AZ;
* Read Replica;
* Backup;
* Snapshot;
* Point-in-Time Recovery;
* IAM;
* Service-Linked Role;
* Least Privilege.

Hands-on realizado:

```text
Engine: MySQL
Instance: db.t4g.micro
Storage: 20 GB gp3
Encryption: enabled
Publicly accessible: false
Multi-AZ: false
Port: 3306
```

O RDS utilizou as subnets privadas existentes:

```text
private-a → subnet-0117c0db343d98f9b
private-b → subnet-0e85409ac28b247d6
```

A VPC utilizada foi:

```text
vpc-09902363bc6acf897
```

Foi criado um Security Group específico para o RDS.

Na validação final:

```text
Ingress: []
Egress: []
```

Portanto, nenhuma conexão de entrada foi autorizada.

### Problema de IAM encontrado

A primeira tentativa de criação do RDS falhou por ausência de:

```text
iam:CreateServiceLinkedRole
```

A policy `aws-cloud-practitioner-lab-rds` foi ajustada para permitir a criação da Service-Linked Role específica do RDS.

Após o ajuste, o RDS foi criado com sucesso.

A Role `AWSServiceRoleForRDS` foi validada.

### Validação final

O RDS ficou:

```text
Status: available
```

E foi confirmado utilizando AWS CLI:

```text
VPC:
vpc-09902363bc6acf897

Subnets:
subnet-0e85409ac28b247d6
subnet-0117c0db343d98f9b

Security Group:
sg-0a5083136ae021200

PubliclyAccessible:
false

MultiAZ:
false

Port:
3306
```

Após os testes, o RDS foi destruído com sucesso.

O Terraform State permaneceu somente com:

```text
aws_iam_policy.rds
aws_iam_user_policy_attachment.rds
```

A policy IAM permanece para o módulo.

---

### Day 8 — ECR

Concluído.

Foram estudados:

* ECR como Container Registry;
* Docker Image x Container;
* Registry x Repository;
* Image Tag;
* Image Digest;
* Push x Pull;
* ECR x ECS;
* ECR x VPC;
* IAM e Least Privilege.

Hands-on realizado:

```text
Docker Image
    ↓
ECR Repository
    ↓
docker push
    ↓
ECR
    ↓
docker pull
```

Foi criado o repository:

```text
aws-cloud-practitioner-lab
```

na região:

```text
sa-east-1
```

Foi criada e anexada ao usuário a policy:

```text
aws-cloud-practitioner-lab-ecr
```

A imagem Docker recebeu a tag:

```text
1.0
```

e foi enviada ao ECR.

A imagem foi validada utilizando AWS CLI através de:

```bash
aws ecr describe-images
```

Depois foi removida do ambiente Docker local e recuperada novamente utilizando `docker pull`, demonstrando o fluxo de armazenamento e distribuição do ECR.

### Cleanup

Durante o cleanup, o repository inicialmente não pôde ser destruído porque ainda continha imagens.

Após a remoção necessária das imagens, o repository foi destruído com sucesso.

Também foi identificado durante o cleanup que a policy do usuário precisava da permissão:

```text
iam:DetachUserPolicy
```

para que o Terraform pudesse remover sua associação.

Isso reforçou a diferença entre as permissões necessárias para criação e destruição de recursos.

### Estado final

Os recursos temporários do ECR foram destruídos.

A policy:

```text
aws-cloud-practitioner-lab-ecr
```

permanece associada ao usuário para o contexto do laboratório, conforme o padrão adotado nos módulos anteriores.

### Relação com os próximos módulos

O principal modelo mental consolidado foi:

```text
Docker
   ↓
ECR
   ↓
ECS
   ↓
Container
```

O próximo módulo utilizará esse conhecimento para estudar o **Amazon ECS**, responsável pelo gerenciamento da execução dos containers.

**Day 8 — ECR: concluído.**

---

### Day 9 — ECS

Concluído.

Foram estudados:

* ECS como serviço de orquestração de containers;
* ECS Cluster;
* Task Definition;
* Task;
* ECS Service;
* Fargate;
* ECS × ECR;
* Execution Role;
* CloudWatch Logs;
* Security Group;
* IAM Least Privilege;
* desired count;
* substituição automática de Tasks.

Hands-on realizado com Terraform:

```text
ECR
 ↓
ECS Cluster
 ↓
ECS Service
 ↓
Fargate Task
 ↓
Container
 ↓
CloudWatch Logs
```

Configuração principal:

```text
Cluster:
aws-cloud-practitioner-lab

Service:
aws-cloud-practitioner-lab

Launch Type:
FARGATE

Desired Count:
1

CPU:
256

Memory:
512

Network Mode:
awsvpc

Log Group:
/aws/ecs/aws-cloud-practitioner-lab
```

A imagem utilizada foi:

```text
aws-cloud-practitioner-lab:1.0
```

proveniente do ECR utilizado no Day 8.

### Validações realizadas

O ECS Service foi validado com uma Task em estado `RUNNING`.

Os logs do container foram recuperados pelo CloudWatch Logs e demonstraram a inicialização do Nginx.

Uma Task foi interrompida manualmente utilizando AWS CLI. O ECS Service iniciou automaticamente uma nova Task para manter:

```text
desired_count = 1
```

O Terraform também foi validado após o teste:

```text
No changes.
Your infrastructure matches the configuration.
```

### Problemas de IAM encontrados

A criação do ECS Service inicialmente exigiu:

```text
iam:CreateServiceLinkedRole
```

A policy `aws-cloud-practitioner-lab-ecs` foi ajustada para permitir a criação da Service-Linked Role específica do ECS.

Para observação dos logs foi necessário:

```text
logs:DescribeLogStreams
```

Durante o cleanup também foram identificadas permissões adicionais necessárias para operações de destruição, incluindo:

```text
iam:ListInstanceProfilesForRole
iam:DetachUserPolicy
```

Esses problemas reforçaram o princípio de que as permissões necessárias para criar, observar e destruir recursos podem ser diferentes.

### Controle de custos

Foi utilizada uma única Fargate Task com CPU `256` e memória `512`.

Foi utilizada retenção de logs de 1 dia.

Não foram utilizados:

* NAT Gateway;
* Load Balancer;
* EC2 dedicada para o ECS.

Os recursos temporários do ECS foram destruídos após os testes.

**Estado atual: Day 9 — ECS concluído.**

---

## 3. Plano dos módulos

| #  | Lab / Topic           | Hands-on | Status        |
| -- | --------------------- | -------- | ------------- |
| 01 | Cloud Concepts        | N/A      | ✅ Completed   |
| 02 | Global Infrastructure | N/A      | ✅ Completed   |
| 03 | IAM                   | Hands-on | ✅ Completed   |
| 04 | VPC                   | Hands-on | ✅ Completed   |
| 05 | EC2                   | Hands-on | ✅ Completed   |
| 06 | S3                    | Hands-on | ✅ Completed   |
| 07 | RDS                   | Hands-on | ✅ Completed   |
| 08 | ECR                   | Hands-on | ✅ Completed   |
| 09 | ECS                   | Hands-on | ✅ Completed   |
| 10 | SQS                   | Hands-on | ✅ Completed   |
| 11 | CloudWatch            | Hands-on | ⬜ Not started |
| 12 | Auto Scaling          | Hands-on | ⬜ Not started |
| 13 | Final Project         | Hands-on | ⬜ Not started |

**Próximo módulo: Day 11 — CloudWatch.**

---

### Day 10 — SQS

**Status:** Concluído.

### Principais conceitos estudados

* Amazon SQS;
* comunicação síncrona x assíncrona;
* desacoplamento;
* Producer / Consumer;
* Queue / Message;
* ciclo de vida de uma mensagem;
* `SendMessage`;
* `ReceiveMessage`;
* `DeleteMessage`;
* `ReceiptHandle`;
* Visibility Timeout;
* at-least-once delivery;
* idempotência;
* Standard Queue;
* FIFO Queue;
* Message Retention;
* Long Polling;
* Dead-Letter Queue (DLQ);
* SQS x SNS;
* integração conceitual com ECS/EC2;
* IAM e least privilege;
* custo baseado principalmente em requisições/operações.

### Hands-on realizado

Foi criada uma fila SQS com Terraform:

```text
aws-cloud-practitioner-lab
```

Configurações principais:

```text
Visibility Timeout: 30 segundos
Message Retention: 24 horas
Long Polling: 10 segundos
Region: sa-east-1
```

Foi criada uma política IAM específica para o laboratório, contendo somente as permissões necessárias para a criação, gerenciamento e utilização da fila.

A criação inicialmente falhou com `AccessDenied` porque o usuário não possuía `sqs:CreateQueue`. Após a criação e associação da política específica de SQS, o Terraform conseguiu criar a fila sem AdministratorAccess.

Foi utilizada uma dependência explícita:

```hcl
depends_on = [
  aws_iam_user_policy_attachment.sqs
]
```

Isso garantiu que o attachment da política fosse concluído antes da tentativa de criação da fila.

### Testes realizados

Foram realizados testes de:

1. obtenção da Queue URL;
2. envio de mensagem;
3. recebimento de mensagem;
4. observação do `MessageId`;
5. observação do `ReceiptHandle`;
6. recebimentos consecutivos para observar a possibilidade de duplicação em Standard Queue;
7. exclusão da mensagem com `DeleteMessage`;
8. validação de que a mensagem excluída não estava mais disponível;
9. teste de Visibility Timeout sem excluir a mensagem;
10. observação da mensagem voltar a ficar disponível;
11. observação do Long Polling quando a fila estava vazia.

### Principais aprendizados

```text
Producer
    ↓
SendMessage
    ↓
SQS Queue
    ↓
ReceiveMessage
    ↓
Visibility Timeout
    ↓
Processamento
    ↓
DeleteMessage
```

Se o consumidor não excluir a mensagem após o processamento, ela poderá voltar a ficar disponível.

Como o SQS Standard utiliza entrega at-least-once, consumidores devem considerar a possibilidade de mensagens duplicadas e implementar idempotência quando necessário.

### Recursos criados

* `aws_sqs_queue.app`
* `aws_iam_policy.sqs`
* `aws_iam_user_policy_attachment.sqs`

### Limpeza

Após os testes, foi executado:

```bash
terraform destroy
```

O destroy foi concluído com sucesso.

Não há infraestrutura do laboratório de SQS mantida após a conclusão dos testes.

---

## 14. Padrão de explicação desejado

O aluno prefere:

* explicações diretas;
* passo a passo;
* linguagem clara;
* exemplos práticos;
* entender o "porquê" antes de executar;
* progressão de dificuldade;
* evitar listas excessivamente longas;
* evitar respostas genéricas;
* relacionar conceitos com situações reais;
* destacar pegadinhas da CLF-C02.

Não simplificar excessivamente conceitos importantes apenas para tornar a explicação curta.

---

## 15. Continuidade em uma nova conversa

Se esta conversa ficar muito longa, uma nova conversa pode ser iniciada usando este arquivo e o `docs/learning-log.md` como contexto.

Mensagem recomendada:

> Estou continuando meu projeto `aws-cloud-practitioner-lab` em uma nova conversa.
>
> Leia o `docs/study-context.md` e o `docs/learning-log.md` do projeto para recuperar o contexto do curso.
>
> Mantenha exatamente a metodologia definida no `study-context.md`.
>
> O último módulo concluído é o **Day 7 — RDS**.
>
> Quero continuar pelo próximo módulo, **Day 8 — ECR**.
>
> Não repita os módulos anteriores. Comece pelas perguntas de diagnóstico e siga a sequência:
>
> 1. perguntas;
> 2. correção e explicação;
> 3. conteúdo do dia;
> 4. hands-on;
> 5. anotações/README;
> 6. atualização do `docs/learning-log.md`;
> 7. atualização da tabela de Labs;
> 8. conclusão e indicação do próximo dia.
>
> Priorize segurança de custos, aprendizado real e explicações passo a passo.
>
> **Não tente inferir padrões. Se houver alguma dúvida sobre como os módulos anteriores foram conduzidos, pergunte antes de prosseguir.**

---

## 16. Estado atual das credenciais e IAM do laboratório

### IAM User utilizado pelo laboratório

O usuário IAM utilizado para executar comandos AWS CLI e Terraform é:

```text
aws-cloud-practitioner-lab
```

O Terraform utiliza as credenciais configuradas localmente no AWS CLI para esse usuário.

As permissões disponíveis são as permissões desse usuário e não as permissões do Root User ou de outra identidade IAM.

### Policies específicas dos módulos

O laboratório utiliza policies específicas conforme cada módulo exige.

Atualmente foram utilizadas, entre outras:

```text
aws-cloud-practitioner-lab-read-identity
aws-cloud-practitioner-lab-vpc
aws-cloud-practitioner-lab-ec2
aws-cloud-practitioner-lab-s3
aws-cloud-practitioner-lab-rds
```

O princípio adotado é adicionar somente as permissões necessárias para o exercício.

### Regra para os próximos hands-on

Antes de executar um `terraform apply` que crie ou modifique recursos AWS:

```text
identificar recurso
      ↓
identificar permissões necessárias
      ↓
configurar Least Privilege
      ↓
terraform plan
      ↓
terraform apply
      ↓
testar
      ↓
documentar
      ↓
terraform destroy
```

Não assumir que o Terraform possui permissões suficientes somente porque consegue executar `plan` ou consultar a identidade.

### IAM Role criada no Day 3

Existe a Role:

```text
aws-cloud-practitioner-lab-ec2-s3-read
```

Ela possui:

```text
Trust Policy:
EC2 → pode assumir a Role

Permissions Policy:
AmazonS3ReadOnlyAccess
```

Essa Role não concede permissões ao usuário `aws-cloud-practitioner-lab`.

Ela será utilizada para demonstrar credenciais temporárias em workloads AWS.

### Regra de segurança

Nunca colocar Access Key ou Secret Access Key diretamente no Terraform, código ou Git.

---

## 17. Padrão de commits e histórico do projeto

### Padrão de mensagens

```text
tipo: descrição curta em inglês
```

Tipos utilizados:

* `docs:`
* `feat:`

As mensagens devem ser:

* curtas;
* objetivas;
* em inglês;
* iniciadas por um tipo semântico;
* descrever claramente o que foi alterado.

### Histórico registrado anteriormente

```text
e47533e (HEAD -> main) docs: update AWS lab IAM context
59f8c18 docs: add study context and methodology
557aa06 (origin/main, origin/HEAD) docs: complete iam study and lab
5beb710 feat: add iam terraform lab
6016ae0 docs: complete global infrastructure study
037e808 docs: mark cloud concepts as completed
f9266f4 docs: add cloud concepts study notes
c9f6694 docs: update project README
0907a44 Initial commit
```

Antes de iniciar um novo commit:

```bash
git status
git log --oneline --decorate -n 10
```

Evitar commits duplicados quando uma alteração já tiver sido registrada.

### Commits do Day 7

O Day 7 envolve duas categorias de alteração:

```text
feat:
→ infraestrutura Terraform do RDS

docs:
→ README, learning log e contexto
```

Os commits exatos devem ser definidos após verificar o estado atual do Git, para evitar duplicação.

---

## 18. Estado atual do curso

### Estado dos módulos

```text
Day 1  → Cloud Concepts          ✅
Day 2  → Global Infrastructure   ✅
Day 3  → IAM                     ✅
Day 4  → VPC                     ✅
Day 5  → EC2                     ✅
Day 6  → S3                      ✅
Day 7  → RDS                     ✅
Day 8  → ECR                     ✅
Day 9  → ECS                     ✅
Day 10 → SQS                     ✅
Day 11 → CloudWatch              ⬜ Próximo
Day 12 → Auto Scaling            ⬜
Day 13 → Final Project           ⬜
```

---

## Continuidade

O próximo módulo deve ser:

**Day 11 — CloudWatch**

A metodologia continua:

```text
diagnóstico
    ↓
correção
    ↓
explicação
    ↓
entender
    ↓
criar
    ↓
testar
    ↓
observar
    ↓
documentar
    ↓
destruir
```

O próximo dia deve começar pelo **diagnóstico de CloudWatch**, sem repetir SQS ou os módulos anteriores.
