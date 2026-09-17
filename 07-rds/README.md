# 07 — RDS (Relational Database Service)

## Objetivo

Estudar o Amazon RDS como serviço de banco de dados relacional gerenciado da AWS, entendendo:

* o que é RDS e qual problema ele resolve;
* RDS versus banco de dados em EC2;
* responsabilidades da AWS e do cliente;
* Multi-AZ;
* Read Replica;
* backups e recuperação;
* DB Subnet Groups;
* Security Groups;
* integração do RDS com uma VPC;
* uso de subnets privadas;
* permissões IAM necessárias para o laboratório;
* criação e validação do RDS utilizando Terraform.

O laboratório utiliza uma instância MySQL privada dentro da VPC criada nos módulos anteriores.

---

# 1. Diagnóstico inicial

Antes de iniciar a explicação, foi realizado um diagnóstico sem pesquisa para verificar o conhecimento prévio sobre RDS.

## Perguntas de diagnóstico

### 1. O que é o Amazon RDS e qual problema ele resolve?

**Minha resposta:**

> RDS é um serviço de armazenamento da AWS que egue a arquitetura de banco de dados relacional

**Correção:**

🟡 **Parcialmente correta.**

Foi acertada a parte de banco de dados relacional, mas chamar RDS de "serviço de armazenamento" pode levar a uma associação errada com S3 ou EBS.

A definição principal é:

> **Amazon RDS é um serviço gerenciado de bancos de dados relacionais.**

O RDS oferece mecanismos para executar bancos relacionais como MySQL, PostgreSQL, MariaDB, Oracle e SQL Server, enquanto a AWS administra várias tarefas de infraestrutura.

Uma distinção importante:

```text
S3  → Object Storage

EBS → Block Storage

RDS → Relational Database Service
```

O RDS naturalmente utiliza armazenamento, mas seu propósito não é ser um serviço genérico de armazenamento.

---

### 2. Qual destas opções descreve melhor o RDS?

A. Um serviço de armazenamento de objetos

B. Um serviço gerenciado de bancos de dados relacionais

C. Um serviço de execução de containers

D. Um serviço de armazenamento de arquivos

**Minha resposta:**

> B

**Correção:**

🟢 **Correta.**

**RDS = serviço gerenciado de bancos de dados relacionais.**

A palavra **gerenciado** é importante porque representa uma das principais vantagens do serviço.

---

### 3. Qual é a principal diferença entre utilizar um banco de dados em uma EC2 e utilizar o RDS?

**Minha resposta:**

> O RDS oferece muitas facilidades de gerenciamento que não precisam ser feitas manualmente na EC2 como atualização de versões, cache, etc

**Correção:**

🟢 **Boa ideia, com uma correção importante.**

Foi identificado corretamente o principal benefício: **redução da carga operacional**.

Dependendo da configuração, a AWS pode cuidar de tarefas como:

* provisionamento da infraestrutura;
* backups automatizados;
* patching e manutenção;
* substituição de hardware;
* mecanismos de recuperação;
* configuração de alta disponibilidade.

Porém, **cache não deve ser associado diretamente ao RDS como a principal diferença entre RDS e banco em EC2**.

O modelo mental é:

```text
Banco em EC2

    ↓

Cliente administra muito mais coisas


RDS

    ↓

AWS gerencia grande parte da infraestrutura

    ↓

Cliente concentra-se mais no banco e na aplicação
```

Isso é um exemplo do benefício de utilizar um **managed service**.

---

### 4. Cite pelo menos dois mecanismos de alta disponibilidade ou recuperação que você acredita que o RDS oferece.

**Minha resposta:**

> Cache e sharding

**Correção:**

🔴 **Aqui houve uma confusão importante.**

Nem **cache** nem **sharding** são mecanismos que devemos utilizar como resposta para alta disponibilidade ou recuperação do RDS.

**Cache** serve principalmente para reduzir consultas ao banco e melhorar desempenho.

**Sharding** consiste em distribuir dados entre diferentes partições ou servidores para escalar determinados workloads.

Para o estudo de RDS, os mecanismos importantes são:

```text
Multi-AZ

Backups

Snapshots

Automated Backups

Point-in-Time Recovery
```

Principal distinção:

> **Multi-AZ → alta disponibilidade**

> **Backup/Snapshot → recuperação**

---

### 5. O que você entende por Multi-AZ no RDS?

Ela serve principalmente para aumentar capacidade de leitura ou disponibilidade?

**Minha resposta:**

> É um mecanismo de confiabilidade que ajuda a aumentar a resiliência aumentando a disponibilidade

**Correção:**

🟢 **Conceitualmente correta.**

Foi corretamente associado **Multi-AZ → disponibilidade/resiliência**.

De forma simplificada:

```text
Multi-AZ

Instância principal
        ↓
Standby em outra AZ
        ↓
Maior disponibilidade
        ↓
Failover automático
```

Uma pegadinha importante:

> **Multi-AZ não existe principalmente para aumentar capacidade de leitura.**

Esse papel está associado às **Read Replicas**.

---

### 6. Imagine uma aplicação que recebe muitas consultas SELECT, mas poucas operações de escrita. O que poderia ser utilizado no RDS para distribuir as leituras?

**Minha resposta:**

> Um sharding com uma cópia de leitura da base original

**Correção:**

🟡 **Chegou perto do conceito correto, mas misturou dois mecanismos.**

Para esse cenário, a resposta esperada é:

> **Read Replica**

Uma arquitetura simplificada:

```text
                 ┌── Read Replica
                 │
Application ──→ RDS Primary
                 │
                 └── Read Replica
```

As consultas de leitura podem ser direcionadas para as réplicas.

**Sharding** é outra técnica e não é a resposta esperada para essa questão.

---

### 7. Qual é a diferença entre Multi-AZ e Read Replica?

**Minha resposta:**

> Não sei

**Correção:**

🟢 **Ótimo diagnóstico.**

Esse era justamente um conceito que precisava ser consolidado.

|                    | Multi-AZ                   | Read Replica                    |
| ------------------ | -------------------------- | ------------------------------- |
| Principal objetivo | **Alta disponibilidade**   | **Escalar leituras**            |
| Foco               | Resiliência/failover       | Performance de leitura          |
| Relação            | Standby                    | Réplica utilizada para leituras |
| Pergunta mental    | "E se a principal falhar?" | "Tenho muitas leituras?"        |

Forma simples de memorizar:

```text
Multi-AZ
→ disponibilidade

Read Replica
→ leitura
```

---

### 8. O que acontece com o banco de dados se uma instância RDS apresentar uma falha de hardware?

**Minha resposta:**

> É muito difícil chegar nesse cenário mas ela será tratada pela AWS, não necessitando de ação manual por parte de quem está utilizando o serviço

**Correção:**

🟢 **Boa compreensão do conceito de serviço gerenciado.**

É importante apenas tomar cuidado com a afirmação de que nunca haverá ação manual.

Em uma configuração apropriada de **Multi-AZ**, o RDS pode realizar **failover automático** para a instância standby.

Simplificando:

```text
Falha
  ↓
RDS detecta
  ↓
Failover
  ↓
Standby assume
```

Isso reduz significativamente a necessidade de intervenção manual.

---

### 9. Quem é responsável pelo sistema operacional de uma instância RDS?

A. O cliente precisa acessar e administrar o sistema operacional

B. A AWS gerencia essa camada

C. O Terraform gerencia essa camada

D. O IAM gerencia essa camada

**Minha resposta:**

> B

**Correção:**

🟢 **Correta.**

Uma das principais vantagens do RDS é não precisarmos administrar o sistema operacional da mesma maneira que administraríamos uma instância EC2.

Isso se relaciona diretamente ao conceito de **managed service**.

---

### 10. Quais destes você considera responsabilidade da AWS ao utilizar RDS?

A. Manutenção da infraestrutura física

B. Gerenciamento do sistema operacional

C. Definição das tabelas do banco

D. Aplicação das regras de negócio

E. Patching da infraestrutura gerenciada

**Minha resposta:**

> A, B e E

**Correção:**

🟢 **Correta.**

Para o modelo estudado:

* **A — infraestrutura física:** AWS
* **B — sistema operacional:** AWS
* **E — manutenção/patching da infraestrutura gerenciada:** AWS

Enquanto:

* **C — tabelas:** responsabilidade do cliente
* **D — regras de negócio:** responsabilidade da aplicação/cliente

Isso se relaciona ao **Shared Responsibility Model**.

---

### 11. Um banco RDS está em uma subnet privada. Isso significa que a aplicação não consegue acessá-lo?

Explique o que você acha que precisa existir para permitir essa comunicação.

**Minha resposta:**

> Significa que ela não pode ser acessada publicamente pela Internet
>
> Para acessar o RDS precisa ter a role com a policy e com as permissões corretas

**Correção:**

🟡 **Primeira parte correta; segunda parte não.**

Uma subnet privada significa que o recurso não possui acesso direto à Internet através de um Internet Gateway.

Porém, o acesso **da aplicação para o RDS não é controlado por IAM Role da maneira descrita na resposta**.

Aqui estamos falando principalmente de **rede**:

```text
Application

    ↓

Network

    ↓

Security Group

    ↓

RDS
```

Por exemplo:

```text
EC2 Security Group
        ↓
     TCP 3306
        ↓
RDS Security Group
```

Se o banco for MySQL, o Security Group do RDS pode permitir conexões na porta `3306` provenientes do Security Group da aplicação.

A distinção é:

```text
IAM

→ "Essa identidade pode executar uma ação AWS?"


Security Group

→ "Esse tráfego de rede pode passar?"
```

Essa diferença conecta diretamente o Day 7 aos conceitos estudados no Day 5.

---

### 12. Pensando no nosso laboratório com a VPC do Day 4, onde você colocaria um banco RDS em uma arquitetura como esta?

```text
Internet

   ↓

ALB

   ↓

EC2 / ECS

   ↓

RDS
```

Você colocaria o RDS em subnet pública ou privada? Por quê?

**Minha resposta:**

> Numa subnet pública porque dificilmente um banco de dados precisará ser acessado publicamente pela Internet

**Correção:**

🔴 **A conclusão contradiz o próprio raciocínio apresentado.**

A observação de que dificilmente um banco precisa ser acessado publicamente está correta.

Justamente por isso, **o RDS normalmente deve ficar em subnets privadas**.

Uma arquitetura típica:

```text
                 Internet
                    ↓
                   ALB
                    ↓
              Application
             /           \
        AZ A              AZ B
         ↓                  ↓
    Private Subnet     Private Subnet
         \                  /
          └────── RDS ─────┘
```

Conexão com o Day 4:

```text
Public Subnet
→ rota para Internet Gateway

Private Subnet
→ sem rota direta para Internet Gateway

RDS
→ normalmente em Private Subnets
→ acesso permitido pela rede da aplicação
→ não precisa ser publicamente acessível
```

Um ponto importante:

> **"O RDS está em uma subnet privada" não significa "o RDS não pode ser acessado pela aplicação".**

Uma aplicação dentro da VPC pode acessar o RDS pela rede privada.

---

# 2. Resultado do diagnóstico

O diagnóstico mostrou uma boa base sobre:

* conceito de serviço gerenciado;
* responsabilidades da AWS;
* disponibilidade;
* subnets privadas;
* arquitetura geral.

Os principais pontos que precisavam ser trabalhados foram:

```text
Multi-AZ
    ×
Read Replica

Backup
    ×
High Availability

Security Group
    ×
IAM

Public Subnet
    ×
Private Subnet
```

Principal associação para memorizar:

```text
"Preciso continuar funcionando se houver uma falha?"
                    ↓
                 Multi-AZ


"Tenho muitas consultas de leitura?"
                    ↓
              Read Replica


"Preciso recuperar dados?"
                    ↓
          Backup / Snapshot


"Quem pode acessar o banco pela rede?"
                    ↓
            Security Group
```

---

# 3. Conceitos estudados

## Amazon RDS

O Amazon Relational Database Service (RDS) é um serviço gerenciado para bancos de dados relacionais.

Entre os engines suportados estão:

* MySQL;
* PostgreSQL;
* MariaDB;
* Oracle;
* SQL Server.

A AWS gerencia grande parte da infraestrutura necessária para executar o banco, permitindo que o cliente se concentre mais nos dados, schema, usuários, consultas e aplicação.

---

## RDS × EC2

Ao instalar um banco de dados diretamente em uma EC2, o cliente possui maior responsabilidade sobre a infraestrutura e o sistema operacional.

Com RDS, a AWS assume diversas responsabilidades de gerenciamento.

```text
EC2 + Database

Cliente
├── EC2
├── SO
├── Banco
├── Patches
├── Manutenção
└── Configuração


RDS

AWS
├── Infraestrutura
├── SO gerenciado
└── Diversas tarefas operacionais

Cliente
├── Banco
├── Dados
├── Schema
├── Usuários
└── Aplicação
```

---

## Multi-AZ

Multi-AZ é utilizado para aumentar a disponibilidade e fornecer mecanismos de failover.

A ideia principal é:

```text
Primary
   ↓
Standby em outra AZ
   ↓
Failover
```

O objetivo principal não é distribuir consultas de leitura.

---

## Read Replica

Read Replica permite criar réplicas utilizadas para distribuir operações de leitura.

A associação mental é:

```text
Multi-AZ
→ disponibilidade

Read Replica
→ leitura
```

---

## Backup e recuperação

Backups e snapshots possuem finalidade de recuperação.

Eles devem ser diferenciados de mecanismos de alta disponibilidade.

```text
Alta disponibilidade
→ Multi-AZ

Recuperação
→ Backup / Snapshot / Point-in-Time Recovery
```

---

## DB Subnet Group

Um DB Subnet Group define as subnets que podem ser utilizadas pelo RDS dentro da VPC.

Neste laboratório foram utilizadas duas subnets privadas:

```text
private-a
10.0.11.0/24

private-b
10.0.12.0/24
```

As duas estão em Availability Zones diferentes.

---

## Publicly Accessible

Neste laboratório:

```text
publicly_accessible = false
```

O RDS não é configurado para acesso público.

Isso está alinhado à arquitetura em que o banco fica na camada privada da aplicação.

---

## Security Group

O Security Group funciona como firewall virtual associado à instância RDS.

Neste laboratório, ele foi criado inicialmente sem regras de entrada ou saída:

```text
Ingress: []
Egress: []
```

Isso significa que nenhuma comunicação de rede foi liberada para o banco nessa etapa.

Posteriormente, em uma arquitetura com aplicação, poderia ser permitido algo como:

```text
Application Security Group
        ↓
      TCP 3306
        ↓
RDS Security Group
```

---

# 4. Arquitetura do laboratório

```text
VPC
10.0.0.0/16
│
├── private-a
│   10.0.11.0/24
│
└── private-b
    10.0.12.0/24
         │
         ▼
   DB Subnet Group
         │
         ▼
   Amazon RDS
   MySQL 8.0
   db.t4g.micro
   Single-AZ
   Private
         │
         ▼
   RDS Security Group
   sem regras de ingress/egress
```

---

# 5. Terraform

O laboratório utiliza Terraform para criar:

* DB Subnet Group;
* Security Group do RDS;
* instância RDS MySQL;
* IAM Policy específica para o laboratório;
* attachment da policy ao usuário `aws-cloud-practitioner-lab`.

A VPC e as subnets existentes são descobertas por tags utilizando `data sources`.

Não são utilizados IDs de VPC ou subnet fixados manualmente no código.

---

## Senha do banco

A senha é recebida através de uma variável sensível:

```bash
export TF_VAR_db_password='SUA_SENHA'
```

Após o uso:

```bash
unset TF_VAR_db_password
```

A senha não deve ser armazenada no Git.

O Terraform State pode conter informações sensíveis do recurso, portanto os arquivos de state permanecem ignorados pelo Git.

---

# 6. IAM e Service-Linked Role

Durante o primeiro `terraform apply`, a criação do RDS falhou porque o usuário do laboratório não possuía permissão para criar o Service-Linked Role utilizado pelo RDS.

O erro indicava a necessidade da permissão:

```text
iam:CreateServiceLinkedRole
```

A policy foi então ajustada para permitir essa ação de forma restrita ao Service-Linked Role do RDS e condicionada ao serviço:

```text
rds.amazonaws.com
```

Após a alteração, o `terraform apply` foi concluído com sucesso.

O Service-Linked Role utilizado pelo RDS é:

```text
AWSServiceRoleForRDS
```

Essa situação foi utilizada como exemplo prático de **least privilege**.

Em vez de restaurar `AdministratorAccess`, foi adicionada somente a permissão necessária para a operação.

---

# 7. Resultado do laboratório

A instância RDS criada apresentou:

| Propriedade         | Resultado                        |
| ------------------- | -------------------------------- |
| Identifier          | `aws-cloud-practitioner-lab-rds` |
| Status              | `available`                      |
| Engine              | MySQL                            |
| Engine Version      | `8.0.46`                         |
| Instance Class      | `db.t4g.micro`                   |
| Storage             | 20 GB gp3                        |
| Encryption          | habilitada                       |
| VPC                 | `vpc-09902363bc6acf897`          |
| Subnets             | `private-a`, `private-b`         |
| Publicly Accessible | `false`                          |
| Multi-AZ            | `false`                          |
| Port                | `3306`                           |
| Security Group      | `aws-cloud-practitioner-lab-rds` |

---

# 8. Validação

A instância foi validada utilizando AWS CLI.

Resultado observado:

```text
Status: available
Engine: mysql
EngineVersion: 8.0.46
InstanceClass: db.t4g.micro
PubliclyAccessible: false
MultiAZ: false
Port: 3306
```

Também foi confirmado que:

* o RDS está na VPC correta;
* o RDS utiliza as duas subnets privadas;
* o RDS utiliza o Security Group criado especificamente para o laboratório;
* o Security Group não possui regras de ingress ou egress.

O Security Group apresentou:

```text
Ingress: []
Egress: []
```

Isso é intencional nesta etapa. O banco foi criado sem liberar acesso de rede.

---

# 9. Controle de custos

RDS é um recurso que pode gerar cobrança enquanto permanece provisionado.

Durante o laboratório, a instância permaneceu criada por mais tempo do que o planejado. Por esse motivo, o recurso foi destruído após a validação.

Essa situação reforçou a regra de controle de custos do projeto:

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

Após concluir o exercício:

```bash
terraform destroy
```

Também deve ser verificado se os recursos foram realmente removidos.

---

# 10. CLF-C02 — pontos de atenção

* RDS é um **serviço gerenciado de bancos de dados relacionais**.
* RDS não é um serviço genérico de armazenamento como S3.
* RDS reduz a carga operacional quando comparado à administração de um banco em EC2.
* **Multi-AZ → alta disponibilidade/failover.**
* **Read Replica → escala de leitura.**
* Backups e snapshots estão relacionados à recuperação.
* Security Groups controlam **tráfego de rede**.
* IAM controla **permissões para ações na AWS**.
* `PubliclyAccessible = false` não impede aplicações dentro da VPC de acessar o banco.
* MySQL utiliza normalmente a porta `3306`.
* DB Subnet Group associa o RDS às subnets disponíveis para o banco.
* RDS normalmente é colocado em **subnets privadas**.
* O sistema operacional da instância RDS é gerenciado pela AWS.
* Multi-AZ e Read Replica possuem objetivos diferentes.

---

# 11. Cleanup

O recurso RDS é destruído após o laboratório para evitar custos desnecessários:

```bash
terraform destroy
```

A senha utilizada no ambiente também deve ser removida da variável de ambiente:

```bash
unset TF_VAR_db_password
```

---

# 12. Conclusão

O Day 7 permitiu estudar o RDS tanto conceitualmente quanto na prática, conectando o serviço aos conceitos de VPC, subnets, Security Groups e IAM estudados anteriormente.

O laboratório também reforçou uma distinção fundamental para a CLF-C02:

```text
Multi-AZ
→ disponibilidade

Read Replica
→ leitura

Backup / Snapshot
→ recuperação

Security Group
→ rede

IAM
→ autorização para ações AWS
```

Além disso, o exercício demonstrou na prática como uma política de **least privilege** pode precisar ser ajustada quando uma operação AWS depende de uma Service-Linked Role.

**Status:** concluído.
