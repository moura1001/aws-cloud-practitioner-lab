# Learning Log

Registro de evolução dos estudos para a certificação **AWS Certified Cloud Practitioner (CLF-C02)**.

---

# Day 01 — Cloud Concepts

**Status:** Concluído
**Resultado:** Bom domínio dos conceitos fundamentais, com pequenos ajustes de terminologia.

## Conceitos estudados

* Cloud Computing
* CapEx e OpEx
* Pay-as-you-go
* Scalability
* Elasticity
* High Availability
* Fault Tolerance
* Region
* Availability Zone
* Edge Location

## Principais aprendizados

### 1. Scalability x Elasticity

```text
Scalability
    ↓
Capacidade de aumentar ou diminuir recursos

Elasticity
    ↓
Ajuste dinâmico dos recursos conforme a demanda
```

**Palavra-chave:**
Elasticity → **dinamicamente**

---

### 2. High Availability x Fault Tolerance

```text
High Availability
    ↓
Minimizar / evitar indisponibilidade

Fault Tolerance
    ↓
Continuar funcionando mesmo diante de falhas
```

Os conceitos são relacionados, mas não são sinônimos.

---

### 3. Region x Availability Zone

```text
AWS Global Infrastructure
│
├── Region
│   │
│   ├── Availability Zone
│   ├── Availability Zone
│   └── Availability Zone
│
└── Edge Locations
```

**Region:** área geográfica da AWS.

**Availability Zone:** localização isolada dentro de uma Region.

**Edge Location:** ponto de presença próximo aos usuários, utilizado por serviços como o CloudFront.

---

## As 5 relações que preciso memorizar

### 1. Region → Availability Zone

Uma **Region contém múltiplas Availability Zones**.

```text
Region
├── AZ
├── AZ
└── AZ
```

---

### 2. Availability Zone → High Availability

Distribuir recursos entre diferentes AZs ajuda a evitar que uma falha localizada provoque a indisponibilidade completa da aplicação.

```text
AZ A → EC2
AZ B → EC2

       ↓
Maior disponibilidade
```

---

### 3. Edge Location → CloudFront → Menor latência

O CloudFront utiliza a infraestrutura de Edge Locations para aproximar o conteúdo dos usuários.

```text
Usuário
   ↓
Edge Location
   ↓
CloudFront
   ↓
Origem
```

---

### 4. Scalability → Elasticity

Scalability representa a capacidade de aumentar ou diminuir a capacidade.

Elasticity representa o ajuste dessa capacidade de forma dinâmica conforme a demanda.

```text
Scalability
    ↓
Pode aumentar/diminuir

Elasticity
    ↓
Aumenta/diminui conforme a demanda
```

---

### 5. High Availability → múltiplas AZs

Utilizar múltiplas AZs é uma estratégia para aumentar a disponibilidade e reduzir pontos únicos de falha.

```text
              ┌── AZ A → EC2
Usuários → LB ┤
              └── AZ B → EC2
```

---

## Erros/correções do Day 01

### Region não é simplesmente um país

A definição correta é **área geográfica da AWS**.

### Edge Location não é apenas cache

Edge Location é um **ponto de presença**. O cache é uma das utilizações, especialmente pelo CloudFront.

### Elasticity e Scalability não são sinônimos

Ambos estão relacionados à capacidade de ajustar recursos, mas **Elasticity enfatiza o ajuste dinâmico conforme a demanda**.

---

## Autoavaliação

**Resultado aproximado:** 89%

O conhecimento conceitual foi considerado bom. Os principais pontos para reforçar são:

* Region ≠ país;
* Edge Location ≠ somente cache;
* Elasticity → ajuste dinâmico;
* High Availability ≠ simplesmente "ter várias máquinas".

---

## Regra prática do Day 01

Ao analisar uma arquitetura AWS, perguntar:

1. **Onde?** → Region
2. **Em quais locais isolados?** → Availability Zones
3. **Como reduzir a latência para usuários?** → Edge Locations / CloudFront
4. **Como aumentar ou diminuir capacidade?** → Scalability
5. **Como ajustar essa capacidade conforme a demanda?** → Elasticity
6. **Como evitar indisponibilidade?** → High Availability
7. **Como continuar funcionando diante de falhas?** → Fault Tolerance

---

## Próximos passos

* [x] Consolidar os conceitos do Day 01
* [x] Registrar as anotações no `01-cloud-concepts/README.md`
* [x] Fazer commit e push
* [x] Iniciar Day 02

---

# Day 02 — AWS Global Infrastructure & Well-Architected Framework

**Status:** Concluído
**Resultado:** Excelente domínio conceitual, com pequenos ajustes de terminologia.

## Conceitos estudados

* AWS Region
* Availability Zone
* Edge Location
* AWS Global Infrastructure
* AWS Well-Architected Framework
* Operational Excellence
* Security
* Reliability
* Performance Efficiency
* Cost Optimization
* Sustainability

## Principais relações aprendidas

```text
Region
    ↓
Availability Zones
    ↓
Maior isolamento contra falhas
    ↓
Maior disponibilidade / resiliência
```

```text
Edge Location
    ↓
CloudFront
    ↓
Conteúdo mais próximo do usuário
    ↓
Menor latência
```

```text
Scalability
    ↓
Aumentar / diminuir capacidade

Elasticity
    ↓
Ajustar dinamicamente conforme a demanda
```

## Relação entre os pilares

```text
Operational Excellence
    → Como operar e melhorar?

Security
    → Como proteger?

Reliability
    → Como resistir e se recuperar de falhas?

Performance Efficiency
    → Como utilizar os recursos de forma eficiente?

Cost Optimization
    → Como evitar gastos desnecessários?

Sustainability
    → Como reduzir o impacto ambiental?
```

## Pontos que precisei corrigir

### Region não é um país

Region deve ser entendida como uma **área geográfica da AWS** que contém múltiplas Availability Zones.

### Reliability é o nome do pilar

Resiliência e tolerância a falhas são conceitos associados à Reliability, mas **não são nomes de pilares**.

### Performance Efficiency

O nome completo do pilar é **Performance Efficiency**, e não apenas "Performance".

### Security não trata apenas de controle de acesso

Security também envolve **auditoria, rastreabilidade, proteção de dados e identidade**.

Exemplo:

```text
Quem pode fazer?
    → Controle de acesso

Quem fez?
    → Auditoria / rastreabilidade
```

### Performance Efficiency × Reliability

```text
Sistema lento / recurso inadequado
    → Performance Efficiency

Sistema falhando / precisa se recuperar
    → Reliability
```

## Autoavaliação

**Resultado:** 9/10

Consegui identificar corretamente os seis pilares nas situações apresentadas.

Os principais ajustes foram relacionados à precisão da terminologia e à distinção entre conceitos próximos:

* Reliability × Resilience;
* Performance Efficiency × Reliability;
* Security × controle de acesso/auditoria.

## Regra prática

Ao analisar uma questão do Well-Architected Framework:

> **Primeiro identificar o problema que a arquitetura está tentando resolver; depois associá-lo ao pilar.**

Não começar procurando uma tecnologia AWS específica.

## Próximo passo

* [x] Estudar Global Infrastructure
* [x] Estudar Well-Architected Framework
* [x] Responder às questões conceituais
* [x] Registrar correções
* [x] Iniciar próximo módulo

---

## Day 3 — IAM

### Conteúdos estudados

* Authentication vs Authorization
* IAM User
* IAM Policy
* Least Privilege
* Root User
* MFA
* Access Keys
* IAM Roles
* Trust Policy
* Permissions Policy
* Credenciais temporárias
* Integração entre AWS CLI, Terraform e IAM

### Principais aprendizados

**Authentication** responde à pergunta: **“Quem é você?”**

**Authorization** responde à pergunta: **“O que você pode fazer?”**

Um **IAM User** representa uma identidade persistente. Já uma **IAM Role** é uma identidade que pode ser assumida e normalmente fornece credenciais temporárias.

Uma IAM Policy define quais ações uma identidade pode executar sobre determinados recursos.

O princípio de **Least Privilege** consiste em conceder somente as permissões necessárias para realizar determinada tarefa.

O **Root User** possui privilégios extremamente amplos e não deve ser utilizado para tarefas rotineiras. MFA deve estar habilitado e Access Keys do Root devem ser evitadas.

### Hands-on realizado

Foi criado o IAM User:

`aws-cloud-practitioner-lab`

O usuário inicialmente recebeu `AdministratorAccess` para permitir a configuração inicial do laboratório. Depois, essa permissão foi removida para demonstrar o princípio de Least Privilege.

Foi criada uma política personalizada permitindo somente:

```text
sts:GetCallerIdentity
```

Com isso, o Terraform conseguiu consultar a identidade e a conta atual, mas não conseguiu executar operações que exigiam outras permissões.

Um teste com `iam:GetUser` retornou `AccessDenied`, demonstrando na prática que uma identidade não pode executar uma ação que não esteja autorizada por sua policy.

Também foi criada a Role:

`aws-cloud-practitioner-lab-ec2-s3-read`

A Role possui:

* **Trust Policy:** permite que o serviço EC2 assuma a Role.
* **Permissions Policy:** permite leitura no Amazon S3 através da `AmazonS3ReadOnlyAccess`.

### Conceito importante sobre Roles

Uma Role é especialmente útil para aplicações executadas em serviços AWS porque evita colocar Access Keys diretamente no código ou na configuração da aplicação.

Quando uma EC2 assume uma Role, ela recebe **credenciais temporárias**, que possuem prazo de validade e são renovadas automaticamente. Isso reduz o risco associado ao vazamento de credenciais permanentes.

### Terraform

O Terraform foi utilizado para consultar:

* Identidade AWS atual;
* Account ID;
* Região atual.

Também foi utilizado para validar o comportamento das permissões do IAM.

Nenhum recurso de infraestrutura com cobrança foi criado neste laboratório.

### Resultado

* [x] Compreendi Authentication vs Authorization
* [x] Compreendi IAM User
* [x] Compreendi IAM Policy
* [x] Pratiquei Least Privilege
* [x] Compreendi Root User e MFA
* [x] Configurei AWS CLI
* [x] Utilizei Terraform com IAM
* [x] Testei uma permissão negada (`AccessDenied`)
* [x] Compreendi IAM Roles
* [x] Compreendi Trust Policy vs Permissions Policy
* [x] Compreendi o uso de credenciais temporárias em Roles
* [x] Nenhum recurso pago foi criado

---

## Day 4 — VPC

### Objetivos

* Entender o conceito de VPC e CIDR.
* Diferenciar VPC, Subnet pública e Subnet privada.
* Entender como Route Tables e Internet Gateway controlam o tráfego.
* Criar uma VPC utilizando Terraform.
* Praticar Least Privilege utilizando uma política IAM específica para o laboratório.
* Validar que o laboratório funciona sem `AdministratorAccess`.

### Conceitos estudados

**VPC (Virtual Private Cloud)** é uma rede virtual logicamente isolada dentro da AWS, na qual podemos definir a organização e as regras de comunicação dos recursos.

Uma VPC pode abranger múltiplas Availability Zones, enquanto uma Subnet pertence a uma única Availability Zone.

**CIDR** define o intervalo de endereços IP da rede. Por exemplo:

* `10.0.0.0/16` → espaço de endereços maior.
* `10.0.1.0/24` → espaço de endereços menor.

**Subnet pública** possui uma Route Table com uma rota para um Internet Gateway.

**Subnet privada** não possui uma rota direta para um Internet Gateway. Ela pode utilizar outros componentes, como NAT Gateway, para permitir acesso de saída à Internet sem exposição direta para conexões de entrada.

**Route Table** define para onde o tráfego deve ser encaminhado.

**Internet Gateway (IGW)** permite a comunicação entre a VPC e a Internet quando existem as configurações de rota e endereço necessárias.

Ter uma VPC, Internet Gateway ou subnet pública não significa, por si só, que um recurso esteja automaticamente acessível pela Internet.

**Security Group** funciona como um firewall virtual associado aos recursos e controla o tráfego permitido.

### Infraestrutura criada

Foi criada uma VPC com:

* CIDR `10.0.0.0/16`
* DNS Support habilitado
* DNS Hostnames habilitado
* Região `sa-east-1`
* Tag `aws-cloud-practitioner-lab-vpc`

A VPC foi dividida entre duas Availability Zones:

```text
VPC 10.0.0.0/16
│
├── sa-east-1a
│   ├── public-a  → 10.0.1.0/24
│   └── private-a → 10.0.11.0/24
│
└── sa-east-1b
    ├── public-b  → 10.0.2.0/24
    └── private-b → 10.0.12.0/24
```

Também foram criados:

* 1 Internet Gateway
* 1 Route Table pública
* 1 Route Table privada
* 1 rota `0.0.0.0/0 → Internet Gateway`
* associações das duas subnets públicas à Route Table pública
* associações das duas subnets privadas à Route Table privada

A configuração foi validada utilizando tanto Terraform quanto AWS CLI.

### Public vs Private Subnet

Uma subnet é considerada pública quando sua Route Table possui uma rota para um Internet Gateway.

No laboratório:

```text
Public Route Table
└── 0.0.0.0/0 → Internet Gateway
```

A Route Table privada não possui essa rota, portanto as subnets privadas não possuem acesso direto à Internet através do IGW.

Uma subnet pública também não torna automaticamente todos os recursos acessíveis pela Internet. Para uma EC2 ser acessível externamente, ainda são necessários fatores como endereço IP público e regras apropriadas no Security Group.

### Alta disponibilidade

As subnets públicas e privadas foram distribuídas entre `sa-east-1a` e `sa-east-1b`.

Isso reforça o conceito de alta disponibilidade e resiliência: uma subnet pertence a uma única AZ, portanto recursos que precisam de maior disponibilidade podem ser distribuídos entre diferentes AZs.

### NAT Gateway

Não foi criado NAT Gateway.

O NAT Gateway permite que recursos em subnets privadas iniciem conexões com a Internet sem que esses recursos precisem ser diretamente expostos à Internet.

Entretanto, ele possui custo e não era necessário para os objetivos deste laboratório. Por isso, foi deliberadamente omitido para manter o exercício simples e evitar custos desnecessários.

### Least Privilege na prática

Inicialmente, o usuário `aws-cloud-practitioner-lab` possuía `AdministratorAccess` para permitir a execução dos laboratórios.

Para praticar Least Privilege, foi criada a política gerenciada:

`aws-cloud-practitioner-lab-vpc`

Ao remover `AdministratorAccess`, o Terraform revelou progressivamente permissões necessárias que não estavam inicialmente previstas.

Entre as permissões identificadas durante os testes estavam:

* `ec2:DescribeVpcAttribute`
* `ec2:DescribeAvailabilityZones`
* `ec2:CreateTags`
* `ec2:DescribeNetworkInterfaces`
* `ec2:ModifySubnetAttribute`
* `iam:GetPolicy`
* `iam:GetPolicyVersion`
* `iam:ListAttachedUserPolicies`

Após os ajustes, o comando:

```bash
terraform plan
```

funcionou corretamente sem `AdministratorAccess`.

O processo demonstrou na prática que ferramentas de infraestrutura como Terraform não precisam apenas das permissões de criação dos recursos. Elas também precisam consultar o estado atual dos recursos e das políticas durante operações como `refresh` e `plan`.

### Validação

A infraestrutura foi validada por meio de:

```bash
terraform validate
terraform plan
terraform apply
```

Também foram utilizados comandos da AWS CLI para confirmar:

* Availability Zones disponíveis
* subnets criadas
* CIDRs
* Availability Zones das subnets
* configuração de IP público
* Route Tables
* rotas configuradas

O `terraform plan` final foi executado com sucesso sem `AdministratorAccess`.

### Estado final do IAM

O usuário `aws-cloud-practitioner-lab` possui:

* `aws-cloud-practitioner-lab-read-identity`
* `aws-cloud-practitioner-lab-vpc`

O `AdministratorAccess` foi removido após a validação da política de Least Privilege.

### Custo

Não foi criado NAT Gateway, EC2 ou outro recurso que gere custo relevante neste laboratório.

A infraestrutura atual consiste principalmente em componentes de rede da VPC utilizados para fins educacionais.

A estratégia de segurança financeira continua sendo:

**entender → criar → testar → observar → documentar → destruir**

### Principais aprendizados do Day 4

* Uma VPC é uma rede virtual logicamente isolada na AWS.
* Uma VPC pode abranger várias Availability Zones.
* Uma subnet pertence a uma única Availability Zone.
* CIDR `/16` representa um espaço de endereços maior que `/24`.
* Uma subnet pública possui rota para um Internet Gateway.
* Uma subnet privada não possui rota direta para um Internet Gateway.
* Route Tables determinam os caminhos do tráfego.
* Internet Gateway fornece conectividade entre a VPC e a Internet.
* NAT Gateway pode fornecer acesso de saída à Internet para subnets privadas, mas possui custo.
* Uma subnet pública não torna automaticamente uma EC2 acessível pela Internet.
* Security Groups controlam o tráfego permitido para os recursos.
* Distribuir recursos entre AZs aumenta a resiliência da arquitetura.
* Terraform pode exigir permissões de leitura e gerenciamento além das ações diretamente relacionadas ao recurso.
* Least Privilege deve ser validado na prática, utilizando somente as permissões necessárias.

### Status

**Day 4 — VPC: concluído.**

---

# Day 5 — EC2

**Status:** Concluído

**Resultado:** Bom domínio dos conceitos fundamentais de EC2, com correções principalmente relacionadas a AMI, EBS, Security Group x IAM e ciclo de vida da instância.

## Conceitos estudados

* EC2
* AMI
* Instance Type
* EBS
* Instance Store
* Security Group
* IP privado e IP público
* Public Subnet
* Internet Gateway
* IAM Role
* Instance Profile
* Stop x Start
* Stop x Terminate
* `DeleteOnTermination`
* Terraform State
* `terraform apply -refresh-only`
* Least Privilege

## Principais aprendizados

### 1. EC2

EC2 fornece capacidade computacional sob demanda.

Uma instância pode ser configurada com diferentes sistemas operacionais, recursos computacionais, armazenamento e configurações de rede.

```text
EC2
 ├── Compute
 ├── Storage
 ├── Network
 └── IAM Role
```

### 2. AMI x Instance Type

```text
AMI
→ imagem/template utilizada para lançar a EC2

Instance Type
→ características computacionais da EC2
```

A AMI não é simplesmente o sistema operacional; ela é a imagem utilizada para criar a instância e pode incluir software e configurações.

### 3. EBS

EBS é armazenamento em bloco persistente utilizado pela EC2.

No laboratório foi utilizado:

```text
gp3
2 GiB
/dev/xvda
```

O volume raiz possuía:

```text
delete_on_termination = true
```

e foi excluído automaticamente quando a EC2 foi terminada.

### 4. Security Group x IAM

```text
Security Group
→ controle de tráfego de rede

IAM
→ controle de permissões AWS
```

Security Group não é um subconjunto do IAM.

Regra prática:

```text
SG → "Pode passar esse tráfego?"

IAM → "Essa identidade pode executar essa ação?"
```

### 5. IAM Role em EC2

A EC2 utilizou a Role criada no Day 3:

```text
aws-cloud-practitioner-lab-ec2-s3-read
```

através de um Instance Profile.

```text
EC2
 ↓
Instance Profile
 ↓
IAM Role
 ↓
credenciais temporárias
```

Isso evita a necessidade de armazenar Access Keys permanentes na instância.

## Hands-on realizado

Foi criada uma EC2 utilizando Terraform:

```text
Instance Type: t3.nano
AMI: Amazon Linux 2023 Minimal
AZ: sa-east-1a
Subnet: public-a
EBS: gp3 / 2 GiB
```

Foi criado um Security Group sem regras de entrada e configurado um Instance Profile utilizando a IAM Role existente.

Foram realizados experimentos de:

* Stop/Start;
* observação de IP privado e público;
* consulta do EBS;
* Terminate;
* exclusão automática do EBS;
* sincronização do Terraform State;
* Terraform Destroy.

## Resultado dos experimentos

Durante Stop/Start:

```text
Private IP → permaneceu 10.0.1.188
Public IP → foi liberado e recebeu novo endereço após Start
```

Durante Terminate:

```text
EC2 → terminated
EBS → excluído
```

A exclusão do EBS ocorreu devido a:

```text
delete_on_termination = true
```

## Terraform State

Após terminar a EC2 diretamente pela AWS CLI, o Terraform inicialmente ainda possuía a instância no state.

Foi utilizado:

```bash
terraform apply -refresh-only
```

para sincronizar o state com a infraestrutura real sem modificar recursos AWS.

Isso demonstrou a diferença entre:

```text
Terraform State
        ×
Infraestrutura real
```

## Least Privilege

Durante o `terraform destroy`, algumas operações IAM falharam inicialmente porque a policy do usuário não possuía:

```text
iam:DetachUserPolicy
iam:RemoveRoleFromInstanceProfile
```

Essas permissões foram adicionadas para permitir o cleanup.

Não foi adicionada:

```text
iam:DeletePolicy
```

para evitar conceder ao usuário a capacidade de excluir a própria policy do laboratório.

O problema demonstrou na prática que:

> As permissões necessárias para criar um recurso não são necessariamente as mesmas necessárias para removê-lo.

## Pegadinhas importantes para a CLF-C02

* AMI é uma imagem/template, não apenas um sistema operacional.
* Instance Type define recursos computacionais.
* EBS é armazenamento persistente em bloco.
* Instance Store é armazenamento temporário/efêmero.
* Security Group controla tráfego de rede.
* IAM controla permissões AWS.
* Subnet pública não significa que toda EC2 terá IP público.
* Stop permite iniciar a instância novamente.
* Terminate encerra a instância definitivamente.
* O comportamento do EBS após Terminate depende de `DeleteOnTermination`.
* IAM Role fornece credenciais temporárias e é preferível a Access Keys permanentes para workloads AWS.
* Uma EC2 acessível por SSH precisa de mais do que apenas uma rota para o Internet Gateway.

## Controle de custos

Foram evitados recursos desnecessários como:

* NAT Gateway;
* Elastic IP;
* infraestrutura adicional;
* regras de acesso SSH abertas.

A instância utilizou `t3.nano` e um EBS de apenas `2 GiB`.

Ao final, os recursos temporários do laboratório foram destruídos.

## Checklist

* [x] Estudar EC2
* [x] Estudar AMI
* [x] Estudar Instance Type
* [x] Estudar EBS
* [x] Estudar Security Group
* [x] Estudar IP público e privado
* [x] Utilizar IAM Role em EC2
* [x] Criar EC2 com Terraform
* [x] Testar Stop/Start
* [x] Observar alteração do IP público
* [x] Testar Terminate
* [x] Validar `DeleteOnTermination`
* [x] Praticar `terraform apply -refresh-only`
* [x] Praticar `terraform destroy`
* [x] Consolidar conceitos da CLF-C02
* [x] Limpar os recursos temporários

---

## Day 6 — S3 (Simple Storage Service)

### Objetivo

Estudar o Amazon S3 como serviço de Object Storage e praticar criação, configuração, acesso, versionamento e cleanup utilizando Terraform e AWS CLI.

### Conceitos estudados

* S3 como Object Storage;
* Bucket e Object;
* Object Key;
* Bucket names globalmente únicos;
* S3 × EBS × EFS;
* Storage Classes;
* Versioning;
* Delete Marker;
* IAM Policy para S3;
* Bucket permissions × Object permissions;
* Block Public Access;
* Least Privilege.

### Principais conceitos

**S3 → Object Storage**

```text
S3  → Object
EBS → Block
EFS → File
```

Bucket é o recipiente dos objetos e Object é o dado armazenado.

O S3 não é um banco de dados NoSQL e não utiliza diretórios tradicionais. Caminhos como `documentos/arquivo.pdf` são representados pela Key do objeto.

### Storage Classes

Storage Class define a categoria de armazenamento utilizada pelo objeto, considerando fatores como frequência de acesso e custo.

Exemplos estudados:

* S3 Standard;
* S3 Standard-IA;
* S3 Glacier.

### Versioning

Com Versioning desabilitado, enviar um objeto utilizando a mesma Key substitui o objeto atual.

Com Versioning habilitado, o S3 mantém múltiplas versões:

```text
example.txt
├── Version A
└── Version B
```

Também foi observado que um objeto criado antes da ativação do Versioning pode aparecer com:

```text
VersionId: null
```

### Delete Marker

Com Versioning habilitado, excluir um objeto cria normalmente um Delete Marker.

As versões anteriores continuam disponíveis:

```text
example.txt
├── Delete Marker
├── Version B
└── Version A
```

Foi possível recuperar uma versão anterior utilizando seu `VersionId`.

### IAM

Foi criada a policy:

```text
aws-cloud-practitioner-lab-s3
```

e anexada ao usuário:

```text
aws-cloud-practitioner-lab
```

As permissões foram separadas entre operações de bucket e objetos, aplicando Least Privilege.

Principais ações:

```text
s3:DeleteBucket
s3:ListBucket
s3:GetBucketVersioning
s3:PutBucketVersioning
s3:GetBucketPublicAccessBlock
s3:PutBucketPublicAccessBlock
s3:GetBucketTagging
s3:PutBucketTagging
s3:GetBucketPolicy
s3:GetBucketAcl
s3:GetBucketCORS
s3:GetBucketWebsite
s3:GetAccelerateConfiguration
s3:GetBucketRequestPayment
s3:GetBucketLogging
s3:GetLifecycleConfiguration
s3:GetReplicationConfiguration
s3:GetEncryptionConfiguration
s3:GetBucketObjectLockConfiguration
s3:ListBucketVersions

s3:GetObject
s3:PutObject
s3:DeleteObject
s3:DeleteObjectVersion
s3:GetObjectVersion
```

### Hands-on

Foi criado um bucket S3 via Terraform utilizando um nome baseado no Account ID:

```text
aws-cloud-practitioner-lab-s3-<account-id>
```

Também foi configurado Block Public Access.

A AWS CLI foi utilizada para:

```text
upload
download
list
delete
list-object-versions
get-object
```

Foram realizados experimentos de sobrescrita, Versioning e Delete Marker.

### Terraform

O bucket foi configurado com:

```hcl
force_destroy = true
```

para permitir o cleanup automático de objetos e versões durante o `terraform destroy`.

### Cleanup

O laboratório foi destruído com sucesso utilizando:

```bash
terraform destroy
```

O bucket, suas versões e demais recursos do laboratório foram removidos.

### Aprendizados / correções das respostas

Inicialmente houve confusão entre S3 e banco de dados NoSQL e entre Object Storage e File Storage.

Após o laboratório, os conceitos foram consolidados:

```text
S3  → Object Storage
EBS → Block Storage
EFS → File Storage
```

Também foi consolidada a diferença entre:

```text
Bucket → recipiente
Object → dado armazenado
Key    → identificador do objeto
VersionId → identificador de uma versão
```

O experimento de Versioning demonstrou na prática que excluir um objeto não necessariamente remove suas versões anteriores.

### Resultado

**Day 6 — S3: Completed**

---

# Day 7 — RDS

**Status:** Concluído

**Resultado:** Bom entendimento dos conceitos fundamentais de RDS e de sua integração com VPC, subnets, Security Groups e IAM. O hands-on também demonstrou na prática a necessidade de uma Service-Linked Role para o RDS.

## Conceitos estudados

* RDS como serviço gerenciado de banco de dados relacional;
* MySQL;
* DB Subnet Group;
* RDS em subnet privada;
* Security Group;
* Multi-AZ;
* Read Replica;
* Backup / Snapshot / Point-in-Time Recovery;
* IAM e Service-Linked Role;
* Least Privilege;
* Terraform.

## Principais aprendizados

### RDS

RDS é um serviço gerenciado de banco de dados relacional.

```text
RDS → Relational Database
S3  → Object Storage
EBS → Block Storage
EFS → File Storage
```

### Multi-AZ x Read Replica

```text
Multi-AZ
→ alta disponibilidade / failover

Read Replica
→ escalabilidade de leitura
```

São mecanismos com objetivos diferentes.

### RDS e VPC

O RDS foi criado utilizando as subnets privadas existentes:

```text
private-a → subnet-0117c0db343d98f9b
private-b → subnet-0e85409ac28b247d6
```

O RDS foi configurado como:

```text
PubliclyAccessible → false
MultiAZ            → false
Port               → 3306
```

### Security Group

Foi criado um Security Group específico:

```text
aws-cloud-practitioner-lab-rds
```

A validação final mostrou:

```text
Ingress → []
Egress  → []
```

Portanto, nenhuma conexão de entrada foi autorizada.

Uma futura aplicação poderia receber acesso ao RDS através de uma regra TCP 3306 originada no Security Group da aplicação.

## Hands-on realizado

Foi criado com Terraform:

```text
RDS MySQL
DB Subnet Group
Security Group
```

Configuração principal:

```text
Engine: MySQL
Instance: db.t4g.micro
Storage: 20 GB gp3
Encryption: enabled
Publicly accessible: false
Multi-AZ: false
```

O `terraform plan` apresentou:

```text
Plan: 3 to add, 0 to change, 0 to destroy.
```

Após o `apply`, o RDS ficou disponível e foi validado diretamente utilizando AWS CLI.

## Service-Linked Role

A primeira tentativa de criação do RDS falhou porque faltava:

```text
iam:CreateServiceLinkedRole
```

A policy específica do laboratório foi ajustada para permitir a criação da Service-Linked Role do RDS.

Depois do ajuste, o RDS foi criado com sucesso.

A Role `AWSServiceRoleForRDS` também foi validada.

### Aprendizado

As permissões necessárias para utilizar um serviço AWS podem incluir permissões auxiliares relacionadas à infraestrutura gerenciada pelo próprio serviço.

Isso reforça a necessidade de validar as permissões durante o processo de Least Privilege.

## Validação final

O RDS foi confirmado na infraestrutura correta:

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

O Security Group também foi validado na VPC correta e sem regras de ingress ou egress.

## Controle de custos

O RDS foi utilizado apenas para o exercício prático e posteriormente destruído.

Também não foram utilizados Multi-AZ, Read Replica ou NAT Gateway.

```text
create → test → observe → document → destroy
```

Após o cleanup, permaneceram no Terraform State somente os recursos IAM:

```text
aws_iam_policy.rds
aws_iam_user_policy_attachment.rds
```

## Resultado

* [x] Estudar RDS
* [x] Compreender RDS como serviço gerenciado
* [x] Estudar DB Subnet Group
* [x] Estudar RDS em subnet privada
* [x] Estudar Security Group
* [x] Diferenciar Multi-AZ e Read Replica
* [x] Estudar backup e recuperação
* [x] Criar RDS com Terraform
* [x] Praticar Least Privilege
* [x] Corrigir permissão de Service-Linked Role
* [x] Validar VPC, subnets e Security Group
* [x] Destruir os recursos temporários
* [x] Consolidar conceitos da CLF-C02

**Day 7 — RDS: concluído.**

---

# Day 8 — ECR

**Status:** Concluído

**Resultado:** Bom entendimento do papel do ECR como Container Registry e de sua relação com Docker e ECS. O hands-on também reforçou conceitos de Image, Container, Repository, Tag, Digest e Least Privilege.

## Conceitos estudados

* ECR como Container Registry;
* Docker Image x Container;
* Registry x Repository;
* Image Tag;
* Image Digest;
* Push x Pull;
* ECR x ECS;
* ECR x VPC;
* IAM e Least Privilege;
* autenticação do Docker no ECR.

## Principais aprendizados

### ECR x ECS

```text
ECR
→ armazena e distribui imagens

ECS
→ gerencia a execução dos containers
```

O ECR não executa containers.

### Image x Container

```text
Image
→ artefato/template

Container
→ instância em execução da Image
```

### Tag x Digest

```text
Tag
→ referência legível

Digest
→ identificação baseada no conteúdo
```

Foi utilizada a tag:

```text
1.0
```

e validado o Digest da imagem no ECR.

## Hands-on

Foi criado com Terraform o repository:

```text
aws-cloud-practitioner-lab
```

na região:

```text
sa-east-1
```

Foi criada uma policy específica:

```text
aws-cloud-practitioner-lab-ecr
```

e anexada ao usuário:

```text
aws-cloud-practitioner-lab
```

Foi criada uma Docker Image, autenticada no ECR e realizado:

```text
docker push
```

A imagem foi validada utilizando:

```bash
aws ecr describe-images
```

Depois a imagem foi removida localmente e recuperada novamente através de:

```text
docker pull
```

O Digest observado no ECR correspondeu ao Digest retornado pelo Docker.

## Problemas e aprendizados

Durante o cleanup, o Terraform inicialmente não conseguiu remover o repository porque ele ainda continha imagens.

Também foi necessário ajustar a permissão:

```text
iam:DetachUserPolicy
```

para permitir que o Terraform removesse a associação da policy durante o cleanup.

Isso reforçou que as permissões necessárias para destruir recursos podem ser diferentes das utilizadas durante sua criação.

## Controle de custos

O ECR foi utilizado apenas durante o exercício e o repository foi destruído ao final.

Não foram mantidos recursos temporários do módulo.

```text
create → test → observe → document → destroy
```

## Resultado

* [x] Estudar ECR
* [x] Criar ECR Repository com Terraform
* [x] Aplicar Least Privilege
* [x] Criar e enviar Docker Image
* [x] Validar imagem no ECR
* [x] Fazer pull da imagem
* [x] Compreender Tag e Digest
* [x] Corrigir problemas de cleanup
* [x] Destruir os recursos temporários
* [x] Consolidar conceitos da CLF-C02

**Day 8 — ECR: concluído.**

---

## Day 9 — ECS

### Conteúdo estudado

Estudamos o **Amazon ECS (Elastic Container Service)** como serviço de orquestração de containers.

Principais conceitos:

* **ECS**: serviço de orquestração que gerencia a execução e o ciclo de vida de containers.
* **ECR**: serviço usado para armazenar e distribuir imagens de containers.
* **Cluster**: agrupamento lógico de recursos do ECS onde as Tasks são executadas.
* **Task Definition**: template que define como uma Task deve ser executada, incluindo imagem, CPU, memória, rede, roles e configuração de logs.
* **Task**: instância em execução de uma Task Definition.
* **Container**: processo baseado na imagem especificada na Task Definition.
* **Service**: mantém a quantidade desejada de Tasks em execução e substitui Tasks que deixam de funcionar.
* **Fargate**: opção de computação serverless para ECS, na qual a AWS gerencia a infraestrutura necessária para executar os containers.
* **ECS com EC2**: alternativa em que as instâncias EC2 fornecem a infraestrutura de computação para as Tasks.
* **CloudWatch Logs**: utilizado para armazenar e observar os logs produzidos pelos containers.

Fluxo estudado:

```text
Docker
  ↓
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

Também foi reforçada a diferença entre os componentes:

* **ECR armazena a imagem**.
* **ECS orquestra a execução**.
* **Fargate fornece a capacidade de computação sem gerenciamento direto de servidores**.
* **Task Definition descreve como executar o container**.
* **Task representa uma execução da definição**.
* **Service mantém a quantidade desejada de Tasks**.

### Diagnóstico

O diagnóstico inicial mostrou que os conceitos gerais de containers e Task Definition já eram parcialmente conhecidos, mas havia dúvidas principalmente sobre:

* diferença entre ECS e um cluster de máquinas;
* relação entre Cluster, Task Definition, Task, Container e Service;
* diferença entre Fargate e EC2;
* comportamento de um ECS Service quando uma Task é interrompida;
* componentes necessários para uma arquitetura ECS.

Durante a correção, foi estabelecido que ECS não é simplesmente um gerenciador de máquinas, mas um serviço de **orquestração de containers**. Também foi esclarecido que um Cluster é um agrupamento lógico e não necessariamente um conjunto de instâncias EC2, pois o ECS pode utilizar Fargate.

### Laboratório prático

Foi criado um laboratório ECS utilizando Terraform, ECR e Fargate.

Recursos utilizados:

* ECS Cluster;
* ECS Service;
* ECS Task Definition;
* Fargate;
* ECR como origem da imagem;
* IAM Role para execução da Task;
* Security Group;
* CloudWatch Log Group;
* VPC e subnets públicas existentes.

A Task Definition utilizou:

* CPU: `256`;
* memória: `512 MiB`;
* network mode: `awsvpc`;
* launch type: Fargate;
* imagem `aws-cloud-practitioner-lab:1.0` armazenada no ECR.

O Service foi configurado com:

```text
desired_count = 1
```

e utilizando as subnets públicas existentes, com atribuição de IP público.

Não foi utilizado Application Load Balancer nem RDS neste laboratório, mantendo o primeiro exercício de ECS simples e focado nos conceitos de execução e orquestração.

### IAM

Foi criada uma política específica para o laboratório ECS, evitando utilizar `AdministratorAccess` como permissão permanente.

Durante a implementação foram identificadas algumas permissões necessárias:

* gerenciamento dos recursos ECS;
* gerenciamento do CloudWatch Logs;
* gerenciamento do Security Group;
* gerenciamento da IAM Role de execução;
* `iam:PassRole`;
* `iam:CreateServiceLinkedRole` para permitir a criação/uso da service-linked role do ECS;
* `logs:DescribeLogStreams` para inspeção dos streams de logs;
* `iam:ListInstanceProfilesForRole` durante o processo de destruição.

Também foi utilizado o **ECS Task Execution Role**, assumido pelo serviço `ecs-tasks.amazonaws.com`, com a política gerenciada `AmazonECSTaskExecutionRolePolicy`.

### Validação

Após a criação, o ECS Service apresentou:

```text
Status: ACTIVE
Desired: 1
Running: 1
Pending: 0
```

A Task executou em Fargate com:

```text
LaunchType: FARGATE
CPU: 256
Memory: 512
```

Os logs do container foram encontrados no CloudWatch Logs, confirmando a inicialização do Nginx dentro do container.

Entre os eventos observados estavam:

```text
/docker-entrypoint.sh
nginx/1.31.6
start worker processes
```

Isso confirmou que não apenas a Task estava em estado `RUNNING`, mas que o container havia iniciado efetivamente a aplicação.

### Teste de self-healing

Para validar o comportamento do ECS Service, a Task em execução foi interrompida manualmente:

```bash
aws ecs stop-task \
  --cluster aws-cloud-practitioner-lab \
  --task <task-id> \
  --region sa-east-1
```

O Service detectou a redução da quantidade de Tasks e iniciou automaticamente uma nova Task para manter:

```text
desired_count = 1
```

A nova Task entrou em estado `RUNNING`.

Esse teste demonstrou na prática a responsabilidade do **ECS Service** de manter a quantidade desejada de Tasks.

### Terraform

Ao final da validação:

```text
No changes. Your infrastructure matches the configuration.
```

Isso confirmou que os recursos existentes estavam de acordo com a configuração Terraform.

Após os testes, foi executado:

```bash
terraform destroy
```

Os recursos criados especificamente para o laboratório ECS foram destruídos para evitar custos desnecessários.

### Resultado do Day 9

Concluído o estudo e laboratório de **Amazon ECS**, incluindo:

* ECS;
* Cluster;
* Task Definition;
* Task;
* Container;
* Service;
* Fargate;
* ECR + ECS;
* IAM Task Execution Role;
* CloudWatch Logs;
* execução de containers em Fargate;
* manutenção de desired count;
* substituição automática de Tasks;
* validação com Terraform;
* destruição dos recursos após o laboratório.

**Status:** concluído.

---

## Day 10 — Amazon SQS

**Status:** Concluído ✅

### O que estudei

* Amazon SQS e desacoplamento entre serviços.
* Comunicação assíncrona e filas como buffer.
* Ciclo `SendMessage → ReceiveMessage → DeleteMessage`.
* Visibility Timeout.
* Entrega at-least-once e necessidade de tolerar mensagens duplicadas.
* `MessageId` vs. `ReceiptHandle`.
* SQS Standard vs. FIFO.
* Long Polling.
* Integração conceitual entre Producer, SQS e Consumer.
* IAM least privilege para operações de SQS.

### Hands-on

Criei uma SQS Standard Queue utilizando Terraform com:

* `visibility_timeout_seconds = 30`;
* `message_retention_seconds = 86400`;
* `receive_wait_time_seconds = 10`;
* IAM policy específica para SQS;
* `depends_on` entre o attachment da policy e a fila.

Testei:

* criação da fila;
* obtenção da Queue URL;
* envio de mensagens;
* recebimento de mensagens;
* comportamento do Visibility Timeout;
* recebimentos repetidos;
* utilização do `ReceiptHandle`;
* exclusão de mensagens;
* Long Polling;
* confirmação de que a mensagem não estava mais disponível após `DeleteMessage`.

### O que ficou mais claro

Antes do laboratório, eu entendia SQS principalmente como uma fila entre serviços. Na prática, ficou mais claro que receber uma mensagem não significa removê-la e que o consumidor precisa confirmar o processamento através do `DeleteMessage`.

Também observei na prática que uma mesma `MessageId` pode aparecer novamente em uma SQS Standard, com outro `ReceiptHandle`, reforçando o conceito de entrega at-least-once e a necessidade de consumidores tolerarem duplicidade.

### Segurança e custos

O laboratório foi executado sem `AdministratorAccess`, utilizando uma policy específica de SQS. A fila foi destruída após os testes com `terraform destroy`.

---

## Day 11 — CloudWatch

**Status:** Concluído

### O que aprendi

* CloudWatch é um serviço de monitoramento e observabilidade da AWS.
* Métricas são dados numéricos organizados ao longo do tempo; estatísticas como `Average`, `Minimum` e `Maximum` são aplicadas aos dados das métricas.
* Os CloudWatch Logs utilizam a estrutura Log Group → Log Stream → Log Events.
* O EC2 fornece métricas padrão como utilização de CPU, enquanto o monitoramento de memória geralmente requer o CloudWatch Agent.
* CloudWatch Alarms podem assumir os estados `OK`, `ALARM` e `INSUFFICIENT_DATA`.
* O SQS disponibiliza métricas como `ApproximateNumberOfMessagesVisible` para o CloudWatch.
* CloudWatch, CloudTrail e EventBridge possuem finalidades diferentes.
* O CloudWatch pode fornecer informações de monitoramento utilizadas por mecanismos de automação, como Auto Scaling.

### Hands-on

Criei uma fila SQS e um CloudWatch Alarm utilizando Terraform e permissões IAM de least privilege.

Foi observado o ciclo completo do alarm:

```text
INSUFFICIENT_DATA → OK → ALARM → OK
```

O alarm foi acionado quando uma mensagem permaneceu disponível na fila e retornou para `OK` após a mensagem ser consumida e excluída.

Durante o laboratório, o Terraform exigiu a permissão `cloudwatch:ListTagsForResource`, que foi adicionada especificamente à policy do CloudWatch.

Também foi identificada a diferença entre a métrica do CloudWatch `ApproximateNumberOfMessagesVisible` e o atributo da API do SQS `ApproximateNumberOfMessages`.

### Limpeza

Executado:

```bash
terraform destroy
```

Os recursos do laboratório foram destruídos com sucesso.

---
