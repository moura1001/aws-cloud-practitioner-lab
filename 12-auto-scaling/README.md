# 12 - Auto Scaling

## Objetivo

Este módulo apresenta o Amazon EC2 Auto Scaling, com foco em elasticidade, capacidade desejada, scale out, scale in, substituição automática de instâncias e distribuição entre Availability Zones.

O laboratório utiliza uma infraestrutura VPC já existente dos módulos anteriores e cria somente os recursos necessários para demonstrar o funcionamento do Auto Scaling.

---

## 1. Diagnóstico inicial

Antes da explicação teórica, foram respondidas perguntas para avaliar o conhecimento prévio sobre Auto Scaling.

### 1. O que é o Amazon EC2 Auto Scaling?

Explique com suas palavras qual problema ele resolve e para que você o utilizaria.

**Minha resposta:**

> Auto Scaling existe dentro do conceito de elasticidade da AWS para lidar com o problema de utilizar corretamente a capacidade computacional necessárias, aumentando o diminuindo quando necessário.

**Correção:**

Correto.

A resposta identificou o principal objetivo: adaptar a capacidade computacional conforme a necessidade.

O EC2 Auto Scaling pode aumentar ou reduzir automaticamente a quantidade de instâncias EC2 de acordo com regras de scaling e limites definidos.

**Ponto de atenção para a CLF-C02:** Auto Scaling está diretamente relacionado à **elasticidade**, mas elasticidade é um conceito mais amplo do que apenas EC2 Auto Scaling.

---

### 2. O que você entende por `Auto Scaling Group (ASG)`?

O que você imagina que seja um ASG e qual seria sua relação com instâncias EC2?

**Minha resposta:**

> ASG deve ser o pool de instâncias disponíveis que serão gerenciadas e ativadas e desativadas ou destruídas no caso de ser Spot

**Correção:**

Parcialmente correto.

A ideia de um "pool de instâncias" gerenciado está próxima do conceito de Auto Scaling Group. O ASG realmente gerencia um conjunto de instâncias EC2 e mantém a capacidade dentro dos limites configurados.

Porém, a associação com **Spot** está incorreta.

Spot Instance é um modelo de aquisição de capacidade EC2. Não é o que define um Auto Scaling Group.

Um ASG pode gerenciar instâncias independentemente de serem Spot ou não.

**Ponto de atenção para a CLF-C02:**

```text
Auto Scaling Group
→ gerenciamento de capacidade

Spot Instance
→ modelo de aquisição de capacidade
```

São conceitos diferentes.

---

### 3. Qual a diferença entre **scale out** e **scale in**?

Dê um exemplo de quando cada um poderia acontecer.

**Minha resposta:**

> Scale out deve ser diminuir e scale in aumentar

**Correção:**

Os conceitos foram invertidos.

O correto é:

```text
Scale out → aumentar a quantidade de instâncias

Scale in → diminuir a quantidade de instâncias
```

Exemplo:

```text
1 EC2 → 2 EC2
```

é **scale out**.

Já:

```text
2 EC2 → 1 EC2
```

é **scale in**.

Essa é uma distinção importante para a CLF-C02.

---

### 4. O que você entende por **desired capacity**, **minimum capacity** e **maximum capacity**?

Por exemplo, se um ASG tivesse:

```text
Min = 1
Desired = 2
Max = 4
```

O que você esperaria que acontecesse?

**Minha resposta:**

> 2 máquinas começariam em execução podendo reduzir e ficar com 1 e aumentando no máximo para 4 ao mesmo tempo

**Correção:**

Correto.

A interpretação está adequada:

```text
Minimum = 1
→ capacidade mínima

Desired = 2
→ capacidade que o ASG procura manter normalmente

Maximum = 4
→ limite máximo de capacidade
```

Portanto, inicialmente seriam buscadas 2 instâncias, podendo o grupo trabalhar entre 1 e 4 instâncias conforme as regras de scaling.

---

### 5. Como o Auto Scaling saberia quando precisa aumentar ou diminuir a quantidade de instâncias?

Você já ouviu falar de **scaling policies** ou de métricas do CloudWatch nesse contexto?

**Minha resposta:**

> Métricas do CloudWatch sim que foi o assunto da aula passada. Muito provavelmente seria ativado a partir de notificações do CloudWatch a partir de determinados threshoulds em CPU ou memória por exemplo. Scaling policies já vi no trabalho por exemplo configuradas para desligar as máquinas nos finais de semana ou ligar em determinado horário e desligar a partir de determinado horário

**Correção:**

A resposta está correta em essência, com uma distinção importante.

Scaling policies podem utilizar métricas do CloudWatch para decidir quando aumentar ou reduzir a capacidade.

Por exemplo, uma política pode utilizar uma métrica de utilização de CPU para determinar uma ação de scaling.

Também existem políticas baseadas em programação, como **Scheduled Scaling**, que permitem definir alterações de capacidade para determinados horários.

O exemplo citado de ligar ou desligar máquinas em determinados horários está relacionado ao conceito de scaling baseado em agendamento.

**Ponto de atenção para a CLF-C02:**

Nem toda scaling policy depende diretamente de um threshold de CloudWatch.

Entre os mecanismos relevantes estão:

* Target Tracking;
* Step Scaling;
* Simple Scaling;
* Scheduled Scaling.

---

### 6. O que acontece com uma instância EC2 que pertence a um Auto Scaling Group e é encerrada?

Por exemplo, imagine que o ASG esteja configurado com `desired = 2` e uma das instâncias seja terminada.

**Minha resposta:**

> Ela será descartada do gerenciamento e uma nova será criada no lugar

**Correção:**

Correto em essência.

Se uma instância gerenciada pelo ASG for considerada unhealthy/encerrada e a capacidade desejada continuar sendo maior que a capacidade atual, o ASG pode iniciar uma nova instância para recuperar a capacidade desejada.

Esse comportamento foi demonstrado no hands-on.

**Ponto importante:** o ASG não simplesmente "ignora" a instância terminada. Ele monitora a capacidade e pode executar replacement para manter o estado desejado.

---

### 7. Qual a diferença entre Auto Scaling e Elastic Load Balancing?

Você já trabalhou ou teve contato com **ALB (Application Load Balancer)**?

**Minha resposta:**

> Auto Scaling trabalha em nível de máquinas enquanto ALB é um mecanismo de distribuir requisições entre essas instâncias. Ele é muito utilizado na frente de APIs para distribuir essas requisições e pode ser privada ou pública

**Correção:**

Correto.

Os dois serviços possuem responsabilidades diferentes:

```text
Auto Scaling
→ gerencia capacidade/quantidade de instâncias

ALB
→ distribui requisições entre destinos
```

Um ALB pode ser **internet-facing** ou **internal**, portanto pode atender aplicações públicas ou internas.

Um ambiente pode utilizar os dois:

```text
Cliente
   ↓
ALB
   ↓
EC2 / ECS
   ↑
Auto Scaling
```

O ALB distribui o tráfego enquanto o Auto Scaling ajusta a capacidade.

---

### 8. O que você entende por **Launch Template**?

Por que um Auto Scaling Group precisaria saber como uma nova instância EC2 deve ser criada?

**Minha resposta:**

> Por que um Auto Scaling Group precisaria saber como uma nova instância EC2 deve ser criada?
> Launch Template contém as definições de poder computacional da instância e precisa ser conhecido justamente para que o ASG consiga criar as novas instâncias necessárias com o template necessário

**Correção:**

Correto.

O Launch Template fornece as configurações necessárias para criar uma nova instância, como:

* AMI;
* instance type;
* Security Group;
* configurações de armazenamento;
* outras configurações de inicialização.

O modelo mental importante é:

```text
Launch Template
→ como criar a instância

Auto Scaling Group
→ quantas instâncias manter
```

---

### 9. O Auto Scaling necessariamente significa aumentar a quantidade de servidores?

Explique o que você entende por isso.

**Minha resposta:**

> Não, está relacionado conceito de elasticidade e pode ser tanto aumentar quanto reduzir

**Correção:**

Correto.

Auto Scaling não significa apenas aumentar a capacidade.

Ele pode executar:

```text
Scale out
→ aumentar capacidade

Scale in
→ reduzir capacidade
```

Essa capacidade de adaptação nos dois sentidos está relacionada ao conceito de elasticidade.

---

### 10. Pensando em custos, quais cuidados você teria ao criar um laboratório de Auto Scaling na sua conta AWS?

**Minha resposta:**

> Utilizar as máquinas mais econômicas possíveis, por exemplo as t3.nano e algo como: Min = 1 Desired = 2 e Max = 3

**Correção:**

A preocupação com custo está correta, especialmente considerando uma conta AWS pessoal.

A escolha de uma instância pequena como `t3.nano` está alinhada ao objetivo do laboratório.

Entretanto, para este laboratório foi adotado:

```text
Min = 1
Desired = 1
Max = 2
```

Isso permite demonstrar scale out para duas instâncias sem manter duas instâncias constantemente em execução.

Também é importante destruir os recursos temporários após os testes.

**Regra aplicada no laboratório:**

```text
create → test → document → destroy
```

---

## 2. Assuntos teóricos

### 2.1 Elasticidade

Elasticidade é a capacidade de ajustar a quantidade de recursos conforme a demanda.

No contexto do EC2 Auto Scaling:

```text
Demanda aumenta
      ↓
Scale out
      ↓
mais instâncias

Demanda diminui
      ↓
Scale in
      ↓
menos instâncias
```

Elasticidade é diferente de simplesmente possuir capacidade adicional disponível.

---

### 2.2 Auto Scaling Group

O **Auto Scaling Group (ASG)** é responsável por gerenciar um conjunto de instâncias EC2.

Ele trabalha principalmente com três valores:

```text
Minimum Capacity
Desired Capacity
Maximum Capacity
```

Exemplo:

```text
Min     = 1
Desired = 2
Max     = 4
```

Isso significa que o grupo procura normalmente manter duas instâncias, podendo trabalhar entre uma e quatro.

---

### 2.3 Desired Capacity

A `DesiredCapacity` representa a quantidade de instâncias que o ASG procura manter.

Se:

```text
Desired = 2
```

e uma instância for perdida, o ASG pode iniciar outra para recuperar a capacidade desejada.

---

### 2.4 Scale out

Scale out significa aumentar a capacidade.

Exemplo:

```text
1 EC2
  ↓
Scale out
  ↓
2 EC2
```

O aumento pode ser provocado por políticas baseadas em métricas, políticas agendadas ou alteração manual da capacidade desejada.

---

### 2.5 Scale in

Scale in significa reduzir a capacidade.

Exemplo:

```text
2 EC2
  ↓
Scale in
  ↓
1 EC2
```

O objetivo é reduzir recursos quando a capacidade adicional não é mais necessária.

---

### 2.6 Launch Template

O Launch Template define **como** uma instância deve ser criada.

Ele pode especificar:

* AMI;
* instance type;
* Security Group;
* armazenamento;
* configurações de inicialização.

O ASG utiliza essas informações para criar novas instâncias.

Modelo mental:

```text
Launch Template
      ↓
como criar

Auto Scaling Group
      ↓
quantas manter
```

---

### 2.7 Health checks e replacement

O ASG pode utilizar health checks para identificar instâncias que não estão saudáveis.

Quando uma instância é considerada unhealthy, o grupo pode:

```text
identificar instância unhealthy
          ↓
terminar/substituir
          ↓
criar nova instância
          ↓
recuperar capacidade desejada
```

Esse comportamento foi demonstrado diretamente neste laboratório.

---

### 2.8 Availability Zones

O ASG pode utilizar subnets de diferentes Availability Zones.

Neste laboratório foram utilizadas:

```text
sa-east-1a
sa-east-1b
```

Isso permitiu observar as instâncias distribuídas entre duas AZs durante o scale out.

A utilização de múltiplas AZs contribui para disponibilidade, mas **Auto Scaling e alta disponibilidade não são sinônimos**.

---

### 2.9 Auto Scaling e CloudWatch

CloudWatch fornece métricas que podem ser utilizadas por políticas de Auto Scaling.

Exemplo conceitual:

```text
EC2
 ↓
CloudWatch Metric
 ↓
Scaling Policy
 ↓
Auto Scaling Group
 ↓
Scale out / Scale in
```

Neste módulo não foi criada uma scaling policy baseada em métrica. O comportamento principal do ASG foi demonstrado através da alteração manual da `DesiredCapacity`, evitando adicionar complexidade e recursos desnecessários ao laboratório.

---

### 2.10 Auto Scaling x Load Balancer

Auto Scaling e Load Balancer possuem responsabilidades diferentes.

```text
                ┌── EC2
ALB ────────────┼── EC2
                └── EC2

       distribuição de tráfego

Auto Scaling
       ↓
gerenciamento da quantidade
de instâncias
```

Um ambiente de produção frequentemente utiliza os dois, mas eles resolvem problemas diferentes.

---

### 2.11 Auto Scaling x Spot

Spot Instance não é uma funcionalidade equivalente ao Auto Scaling.

```text
Auto Scaling
→ gerenciamento de capacidade

Spot
→ modelo de aquisição de capacidade EC2
```

Os conceitos podem aparecer juntos, mas não devem ser confundidos.

---

## 3. Arquitetura

```text
Existing VPC
    |
    +-- Public subnet A (sa-east-1a)
    |
    +-- Public subnet B (sa-east-1b)
              |
              v
       Launch Template
       - Amazon Linux 2023
       - t3.nano
       - Security Group
              |
              v
       Auto Scaling Group
       Min = 1
       Desired = 1
       Max = 2
              |
        +-----+-----+
        |           |
      EC2         EC2
   sa-east-1a  sa-east-1b
```

Não foram utilizados:

* Application Load Balancer;
* NAT Gateway;
* scaling policy baseada em CloudWatch;
* outros componentes adicionais.

Esses recursos não eram necessários para demonstrar os conceitos centrais deste módulo.

---

## 4. Recursos criados

O Terraform criou:

* Security Group `aws-cloud-practitioner-lab-autoscaling`;
* Launch Template `aws-cloud-practitioner-lab-autoscaling`;
* Auto Scaling Group `aws-cloud-practitioner-lab`.

A VPC e as subnets utilizadas já existiam e foram descobertas pelo Terraform através de data sources.

A infraestrutura existente utilizada foi:

```text
VPC: 10.0.0.0/16

public-a:
10.0.1.0/24
sa-east-1a

public-b:
10.0.2.0/24
sa-east-1b
```

---

## 5. Configuração

| Configuração              |             Valor |
| ------------------------- | ----------------: |
| Minimum                   |                 1 |
| Desired                   |                 1 |
| Maximum                   |                 2 |
| Instance type             |         `t3.nano` |
| AMI                       | Amazon Linux 2023 |
| Health check              |               EC2 |
| Health check grace period |       60 segundos |
| Region                    |       `sa-east-1` |

A escolha de `t3.nano` e `Desired = 1` foi feita considerando o controle de custos do laboratório.

---

## 6. IAM e Least Privilege

Foi criada uma policy específica para este módulo:

```text
aws-cloud-practitioner-lab-autoscaling
```

A policy concede somente as ações necessárias para administrar os recursos utilizados no laboratório.

Durante a implementação foram identificadas necessidades adicionais de permissão através dos erros `AccessDenied`.

### Service-Linked Role

A policy inclui:

```text
iam:CreateServiceLinkedRole
```

restrita ao serviço:

```text
autoscaling.amazonaws.com
```

Isso permite reproduzir o laboratório em uma conta onde a service-linked role do Auto Scaling ainda não exista.

A role utilizada pelo Auto Scaling é:

```text
AWSServiceRoleForAutoScaling
```

No ambiente utilizado neste laboratório, essa role já existia.

### SetDesiredCapacity

Durante os testes, a tentativa de executar:

```bash
aws autoscaling set-desired-capacity
```

retornou `AccessDenied`.

A permissão:

```text
autoscaling:SetDesiredCapacity
```

foi então adicionada à policy específica do módulo.

Essa alteração demonstrou, na prática, o processo de refinamento de uma policy de least privilege com base nas operações realmente necessárias.

---

## 7. Hands-on

### 7.1 Criar

O Terraform foi utilizado para criar:

```text
Security Group
      ↓
Launch Template
      ↓
Auto Scaling Group
      ↓
EC2
```

A configuração utilizou a VPC existente e as duas subnets públicas descobertas através de data sources.

---

### 7.2 Testar capacidade inicial

Após o `terraform apply`, o ASG iniciou com:

```text
Min     = 1
Desired = 1
Max     = 2
```

Uma instância EC2 foi criada automaticamente.

---

### 7.3 Scale out

A capacidade desejada foi alterada de 1 para 2:

```bash
aws autoscaling set-desired-capacity \
  --auto-scaling-group-name aws-cloud-practitioner-lab \
  --desired-capacity 2 \
  --region sa-east-1
```

O ASG criou uma segunda instância.

As duas instâncias ficaram:

```text
Healthy
InService
```

e foram distribuídas entre:

```text
sa-east-1a
sa-east-1b
```

Isso demonstrou o scale out.

---

### 7.4 Replacement de instância

Uma das instâncias foi terminada manualmente:

```bash
aws ec2 terminate-instances \
  --instance-ids <instance-id> \
  --region sa-east-1
```

O Auto Scaling detectou a instância como unhealthy e iniciou uma nova instância.

A atividade registrada pelo ASG indicou que a nova instância foi lançada porque uma instância unhealthy precisava ser substituída.

Esse comportamento demonstrou o mecanismo de replacement/auto healing.

---

### 7.5 Scale in

A capacidade desejada foi reduzida de 2 para 1:

```bash
aws autoscaling set-desired-capacity \
  --auto-scaling-group-name aws-cloud-practitioner-lab \
  --desired-capacity 1 \
  --region sa-east-1
```

Uma das instâncias entrou em estado `Terminating`.

Depois da convergência do ASG, permaneceu uma instância `InService`, correspondente à capacidade desejada.

---

### 7.6 Observar atividades do ASG

As atividades foram verificadas com:

```bash
aws autoscaling describe-scaling-activities \
  --auto-scaling-group-name aws-cloud-practitioner-lab \
  --region sa-east-1
```

Isso permitiu observar o histórico de:

* lançamento de instâncias;
* substituição de instâncias unhealthy;
* alterações de capacidade.

---

### 7.7 Validar o estado do Terraform

Após os experimentos foi executado:

```bash
terraform plan
```

Resultado:

```text
No changes. Your infrastructure matches the configuration.
```

Isso confirmou que as alterações realizadas diretamente pela AWS CLI não deixaram divergências na configuração declarada pelo Terraform.

---

## 8. Principais aprendizados

### Launch Template x Auto Scaling Group

O Launch Template define **como** uma instância deve ser criada.

O Auto Scaling Group define **quantas** instâncias devem existir e mantém a capacidade dentro dos limites configurados.

```text
Launch Template
      ↓
como criar

ASG
      ↓
quantas manter
```

### Scale out x Scale in

```text
Scale out → aumenta instâncias
Scale in  → reduz instâncias
```

### Desired Capacity

`DesiredCapacity` representa a quantidade de instâncias que o ASG procura manter em condições normais.

### Auto healing

O Auto Scaling pode substituir instâncias consideradas unhealthy para recuperar a capacidade desejada.

### Elasticidade x alta disponibilidade

Elasticidade está relacionada à adaptação da capacidade à demanda.

Múltiplas Availability Zones podem contribuir para disponibilidade, mas isso não transforma Auto Scaling em sinônimo de alta disponibilidade.

### Auto Scaling x CloudWatch

CloudWatch pode fornecer métricas utilizadas por scaling policies, mas Auto Scaling e CloudWatch são serviços com responsabilidades diferentes.

---

## 9. Controle de custos

O laboratório foi deliberadamente simplificado para reduzir custos.

Foram utilizados:

* uma única região: `sa-east-1`;
* instância `t3.nano`;
* `Desired = 1`;
* `Maximum = 2`;
* somente duas subnets públicas existentes;
* nenhum NAT Gateway;
* nenhum Application Load Balancer;
* nenhuma scaling policy adicional.

A infraestrutura foi destruída após os testes.

Regra aplicada:

```text
create → test → document → destroy
```

---

## 10. Cleanup

Os recursos específicos do módulo foram destruídos com:

```bash
terraform destroy
```

O Auto Scaling Group, Launch Template, Security Group e instâncias EC2 temporárias foram removidos.

Durante o cleanup, o Terraform encontrou uma limitação de permissão ao tentar executar:

```text
iam:DetachUserPolicy
```

A permissão não foi adicionada à policy do próprio laboratório porque isso permitiria ao usuário remover sua própria policy e não era necessária para o objetivo do módulo.

A associação da policy e a própria policy foram removidas utilizando temporariamente uma identidade administrativa.

Após isso, o `terraform destroy` foi concluído com sucesso.

A VPC e suas subnets existentes não pertenciam a este módulo e não foram destruídas.

---

## 11. Comandos úteis

Consultar o ASG:

```bash
aws autoscaling describe-auto-scaling-groups \
  --auto-scaling-group-names aws-cloud-practitioner-lab \
  --region sa-east-1
```

Consultar atividades:

```bash
aws autoscaling describe-scaling-activities \
  --auto-scaling-group-name aws-cloud-practitioner-lab \
  --region sa-east-1
```

Alterar capacidade desejada:

```bash
aws autoscaling set-desired-capacity \
  --auto-scaling-group-name aws-cloud-practitioner-lab \
  --desired-capacity 2 \
  --region sa-east-1
```

---

## 12. Pontos de atenção para a CLF-C02

* **Scale out** aumenta a capacidade.
* **Scale in** reduz a capacidade.
* **Elasticity** significa adaptar a capacidade à demanda.
* **Desired Capacity** é a capacidade que o ASG procura manter.
* **Minimum Capacity** define o limite inferior.
* **Maximum Capacity** define o limite superior.
* **Launch Template** define como as instâncias serão criadas.
* **Auto Scaling Group** gerencia a quantidade de instâncias.
* Auto Scaling pode substituir instâncias unhealthy.
* CloudWatch pode fornecer métricas para scaling policies.
* Auto Scaling não é um Load Balancer.
* Spot Instance não é sinônimo de Auto Scaling.
* Múltiplas Availability Zones podem contribuir para disponibilidade.

---

## Conclusão

O laboratório demonstrou, de forma prática, como o EC2 Auto Scaling mantém uma capacidade computacional desejada, realiza scale out e scale in e substitui automaticamente instâncias que deixam de estar saudáveis.

O módulo também reforçou a aplicação de IAM least privilege, Terraform declarativo, uso de múltiplas Availability Zones e observação do comportamento da infraestrutura através da AWS CLI.

**Day 12 — Auto Scaling: concluído.**
