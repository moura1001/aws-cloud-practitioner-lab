# 03 — IAM (Identity and Access Management)

## 🎯 Objetivo

Estudar os fundamentos do AWS Identity and Access Management (IAM), entendendo como a AWS controla **quem pode acessar a conta, quais ações podem ser executadas e quais recursos podem ser acessados**.

Neste laboratório também foi realizada a integração entre **IAM, AWS CLI e Terraform**, além de testes práticos do princípio do menor privilégio.

---

## 🔐 Authentication × Authorization

### Authentication

Authentication (autenticação) responde:

> **Quem é você?**

É o processo de verificar a identidade de quem está tentando acessar um sistema.

### Authorization

Authorization (autorização) responde:

> **O que você pode fazer?**

Depois que a identidade é autenticada, a AWS verifica quais ações essa identidade está autorizada a executar.

```text
Authentication
      ↓
"Quem é você?"
      ↓
Authorization
      ↓
"O que você pode fazer?"
```

### Exemplo

Uma pessoa consegue se autenticar na AWS, mas recebe `AccessDenied` ao tentar acessar um recurso do S3.

Nesse caso:

```text
Autenticação → ✅
Autorização  → ❌
```

---

# 👤 IAM User

Um **IAM User** representa uma identidade persistente dentro de uma conta AWS.

Um usuário pode possuir credenciais e permissões associadas a ele.

Neste laboratório foi criado:

```text
aws-cloud-practitioner-lab
```

O usuário foi utilizado para realizar chamadas à AWS por meio do AWS CLI e do Terraform.

---

# 📜 IAM Policy

Uma **IAM Policy** define permissões.

Uma policy utiliza elementos como:

```text
Effect
Action
Resource
```

Exemplo simplificado:

```json
{
  "Effect": "Allow",
  "Action": "sts:GetCallerIdentity",
  "Resource": "*"
}
```

Nesse caso:

* `Effect` → permite a ação;
* `Action` → `sts:GetCallerIdentity`;
* `Resource` → recurso ao qual a permissão se aplica.

---

# 🔒 Least Privilege

O princípio do **menor privilégio (Least Privilege)** determina que uma identidade deve receber somente as permissões necessárias para realizar determinada tarefa.

Por exemplo, conceder:

```text
s3:*
```

quando uma aplicação necessita somente de:

```text
s3:GetObject
```

concede permissões maiores do que o necessário.

O objetivo é reduzir a superfície de exposição caso uma credencial seja comprometida.

---

# 👑 Root User

O **Root User** possui privilégios extremamente amplos dentro da conta AWS.

Por isso:

* deve ser utilizado somente quando necessário;
* não deve ser utilizado para tarefas rotineiras;
* deve possuir MFA habilitado;
* não devem ser criadas Access Keys para uso cotidiano do Root.

Neste laboratório, o MFA do Root já estava habilitado antes do início do hands-on.

---

# 🔑 MFA

O **Multi-Factor Authentication (MFA)** adiciona uma camada adicional ao processo de autenticação.

De forma simplificada:

```text
Senha
  +
Segundo fator
  ↓
Autenticação
```

Mesmo que a senha seja comprometida, o segundo fator adiciona uma barreira adicional ao acesso.

---

# 🔑 Access Key

Access Keys são credenciais utilizadas para acesso programático à AWS.

Neste laboratório foi criada uma Access Key para o usuário:

```text
aws-cloud-practitioner-lab
```

A credencial foi configurada localmente por meio do:

```bash
aws configure
```

As credenciais ficam fora do repositório Git:

```text
~/.aws/credentials
```

### ⚠️ Segurança

Access Keys não devem:

* ser colocadas diretamente no código;
* ser armazenadas no Terraform;
* ser commitadas no Git;
* ser publicadas no GitHub.

O projeto possui um `.gitignore` para impedir que arquivos como `terraform.tfstate` sejam versionados.

---

# 🧑‍💻 AWS CLI + Terraform

A autenticação do Terraform foi validada utilizando o IAM User.

O comando:

```bash
aws sts get-caller-identity
```

permitiu verificar a identidade utilizada pelo AWS CLI.

O Terraform também consultou a identidade por meio de:

```hcl
data "aws_caller_identity" "current" {}
```

O resultado confirmou o IAM User:

```text
arn:aws:iam::ACCOUNT_ID:user/aws-cloud-practitioner-lab
```

Nenhum recurso de infraestrutura foi criado durante esse teste.

---

# 🧪 Teste de Least Privilege

Inicialmente, o usuário recebeu temporariamente:

```text
AdministratorAccess
```

Essa policy fornece:

```json
{
  "Effect": "Allow",
  "Action": "*",
  "Resource": "*"
}
```

Ela foi utilizada apenas para demonstrar o conceito de acesso administrativo amplo.

Depois, o `AdministratorAccess` foi removido e substituído por uma policy específica:

```text
aws-cloud-practitioner-lab-read-identity
```

Com a permissão:

```json
{
  "Effect": "Allow",
  "Action": [
    "sts:GetCallerIdentity"
  ],
  "Resource": "*"
}
```

### Resultado

A consulta da identidade continuou funcionando:

```text
sts:GetCallerIdentity → ✅
```

Quando o Terraform tentou consultar informações do IAM User, a operação exigia:

```text
iam:GetUser
```

Como essa ação não estava autorizada, a AWS retornou:

```text
403 AccessDenied
```

Esse teste demonstrou na prática que:

```text
Policy
   ↓
Action permitida?
   ├── Sim → operação permitida
   └── Não → AccessDenied
```

---

# 🧑‍🚀 IAM Role

Uma **IAM Role** é uma identidade que pode ser assumida por uma entidade autorizada.

Diferentemente de uma identidade baseada em credenciais permanentes, uma Role normalmente fornece **credenciais temporárias** quando é assumida.

Neste laboratório foi criada:

```text
aws-cloud-practitioner-lab-ec2-s3-read
```

---

## Trust Policy

A **Trust Policy** define:

> **Quem pode assumir a Role?**

A Role criada possui uma relação de confiança com o serviço EC2.

Conceitualmente:

```text
EC2
 ↓
pode assumir
 ↓
IAM Role
```

---

## Permissions Policy

A **Permissions Policy** define:

> **O que uma identidade pode fazer depois de assumir a Role?**

A Role do laboratório recebeu:

```text
AmazonS3ReadOnlyAccess
```

Assim:

```text
                 IAM Role
                    │
          ┌─────────┴─────────┐
          ↓                   ↓
    Trust Policy       Permissions Policy
          ↓                   ↓
     EC2 pode          S3 leitura
     assumir            permitida
```

### Trust × Permissions

| Tipo               | Pergunta                            |
| ------------------ | ----------------------------------- |
| Trust Policy       | Quem pode assumir a Role?           |
| Permissions Policy | O que pode fazer depois de assumir? |

---

# 🔄 User × Role

| IAM User                                   | IAM Role                                                  |
| ------------------------------------------ | --------------------------------------------------------- |
| Identidade persistente                     | Identidade assumível                                      |
| Pode utilizar credenciais próprias         | Normalmente utiliza credenciais temporárias               |
| Pode representar um usuário/identidade     | Pode ser assumida por serviços, aplicações ou identidades |
| Utilizada neste laboratório pelo Terraform | Será utilizada posteriormente com EC2                     |

Um exemplo de arquitetura recomendada:

```text
EC2
 │
 │ assume Role
 ▼
IAM Role
 │
 │ permissions
 ▼
S3
```

Isso evita colocar Access Keys permanentes diretamente dentro da aplicação.

---

# 🧪 Terraform

O laboratório utiliza o seguinte `main.tf`:

```hcl
terraform {
  required_providers {
    aws = {
      source = "hashicorp/aws"
    }
  }
}

provider "aws" {
  region = "sa-east-1"
}

data "aws_caller_identity" "current" {}

data "aws_region" "current" {}

output "account_id" {
  value = data.aws_caller_identity.current.account_id
}

output "arn" {
  value = data.aws_caller_identity.current.arn
}

output "region" {
  value = data.aws_region.current.region
}
```

O Terraform não cria infraestrutura nesse exemplo.

Ele apenas consulta informações da conta e da identidade autenticada.

Resultado:

```text
Resources: 0 added
Resources: 0 changed
Resources: 0 destroyed
```

---

# 📚 Exercícios e aprendizados

### 1. Authentication × Authorization

Qual é a diferença entre autenticação e autorização?

**Minha resposta:**

> Autenticação é o conceito de gerenciar o acesso de quem pode acessar determinado sistema enquanto que autorização é o conceito do que a pessoa autorizada a entrar no sistema pode ter acesso a qual conjunto de recursos/operações que o sistema disponibiliza

**Conclusão:**

A distinção fundamental é:

```text
Authentication → Quem é você?
Authorization  → O que você pode fazer?
```

---

### 2. Acesso ao S3

Se uma pessoa consegue entrar na AWS, mas não consegue acessar um bucket S3, qual dos dois conceitos está relacionado ao problema?

**Minha resposta:**

> Trata-se de um problema de autorização

**Conclusão:**

Se a pessoa consegue entrar na AWS, mas não consegue acessar um recurso, ela foi autenticada, mas não possui a autorização necessária para aquela operação.

---

### 3. IAM Policy

O que você entende por IAM Policy?

**Minha resposta:**

> IAM Policy é o mecanismo de descrever a parte de autorização dentro para usuários dentro da AWS, podendo ser editada via JSON ou interface gráfica

**Conclusão:**

Uma IAM Policy define permissões e pode ser representada por documentos JSON.

---

### 4. IAM User × IAM Role

Qual é a diferença, em termos gerais, entre um IAM User e uma IAM Role?

**Minha resposta:**

> IAM User refere-se a um acesso/utilização de usuários humanos a AWS enquanto o IAM Role é relacionado a acesso/utilização outros sistemas

**Correção:**

A ideia inicial estava próxima, mas uma Role não é exclusiva de sistemas.

```text
IAM User → identidade persistente
IAM Role → identidade que pode ser assumida
```

Uma Role pode ser assumida por serviços AWS, aplicações, usuários ou outras identidades autorizadas.

---

### 5. Root User

Qual é a diferença, em termos gerais, entre um IAM User e uma IAM Role?

**Minha resposta:**

> Porque o Root User tem acesso a tudo da conta AWS, então se ela for comprometida em relação a segurança/invasão poderá haver diversos problemas de autorização em decorrência do usuário que conseguiu acessar a conta indevidamente

**Conclusão:**

O Root possui privilégios extremamente amplos e, por isso, deve ser protegido com MFA e utilizado somente quando necessário.

---

### 6. Least Privilege

Qual é a diferença, em termos gerais, entre um IAM User e uma IAM Role?

**Minha resposta:**

> Tem a ver com o conceito de autorização e está relacionado com dar apenas o mínimo de acesso/privilégios necessário para a realizar determinada tarefa

**Conclusão:**

O princípio está correto: conceder somente as permissões necessárias.

---

### 7. MFA

Para que serve o MFA?

**Minha resposta:**

> MFA é uma camada extra de proteção no processo de autenticação de sistemas

**Conclusão:**

O MFA adiciona um segundo fator ao processo de autenticação, aumentando a segurança da conta.

---

### 8. Trust Policy × Permissions Policy

**Minha resposta:**

> Trust Policy define quem pode assumir a Role

> Permissions Policy define quais ações podem ser executadas

**Conclusão:**

```text
Trust Policy
→ Quem pode assumir?

Permissions Policy
→ O que pode fazer?
```

---

### 9. Role x Access Key

Por que usar uma Role em uma EC2 pode ser mais seguro do que colocar uma Access Key diretamente na aplicação?

**Minha resposta:**

> Porque se a credencial vazar, ela pode ser utilizada para realizar operações indefinidamente fora da AWS

**Conclusão:**

Uma Access Key é uma credencial permanente. Se ela for exposta ou vazar, alguém poderá utilizá-la para realizar operações na AWS enquanto ela continuar válida, inclusive fora da EC2.

Ao utilizar uma **IAM Role**, a EC2 recebe **credenciais temporárias** para acessar os recursos permitidos pela Role. Essas credenciais possuem **prazo de validade** e são renovadas automaticamente, reduzindo o risco associado ao vazamento de uma credencial permanente.

**Resposta resumida para a prova:**

> Role → credenciais temporárias e renovadas automaticamente.

> Access Key → credencial permanente que pode ser utilizada até ser revogada.

---

# 🎯 Principais pontos para o CLF-C02

```text
Authentication
→ Quem é você?

Authorization
→ O que você pode fazer?

IAM User
→ Identidade persistente

IAM Policy
→ Define permissões

Least Privilege
→ Somente permissões necessárias

MFA
→ Segundo fator de autenticação

IAM Role
→ Identidade assumível

Trust Policy
→ Quem pode assumir a Role?

Permissions Policy
→ O que pode fazer?

AccessDenied
→ A identidade não possui a permissão necessária
```

## ✅ Resultado

* IAM User criado
* Access Key configurada localmente
* AWS CLI autenticado
* Terraform autenticado
* Least Privilege testado
* `AccessDenied` reproduzido
* IAM Role criada
* Trust Policy analisada
* Permissions Policy analisada
* MFA do Root confirmado
* `AdministratorAccess` removido do usuário do laboratório
* Nenhum recurso de infraestrutura pago criado

**Status: Dia 3 — IAM concluído.**
