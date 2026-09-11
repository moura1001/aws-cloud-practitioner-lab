# AWS Cloud Practitioner Lab — Contexto Mestre

## 1. Objetivo do projeto

Este repositório é um laboratório prático criado para estudar e consolidar os conhecimentos necessários para a certificação **AWS Certified Cloud Practitioner (CLF-C02)**.

O objetivo não é apenas decorar conceitos para a prova. O estudo deve combinar:

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
| 04 | VPC                   | —        | ⬜ Not started |
| 05 | EC2                   | —        | ⬜ Not started |
| 06 | S3                    | —        | ⬜ Not started |
| 07 | RDS                   | —        | ⬜ Not started |
| 08 | ECR                   | —        | ⬜ Not started |
| 09 | ECS                   | —        | ⬜ Not started |
| 10 | SQS                   | —        | ⬜ Not started |
| 11 | CloudWatch            | —        | ⬜ Not started |
| 12 | Auto Scaling          | —        | ⬜ Not started |
| 13 | Final Project         | —        | ⬜ Not started |

O próximo módulo a ser iniciado é **Day 4 — VPC**.

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

As perguntas devem ser respondidas pelo aluno antes de receber a explicação completa.

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

Não assumir que o aluno quer apenas copiar comandos.

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

Exemplo:

A IAM Role criada no Day 3 deve permanecer porque será reutilizada no laboratório de EC2.

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

O aluno utiliza AWS CLI configurado localmente para autenticação.

---

## 7. Segurança

Nunca solicitar ao aluno:

* Access Key;
* Secret Access Key;
* senha;
* token;
* credenciais;
* dados sensíveis.

Quando for necessário mostrar uma saída que contenha informações sensíveis, utilizar placeholders como:

```text
ACCOUNT_ID
```

ou:

```text
<ACCOUNT_ID>
```

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

Exemplos:

```bash
git status
git add .
git commit -m "docs: complete iam study and lab"
git push
```

Não criar commits duplicados sem necessidade. Se alterações do módulo já tiverem sido parcialmente commitadas, verificar o estado atual antes de criar outro commit.

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
   └── RDS
        ↓
CloudWatch

Docker → ECR → ECS
```

A relação entre os serviços deve ser apresentada conforme eles forem estudados.

Exemplo:

IAM Role estudada no Day 3 será reutilizada no EC2.

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

A Role deve permanecer para ser utilizada posteriormente no módulo de EC2.

Nenhum recurso de infraestrutura com cobrança foi criado no Day 3.

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

Se esta conversa ficar muito longa, uma nova conversa pode ser iniciada usando este arquivo como contexto.

Mensagem recomendada:

> Estou continuando meu projeto `aws-cloud-practitioner-lab` em uma nova conversa.
>
> Leia o `docs/study-context.md` e o `docs/learning-log.md` do projeto para recuperar o contexto do curso.
>
> Mantenha exatamente a metodologia definida no `study-context.md`.
>
> O último módulo concluído é o Day 3 — IAM.
>
> Quero continuar pelo próximo módulo, Day 4 — VPC.
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

## 16. Estado atual das credenciais e IAM do laboratório

### IAM User utilizado pelo laboratório

O usuário IAM utilizado para executar comandos AWS CLI e Terraform é:

```text
aws-cloud-practitioner-lab
```

O Terraform utiliza as credenciais configuradas localmente no AWS CLI para esse usuário.

Portanto, quando o Terraform executa uma operação como:

```bash
terraform apply
```

as permissões disponíveis são as permissões do usuário:

```text
aws-cloud-practitioner-lab
```

e **não** as permissões do Root User ou de qualquer outra identidade IAM.

### Estado das permissões

Durante o Day 3 — IAM, o usuário foi inicialmente configurado com:

```text
AdministratorAccess
```

Isso permitiu realizar a configuração inicial do laboratório.

Posteriormente, `AdministratorAccess` foi removido para demonstrar o princípio de Least Privilege.

Foi criada e mantida a policy personalizada:

```text
aws-cloud-practitioner-lab-read-identity
```

com a permissão:

```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "sts:GetCallerIdentity"
      ],
      "Resource": "*"
    }
  ]
}
```

Essa policy permite consultar a identidade atual, mas **não permite criar ou modificar recursos de infraestrutura**.

Por exemplo, o usuário consegue executar consultas como:

```bash
aws sts get-caller-identity
```

e o Terraform consegue consultar a identidade e a região, mas uma tentativa de criar uma VPC resulta em `403 AccessDenied` porque o usuário não possui permissões como:

```text
ec2:CreateVpc
ec2:CreateSubnet
ec2:CreateRouteTable
ec2:CreateInternetGateway
...
```

### Regra para os próximos hands-on

Antes de executar um `terraform apply` que crie ou modifique recursos AWS, verificar se o usuário utilizado pelo Terraform possui as permissões necessárias.

Não assumir que o fato de o Terraform estar funcionando significa que ele possui permissões para criar qualquer recurso.

O fluxo esperado deve ser:

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

Quando um novo módulo exigir permissões adicionais, devemos decidir conscientemente entre:

1. criar/adicionar uma policy específica para o laboratório;
2. utilizar permissões administrativas temporariamente, quando isso fizer sentido didático;
3. remover/reduzir as permissões novamente após o exercício.

A decisão deve ser explicada antes da execução.

### AdministratorAccess

`AdministratorAccess` pode ser utilizado temporariamente para determinados exercícios, mas não deve ser tratado como a configuração permanente do usuário do laboratório.

Se for necessário reativá-lo para um exercício, registrar isso no aprendizado e, ao terminar, avaliar a remoção ou substituição pelas permissões mínimas necessárias.

**Importante:** não executar `terraform apply` esperando que uma policy de apenas `sts:GetCallerIdentity` consiga criar recursos como VPC, EC2, S3, RDS etc.

### IAM Role criada no Day 3

Também existe a Role:

```text
aws-cloud-practitioner-lab-ec2-s3-read
```

Essa Role possui:

```text
Trust Policy:
EC2 → pode assumir a Role

Permissions Policy:
AmazonS3ReadOnlyAccess
```

Essa Role **não concede permissões ao usuário `aws-cloud-practitioner-lab`**.

Ela será utilizada posteriormente no módulo de EC2 para demonstrar o uso de credenciais temporárias:

```text
EC2
 ↓
assume Role
 ↓
credenciais temporárias
 ↓
permissões da Role
 ↓
S3
```

### Regra de segurança

Nunca colocar Access Key ou Secret Access Key diretamente no Terraform, código ou Git.

O Terraform deve continuar utilizando as credenciais configuradas pelo AWS CLI/ambiente local.

