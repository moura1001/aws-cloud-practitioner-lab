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

## Próximo módulo

**Day 6 — S3**
