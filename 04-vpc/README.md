# 04 — VPC (Virtual Private Cloud)

Laboratório prático do **AWS Certified Cloud Practitioner (CLF-C02)** para compreender redes virtuais na AWS utilizando VPC, Subnets, Route Tables e Internet Gateway.

---

## 1. Objetivos

Ao finalizar este laboratório, os objetivos são:

* entender o conceito de **VPC (Virtual Private Cloud)**;
* compreender **CIDR** e divisão de redes;
* diferenciar **VPC, Availability Zone e Subnet**;
* entender a diferença entre **Subnet pública e privada**;
* compreender **Route Tables**;
* compreender o funcionamento do **Internet Gateway (IGW)**;
* entender o papel do **NAT Gateway**;
* relacionar VPC com **alta disponibilidade**;
* praticar a criação da infraestrutura utilizando Terraform;
* validar a infraestrutura utilizando AWS CLI;
* praticar **Least Privilege** durante o uso do Terraform;
* evitar recursos desnecessários que possam gerar custos.

---

# 2. Perguntas de diagnóstico

Antes da explicação completa, foram utilizadas perguntas para identificar o conhecimento prévio sobre VPC.

## 2.1 O que é uma VPC?

### Minha resposta:

> É uma forma de isolar aplicações de outras entre as contas da AWS e a Internet em geral, só ficando públicas se explicitamente expostas

### Correção:

A ideia de isolamento estava correta, mas a definição precisava ser mais precisa.

Uma **VPC (Virtual Private Cloud)** é uma **rede virtual logicamente isolada dentro da AWS**, na qual podemos configurar o espaço de endereçamento IP, subnets, rotas e regras de comunicação.

A VPC não significa simplesmente "isolar aplicações de outras contas".

Uma forma melhor de memorizar:

```text
VPC
 ↓
Rede virtual logicamente isolada
 ↓
Nós controlamos sua organização e comunicação
```

A VPC também não é "pública" ou "privada" por si só. Dentro dela podemos criar subnets com diferentes características de conectividade.

---

## 2.2 O que significa `10.0.0.0/16`? Qual a diferença para `/24`?

### Minha resposta:

> Não sei, não lembro desse conteúdo sobre máscaras de rede

### Correção:

A notação CIDR define o intervalo de endereços IP disponível para aquela rede.

Quanto **menor o número depois da barra**, maior é o espaço de endereços.

Por exemplo:

```text
10.0.0.0/16
     ↓
rede maior

10.0.1.0/24
     ↓
rede menor
```

Para o CLF-C02, não é necessário dominar cálculos binários complexos. O mais importante é reconhecer que:

```text
/16 → mais endereços
/24 → menos endereços
```

No laboratório usamos:

```text
VPC
10.0.0.0/16
```

e dividimos esse espaço em subnets `/24`.

---

## 2.3 Qual é a diferença entre uma Subnet pública e uma Subnet privada?

### Minha resposta:

> Não sei, mas acredito que é algo relacionado a está acessível pela Internet ou não

### Correção:

A ideia estava correta, mas precisamos identificar **o mecanismo responsável pela conectividade**.

Uma subnet é considerada pública quando sua **Route Table possui uma rota para um Internet Gateway**.

Exemplo:

```text
0.0.0.0/0
      ↓
Internet Gateway
```

Uma subnet privada não possui uma rota direta para o Internet Gateway.

Portanto:

```text
Public Subnet
    ↓
Route Table
    ↓
Internet Gateway
    ↓
Internet
```

Enquanto:

```text
Private Subnet
    ↓
Route Table
    ↓
sem rota direta para IGW
```

Uma subnet privada ainda pode possuir acesso de saída à Internet por meio de um **NAT Gateway**, mas isso não foi criado neste laboratório devido ao custo.

---

## 2.4 O que uma EC2 precisa para conseguir acessar a Internet?

### Minha resposta:

> Na aplicação que estou trabalhando existe um ALB, e também sei que precisa de algo relacionado a Route Tables

### Correção:

A Route Table realmente é importante, mas o ALB não é responsável por simplesmente fornecer acesso à Internet para uma EC2.

Para uma EC2 ter conectividade com a Internet, precisamos considerar elementos como:

* subnet;
* Route Table;
* Internet Gateway;
* endereço IP público, quando necessário;
* Security Group;
* regras de rede.

Uma arquitetura simplificada seria:

```text
EC2
 ↓
Subnet pública
 ↓
Route Table
 ↓
Internet Gateway
 ↓
Internet
```

Já uma EC2 em subnet privada não possui esse caminho direto.

---

## 2.5 Uma VPC pode abranger múltiplas Availability Zones?

Qual destas afirmações você considera correta?

A) Uma VPC pertence a uma Availability Zone.

B) Uma VPC pode abranger várias Availability Zones dentro de uma Region.

C) Uma Subnet pode abranger várias Regions.

D) Uma Availability Zone pode pertencer a várias Regions.

### Minha resposta:

> Acredito que a afirmação correta é a B pois as aplicações resilientes tem que saber se comunicar entre as diferentes AZs

### Correção:

**Correto.**

Uma VPC pode abranger múltiplas Availability Zones dentro de uma Region.

No laboratório utilizamos:

```text
Region: sa-east-1

VPC
├── sa-east-1a
└── sa-east-1b
```

Por outro lado, uma **Subnet pertence a uma única Availability Zone**.

Essa relação é importante:

```text
Region
   │
   ├── Availability Zone
   │       └── Subnet
   │
   └── Availability Zone
           └── Subnet
```

---

## 2.6 Para servidores de aplicação que não devem ficar diretamente expostos à Internet, qual subnet seria mais adequada?

Uma empresa possui servidores de aplicação que não devem ser acessíveis diretamente pela Internet, mas precisam acessar serviços externos, como APIs públicas.

Você colocaria esses servidores em:

A) Subnet pública

B) Subnet privada

C) Edge Location

D) Availability Zone sem VPC

### Minha resposta:

> Os servidores deveriam está numa Subnet privada, provavelmente com regras de saída permitidas mas de entrada não, provavelmente sendo configuradas através de Route Tables

### Correção:

**Correto.**

Servidores de aplicação que não precisam receber conexões diretamente da Internet normalmente ficam em **private subnets**.

As Route Tables determinam os caminhos que o tráfego pode seguir.

Se a aplicação precisar acessar a Internet para saída, uma arquitetura comum seria:

```text
Private Subnet
      ↓
NAT Gateway
      ↓
Internet Gateway
      ↓
Internet
```

O NAT Gateway permite que recursos privados iniciem conexões para fora sem fornecer uma rota direta de entrada da Internet para esses recursos.

---

# 3. Conceitos estudados

## 3.1 VPC

Uma **VPC (Virtual Private Cloud)** é uma rede virtual logicamente isolada dentro da AWS.

Ela permite definir:

* espaço de endereçamento IP;
* subnets;
* tabelas de rotas;
* gateways;
* regras de comunicação;
* organização dos recursos de rede.

No laboratório:

```text
VPC
CIDR: 10.0.0.0/16
Region: sa-east-1
```

---

## 3.2 CIDR

CIDR define o intervalo de endereços IP de uma rede.

Utilizamos:

```text
10.0.0.0/16
```

para a VPC.

Depois dividimos esse espaço:

```text
10.0.1.0/24
10.0.2.0/24
10.0.11.0/24
10.0.12.0/24
```

Uma regra prática:

```text
/16
 ↓
rede maior

/24
 ↓
rede menor
```

Não é necessário memorizar cálculos binários para o CLF-C02, mas é importante entender que CIDR determina o tamanho do espaço de endereçamento.

---

## 3.3 Availability Zone × Subnet

Uma VPC pode utilizar várias Availability Zones.

Porém:

> **Uma subnet pertence a uma única Availability Zone.**

No laboratório:

```text
sa-east-1a
├── public-a
└── private-a

sa-east-1b
├── public-b
└── private-b
```

Distribuir recursos entre diferentes AZs é uma estratégia de **alta disponibilidade e resiliência**.

---

# 4. Public Subnet × Private Subnet

## Public Subnet

Uma subnet pública possui uma Route Table com uma rota para um Internet Gateway.

No laboratório:

```text
Public Route Table
       │
       └── 0.0.0.0/0 → Internet Gateway
```

## Private Subnet

Uma subnet privada não possui rota direta para um Internet Gateway.

No laboratório:

```text
Private Route Table
       │
       └── sem rota direta para IGW
```

### Pegadinha da CLF-C02

> **Uma subnet pública não significa que todos os recursos dentro dela estão automaticamente acessíveis pela Internet.**

Para uma EC2 ser acessível externamente, ainda precisamos considerar:

* endereço IP público;
* Security Group;
* regras de rede;
* configuração da própria aplicação.

Portanto:

```text
Public Subnet
       ≠
EC2 automaticamente acessível pela Internet
```

---

# 5. Route Table

Uma **Route Table** define para onde o tráfego de uma subnet deve ser encaminhado.

Uma rota típica para uma subnet pública é:

```text
Destination: 0.0.0.0/0
Target: Internet Gateway
```

Isso significa, de forma simplificada:

> Para destinos IPv4 que não pertencem a uma rota mais específica, encaminhar o tráfego para o Internet Gateway.

No laboratório criamos:

```text
Public Route Table
Private Route Table
```

---

# 6. Internet Gateway

O **Internet Gateway (IGW)** permite a comunicação entre uma VPC e a Internet.

Criamos um Internet Gateway e o associamos à VPC:

```text
VPC
 │
 └── Internet Gateway
```

Depois criamos uma rota:

```text
0.0.0.0/0
      ↓
Internet Gateway
```

### Importante

A simples existência de um Internet Gateway não torna automaticamente todos os recursos da VPC públicos.

É necessário que exista o caminho de rede adequado, além das demais configurações necessárias do recurso.

---

# 7. NAT Gateway

O **NAT Gateway** permite que recursos em subnets privadas iniciem conexões com a Internet sem ficarem diretamente expostos para conexões de entrada.

Exemplo:

```text
Private Subnet
      ↓
NAT Gateway
      ↓
Internet Gateway
      ↓
Internet
```

### Por que não utilizamos NAT Gateway?

Porque:

1. ele não era necessário para os objetivos atuais do laboratório;
2. é um recurso que gera custo;
3. queríamos manter o laboratório simples;
4. o objetivo principal era compreender VPC, Subnets, Route Tables e IGW.

**Regra do laboratório:**

> Não criar recursos pagos apenas para demonstrar conceitos que podem ser compreendidos sem eles.

---

# 8. Security Group

O **Security Group** funciona como um firewall virtual associado aos recursos.

Ele controla quais conexões são permitidas.

Por exemplo, mesmo que uma EC2 esteja em uma subnet pública:

```text
Internet
   ↓
Public Subnet
   ↓
EC2
```

o Security Group ainda pode bloquear determinada porta.

Portanto, conectividade de rede e autorização de tráfego são conceitos diferentes.

---

# 9. Arquitetura criada

A infraestrutura final do laboratório ficou:

```text
                         VPC 10.0.0.0/16
                                │
              ┌─────────────────┴─────────────────┐
              │                                   │
          sa-east-1a                          sa-east-1b
              │                                   │
       ┌──────┴──────┐                     ┌──────┴──────┐
       │             │                     │             │
    public-a      private-a             public-b      private-b
  10.0.1.0/24  10.0.11.0/24          10.0.2.0/24  10.0.12.0/24
       │             │                     │             │
       └──────┐      │                     │      ┌──────┘
              │      │                     │      │
              └──────┴──── Public RT ──────┴──────┘
                              │
                         0.0.0.0/0
                              │
                     Internet Gateway
```

A associação das Route Tables é:

```text
Public Route Table
├── public-a
└── public-b

Private Route Table
├── private-a
└── private-b
```

A Private Route Table não possui rota direta para o Internet Gateway.

---

# 10. Hands-on com Terraform

## 10.1 VPC

Foi criada uma VPC com:

```hcl
cidr_block           = "10.0.0.0/16"
enable_dns_support   = true
enable_dns_hostnames = true
```

---

## 10.2 Subnets

Foram criadas quatro subnets:

| Subnet      | CIDR           | AZ           | Tipo    |
| ----------- | -------------- | ------------ | ------- |
| `public_a`  | `10.0.1.0/24`  | `sa-east-1a` | Pública |
| `public_b`  | `10.0.2.0/24`  | `sa-east-1b` | Pública |
| `private_a` | `10.0.11.0/24` | `sa-east-1a` | Privada |
| `private_b` | `10.0.12.0/24` | `sa-east-1b` | Privada |

Nas subnets públicas foi utilizado:

```hcl
map_public_ip_on_launch = true
```

### Importante

`map_public_ip_on_launch = true` **não é o que transforma a subnet em pública**.

Isso apenas configura instâncias lançadas nessa subnet para receberem automaticamente um IPv4 público.

A característica pública vem principalmente da **Route Table com rota para o Internet Gateway**.

---

## 10.3 Internet Gateway

Foi criado e associado à VPC:

```text
aws_internet_gateway.main
```

---

## 10.4 Route Tables

Foram criadas:

```text
aws_route_table.public
aws_route_table.private
```

A Route Table pública recebeu:

```text
0.0.0.0/0 → Internet Gateway
```

---

## 10.5 Associações

As subnets foram associadas às respectivas Route Tables:

```text
public-a  → public route table
public-b  → public route table

private-a → private route table
private-b → private route table
```

---

# 11. Validação com AWS CLI

A disponibilidade das Availability Zones foi verificada:

```bash
aws ec2 describe-availability-zones \
  --region sa-east-1 \
  --query 'AvailabilityZones[].ZoneName' \
  --output table
```

Resultado utilizado:

```text
sa-east-1a
sa-east-1b
sa-east-1c
```

Foram utilizadas `sa-east-1a` e `sa-east-1b`.

As subnets foram verificadas com:

```bash
aws ec2 describe-subnets \
  --region sa-east-1 \
  --query 'Subnets[].{Subnet:SubnetId,CIDR:CidrBlock,AZ:AvailabilityZone,PublicIP:MapPublicIpOnLaunch}' \
  --output table
```

As Route Tables foram verificadas com:

```bash
aws ec2 describe-route-tables \
  --region sa-east-1 \
  --query 'RouteTables[].{RouteTable:RouteTableId,Routes:Routes[].DestinationCidrBlock}' \
  --output table
```

Os comandos foram executados com sucesso.

---

# 12. Least Privilege aplicado ao laboratório

Durante o Day 4, o usuário:

```text
aws-cloud-practitioner-lab
```

estava utilizando inicialmente:

```text
AdministratorAccess
```

para permitir a criação inicial da infraestrutura.

Depois foi criada a política específica:

```text
aws-cloud-practitioner-lab-vpc
```

O `AdministratorAccess` foi removido para testar se o laboratório funcionaria utilizando somente as permissões necessárias.

Durante os testes com:

```bash
terraform plan
```

foram identificadas permissões adicionais necessárias para que o Terraform pudesse consultar e gerenciar os recursos e a própria política IAM.

Entre elas:

```text
ec2:DescribeVpcAttribute
ec2:DescribeAvailabilityZones
ec2:CreateTags
ec2:DescribeNetworkInterfaces
ec2:ModifySubnetAttribute
iam:GetPolicy
iam:GetPolicyVersion
iam:ListAttachedUserPolicies
```

Depois dos ajustes, o:

```bash
terraform plan
```

funcionou corretamente **sem `AdministratorAccess`**.

### Principal aprendizado

O Terraform não executa apenas operações de criação.

Durante o `plan`, ele também precisa consultar o estado atual dos recursos e das políticas.

Isso demonstrou na prática:

```text
AccessDenied
     ↓
identificar a Action necessária
     ↓
adicionar somente a permissão necessária
     ↓
terraform plan
     ↓
validar novamente
```

Esse processo reforçou o conceito de **Least Privilege** estudado no Day 3.

---

# 13. Resultado do Terraform

A infraestrutura foi criada com sucesso.

Na etapa das Route Tables, o Terraform apresentou:

```text
Plan: 8 to add, 0 to change, 0 to destroy.
```

Foram criados:

* 1 Internet Gateway;
* 2 Route Tables;
* 1 rota pública;
* 4 associações de Route Table.

As quatro subnets haviam sido criadas anteriormente.

---

# 14. Controle de custos

O laboratório foi deliberadamente mantido simples.

Não foram criados:

* NAT Gateway;
* EC2;
* Load Balancer;
* outros recursos pagos desnecessários.

A VPC e seus componentes básicos foram utilizados para aprendizado de rede.

O NAT Gateway foi evitado principalmente porque possui custo e não era necessário para o objetivo do Day 4.

### Estratégia de segurança de custos

```text
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

Recursos que precisarem permanecer para módulos futuros devem ter sua permanência justificada.

---

# 15. Perguntas finais de verificação

Após a prática, as seguintes questões foram respondidas:

## 15.1 Por que `public-a` está em `sa-east-1a` e `public-b` em `sa-east-1b`?

### Minha resposta:

> Primeiro que uma subnet só pode está vinculada a uma única AZ e segundo porque isso é uma das configurações necessárias caso se esteja querendo ter alta resiliência na aplicação que precisa se comunicar com a Internet

### Correção:

**Correto.**

Uma subnet pertence a uma única Availability Zone.

Distribuir subnets equivalentes entre AZs diferentes permite distribuir os recursos da aplicação entre diferentes zonas, aumentando a resiliência e reduzindo o impacto de uma falha localizada.

A comunicação com a Internet não é, por si só, o motivo da alta disponibilidade; o ponto principal é a distribuição dos recursos entre diferentes AZs.

---

## 15.2 O que faz a subnet ser pública?

### Minha resposta:

> Uma subnet é pública quando ela tem uma rota de acesso para Internet Gateway (IGW)

### Correção:

**Correto.**

A característica fundamental é a existência de uma rota na Route Table associada à subnet apontando para um Internet Gateway.

No laboratório:

```text
0.0.0.0/0
     ↓
Internet Gateway
```

---

## 15.3 Por que `private-a` não consegue acessar diretamente a Internet através do nosso IGW?

### Minha resposta:

> A subnet privada private-a não tem uma rota de acesso vinculada ao IGW

### Correção:

**Correto.**

A Route Table associada à `private-a` não possui uma rota direta para o Internet Gateway.

Caso fosse necessário acesso de saída à Internet, poderíamos utilizar um NAT Gateway, mas isso não faz parte da infraestrutura atual.

---

## 15.4 Se amanhã colocarmos uma EC2 em `public-a`, ela automaticamente estará acessível pela Internet?

### Minha resposta:

> Aparentemente sim mas provavelmente não porque também pode ter bloqueios de security group, firewall, etc

### Correção:

**Parcialmente correto.**

Estar em uma subnet pública fornece o caminho de rede para a Internet, mas não significa que a EC2 automaticamente estará acessível externamente.

Para acesso pela Internet, devemos considerar:

* endereço IP público;
* Route Table;
* Internet Gateway;
* Security Group;
* regras de firewall;
* serviço/aplicação escutando na porta correspondente.

Portanto:

```text
Subnet pública
      ≠
EC2 automaticamente acessível
```

---

## 15.5 Por que não criamos um NAT Gateway neste laboratório?

### Minha resposta:

> Esse é um recurso pago da AWS e não era necessário para fixar os conceitos realmente importantes do laboratório prático

### Correção:

**Correto.**

O NAT Gateway possui custo e não era necessário para os objetivos do Day 4.

A ausência dele também ajuda a manter o laboratório alinhado com a prioridade de controle de custos.

---

# 16. Pegadinhas importantes para a CLF-C02

### 1. VPC pode abranger várias AZs

```text
Region
└── VPC
    ├── AZ A
    │   └── Subnets
    └── AZ B
        └── Subnets
```

### 2. Uma subnet pertence a uma única AZ

Não existe uma subnet pertencendo simultaneamente a duas AZs.

### 3. Public subnet não significa EC2 automaticamente pública

É necessário considerar IP público e regras de segurança.

### 4. Internet Gateway não é NAT Gateway

**IGW:**

```text
VPC ↔ Internet
```

**NAT Gateway:**

```text
Private resources
       ↓
NAT Gateway
       ↓
Internet
```

### 5. Route Table determina o caminho

Não confundir Route Table com Security Group.

```text
Route Table
→ para onde o tráfego vai?

Security Group
→ qual tráfego é permitido?
```

### 6. NAT Gateway possui custo

Não criar automaticamente só porque existe uma subnet privada.

---

# 17. Checklist de conclusão

* [x] Entendi o conceito de VPC
* [x] Entendi CIDR
* [x] Entendi `/16` vs `/24`
* [x] Entendi Region → AZ → Subnet
* [x] Entendi Subnet pública
* [x] Entendi Subnet privada
* [x] Entendi Route Table
* [x] Entendi Internet Gateway
* [x] Entendi o papel do NAT Gateway
* [x] Entendi a relação entre subnet pública e acesso à Internet
* [x] Entendi o papel do Security Group
* [x] Entendi a distribuição entre múltiplas AZs
* [x] Criei uma VPC com Terraform
* [x] Criei quatro subnets
* [x] Criei Internet Gateway
* [x] Criei Route Tables
* [x] Configurei rota pública
* [x] Associei as subnets às Route Tables
* [x] Validei a infraestrutura com AWS CLI
* [x] Pratiquei Least Privilege
* [x] Removi `AdministratorAccess`
* [x] Executei `terraform plan` sem `AdministratorAccess`
* [x] Evitei NAT Gateway para não gerar custo desnecessário

---

# 18. Conclusão

O Day 4 consolidou os principais fundamentos de redes da AWS necessários para compreender a arquitetura dos próximos módulos.

A VPC criada neste laboratório será utilizada como base para os próximos recursos, especialmente no laboratório de **EC2**.

A arquitetura atual é:

```text
                    VPC 10.0.0.0/16
                           │
             ┌─────────────┴─────────────┐
             │                           │
         sa-east-1a                  sa-east-1b
             │                           │
      ┌──────┴──────┐             ┌─────┴──────┐
      │             │             │            │
   public-a      private-a     public-b      private-b
      │             │             │            │
      └─────────────┴─────────────┴────────────┘
                           │
                    Route Tables
                           │
                    Internet Gateway
```
