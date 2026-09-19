# 08 — ECR (Elastic Container Registry)

## Status

**Concluído**

## Objetivo

Estudar o Amazon Elastic Container Registry (ECR) como serviço de registry para imagens de containers e compreender sua relação com Docker, ECS e os demais componentes da arquitetura AWS.

O hands-on teve como objetivos:

* compreender Image, Container, Registry, Repository, Tag e Digest;
* criar um ECR Repository utilizando Terraform;
* aplicar Least Privilege com uma policy IAM específica;
* criar uma imagem Docker;
* autenticar o Docker no ECR;
* realizar push da imagem;
* validar a imagem armazenada no ECR;
* remover a imagem localmente;
* realizar pull da imagem diretamente do ECR;
* destruir o repository ao final do laboratório.

---

## Diagnóstico inicial

### 1. O que é o Amazon ECR e qual problema ele resolve?

**Minha resposta:**

> O ECR é um repositório de imagens Docker que são usados nos deploys dos containeres do ECS

**Avaliação:** Correta.

O Amazon ECR é um serviço gerenciado de container registry utilizado para armazenar, gerenciar e distribuir imagens de containers.

O ECR é frequentemente utilizado com ECS, mas não é exclusivo dele.

---

### 2. Qual destas opções descreve melhor o ECR?

A. Serviço de Object Storage
B. Container Registry
C. Serviço de banco relacional
D. Serviço de máquinas virtuais

**Minha resposta:**

> B

**Avaliação:** Correta.

O ECR é um **Container Registry**.

---

### 3. Qual é a diferença entre uma Docker Image e um Container?

**Minha resposta:**

> A imagem do container é quem define o template (SO, serviços, aplicações, etc) e o container é quem executará esse template a partir de seus recursos de cpu e memória

**Avaliação:** Parcialmente correta.

A ideia principal está correta.

A Image é o artefato/template utilizado para criar containers. O Container é uma instância em execução baseada nessa imagem.

Uma observação importante: uma container image não funciona exatamente como uma VM. Ela não precisa carregar um kernel completo do sistema operacional; containers compartilham o kernel do ambiente de execução.

---

### 4. Onde uma Docker Image pode ser armazenada antes de ser utilizada por um serviço como ECS?

**Minha resposta:**

> Ela é armazenada no ECR

**Avaliação:** Correta.

O ECR pode armazenar as imagens que posteriormente serão utilizadas por serviços como ECS.

Outros registries também existem, como Docker Hub e GitHub Container Registry.

---

### 5. O que significa Container Registry?

**Minha resposta:**

> Não sei exatamente mas parece ser um protocolo padrão de criação e armazenamento das imagens Docker que justamente o ECR implementa/segue

**Avaliação:** Incorreta.

Registry não é um protocolo.

Um **Container Registry** é um serviço ou sistema responsável por armazenar e distribuir imagens de containers.

Exemplo:

```text
Docker
  ↓
Image
  ↓
Container Registry
  ↓
Push / Pull
```

O ECR é a implementação gerenciada pela AWS desse conceito.

---

### 6. Qual é a diferença entre ECR e ECS?

**Minha resposta:**

> ECR é onde as imagens Docker são guardadas e que serão utilizados pelos containeres gerenciados pelo ECS

**Avaliação:** Correta.

Essa é uma das relações mais importantes do módulo:

```text
ECR
↓
armazena/distribui imagens

ECS
↓
gerencia a execução dos containers
```

O ECR não executa os containers.

---

### 7. Qual é a função de um Repository dentro do ECR?

**Minha resposta:**

> O ECR é responsável por gerenciar e disponibilizar as diferentes versões das imagens Docker

**Avaliação:** Correta, com pequeno ajuste.

O **Repository** é o espaço lógico dentro do registry onde imagens relacionadas são armazenadas.

Um repository pode conter diferentes versões/referências da mesma aplicação.

---

### 8. O que é um ECR Repository?

**Minha resposta:**

> Um local de armazenamento das imagens Docker

**Avaliação:** Correta.

Uma definição mais precisa seria:

> Um espaço lógico dentro do ECR destinado ao armazenamento e gerenciamento de imagens relacionadas.

---

### 9. O que é uma Tag de uma imagem Docker?

**Minha resposta:**

> Uma tag seria uma informação para identificação única e rastreável de determinada imagem Docker "compilada"

**Avaliação:** Parcialmente correta.

A tag é uma referência legível utilizada para identificar uma imagem, por exemplo:

```text
1.0
latest
production
dev
```

Entretanto, a tag não deve ser considerada um identificador imutável.

O **Digest** identifica o conteúdo da imagem de maneira baseada no conteúdo.

Importante para a CLF-C02:

```text
Tag
→ referência legível

Digest
→ identificação baseada no conteúdo
```

Além disso, `latest` não significa necessariamente "a imagem mais nova".

---

### 10. Uma imagem armazenada no ECR é um container em execução?

**Minha resposta:**

> Não, significa apenas que ela pode ser utilizada na subida dos containeres como template do que será executado dentro da máquina

**Avaliação:** Correta.

A Image é um artefato.

O Container é criado e executado por um ambiente de runtime, como ECS/Fargate ou ECS sobre EC2.

```text
Image
  ↓
Container
  ↓
Execution
```

---

### 11. Por que um ECR Repository normalmente não deve ser público sem uma necessidade específica?

**Minha resposta:**

> Porque o ECR tem tanto uma cobrança por armazenamento quanto expor as imagens publicamente poderia ter risco de exposição de segredos de negócio através de engenharia reversa, etc. Quem limita isso deve ser o IAM

**Avaliação:** Correta.

Existem dois aspectos diferentes:

**Segurança:** uma imagem pode conter código, dependências ou informações que não deveriam ser publicamente acessíveis.

**Custos:** armazenamento e transferência podem gerar custos.

O IAM participa do controle de acesso às operações do ECR.

Também é importante nunca colocar secrets diretamente dentro de uma imagem Docker.

---

### 12. Onde o ECR se encaixa em uma arquitetura com EC2/ECS?

**Minha resposta:**

> Se encaixaria antes do EC2 pois se trata de imagens que são executadas em containeres que rodam em máquinas EC2

**Avaliação:** Parcialmente correta.

A relação conceitual está correta, mas o ECR não depende diretamente de EC2.

O fluxo pode ser:

```text
Docker Image
    ↓
ECR
    ↓
ECS
    ↓
Fargate ou EC2
    ↓
Container
```

Com Fargate, por exemplo, não precisamos administrar diretamente as instâncias EC2.

---

# Conceitos estudados

## ECR

Amazon ECR é um serviço gerenciado de **Container Registry**.

Sua função principal é:

```text
armazenar
gerenciar
distribuir
```

imagens de containers.

---

## Image x Container

```text
Image
↓
artefato/template

Container
↓
instância em execução da Image
```

Uma imagem pode ser utilizada para criar múltiplos containers.

---

## Registry x Repository

```text
ECR
│
├── Repository A
│   ├── Image: 1.0
│   └── Image: 2.0
│
└── Repository B
    ├── Image: 1.0
    └── Image: 2.0
```

O Registry é o serviço que armazena e distribui as imagens.

O Repository organiza imagens relacionadas.

---

## Tag x Digest

```text
Tag
↓
referência legível

Digest
↓
identificação baseada no conteúdo
```

Durante o laboratório, a imagem recebeu a tag:

```text
1.0
```

O ECR também apresentou o digest da imagem, por exemplo:

```text
sha256:...
```

---

# Relação entre Docker, ECR e ECS

O fluxo estudado foi:

```text
Código
  ↓
Dockerfile
  ↓
docker build
  ↓
Docker Image
  ↓
ECR Repository
  ↓
docker push
  ↓
ECS
  ↓
Container
```

O ECR participa do armazenamento e distribuição da imagem.

Ele não faz parte do caminho de requisições da aplicação.

Por exemplo:

```text
Internet
   ↓
ALB
   ↓
ECS
   ↓
Container
   ↓
RDS
```

O ECR fornece a imagem utilizada pelo ECS, mas não recebe as requisições da aplicação.

---

# Hands-on

## 1. IAM

Foi criada uma policy específica:

```text
aws-cloud-practitioner-lab-ecr
```

e anexada ao usuário:

```text
aws-cloud-practitioner-lab
```

O laboratório continuou utilizando Least Privilege, sem `AdministratorAccess`.

---

## 2. Criação do Repository

Foi criado utilizando Terraform:

```text
Repository:

aws-cloud-practitioner-lab
```

Na região:

```text
sa-east-1
```

O repository foi validado utilizando AWS CLI.

Configuração observada:

```text
Image Tag Mutability: MUTABLE
Scan on Push: false
Encryption: AES256
```

O ECR forneceu o Repository URI:

```text
234644232681.dkr.ecr.sa-east-1.amazonaws.com/aws-cloud-practitioner-lab
```

---

## 3. Docker Login

O Docker foi autenticado no ECR e retornou:

```text
Login Succeeded
```

O login utiliza um token temporário obtido através da AWS.

Isso é diferente de armazenar permanentemente a Access Key dentro do Docker.

O token utilizado para autenticação no registry possui validade limitada. Em outro dia, caso o token tenha expirado, será necessário realizar novamente o login.

As credenciais AWS continuam sendo fornecidas pelo mecanismo de autenticação configurado para o AWS CLI/Terraform; elas não devem ser colocadas diretamente no comando ou na imagem.

---

## 4. Docker Image

Foi criada uma imagem Docker local para o laboratório.

Depois, a imagem recebeu uma tag compatível com o endereço do ECR:

```text
234644232681.dkr.ecr.sa-east-1.amazonaws.com/aws-cloud-practitioner-lab:1.0
```

---

## 5. Push

A imagem foi enviada para o ECR utilizando:

```text
docker push
```

O ECR passou a armazenar a imagem e suas camadas.

---

## 6. Validação no ECR

A imagem foi validada utilizando:

```bash
aws ecr describe-images \
  --repository-name aws-cloud-practitioner-lab \
  --region sa-east-1
```

O ECR apresentou três manifests/digests relacionados ao artefato enviado, incluindo a imagem com a tag:

```text
1.0
```

O digest da imagem com essa tag foi:

```text
sha256:ca0ab07f6ca437c7c63acf2a6c353336cb68ca9b8a02ec06ea04beabd7f07819
```

A imagem estava com:

```text
imageStatus: ACTIVE
```

---

## 7. Pull

Para demonstrar que o ECR realmente funcionava como registry, a imagem foi removida do ambiente Docker local:

```bash
docker rmi \
  aws-cloud-practitioner-lab:1.0 \
  234644232681.dkr.ecr.sa-east-1.amazonaws.com/aws-cloud-practitioner-lab:1.0
```

Depois foi executado:

```bash
docker pull \
  234644232681.dkr.ecr.sa-east-1.amazonaws.com/aws-cloud-practitioner-lab:1.0
```

O Docker baixou novamente as camadas da imagem a partir do ECR.

O digest retornado pelo Docker correspondeu ao digest observado no ECR.

Isso demonstrou o fluxo:

```text
ECR
 ↓
docker pull
 ↓
Docker local
```

---

# ECR e VPC

Diferentemente do RDS estudado no Day 7, o ECR Repository criado neste módulo não precisou de:

```text
VPC
Subnet
Route Table
Internet Gateway
Security Group
```

O repository é um recurso regional do ECR.

Isso reforça uma diferença importante entre os serviços estudados:

```text
RDS
→ recurso associado à VPC

ECR
→ serviço de registry
```

---

# Controle de custos

O ECR pode gerar custos relacionados principalmente ao armazenamento das imagens e à transferência de dados.

Por isso, o repository foi utilizado somente durante o exercício.

Não foram mantidas imagens ou repository após a conclusão do laboratório.

Fluxo utilizado:

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

# Problemas encontrados durante o cleanup

## Repository contendo imagens

A primeira tentativa de:

```bash
terraform destroy
```

falhou porque o repository ainda continha imagens.

A AWS retornou:

```text
RepositoryNotEmptyException
```

Isso demonstrou que um ECR Repository não pode ser removido enquanto possui imagens, salvo quando configurado para exclusão forçada.

O problema foi resolvido removendo o conteúdo necessário e executando novamente o cleanup.

---

## Detach da IAM Policy

Durante uma tentativa posterior de cleanup, o Terraform conseguiu destruir o repository, mas falhou ao tentar remover a associação da policy:

```text
aws-cloud-practitioner-lab-ecr
```

A AWS retornou:

```text
AccessDenied
```

para:

```text
iam:DetachUserPolicy
```

A permissão foi ajustada e o cleanup foi concluído com sucesso.

Esse comportamento reforçou uma lição já observada em módulos anteriores:

> As permissões necessárias para criar um recurso não são necessariamente as mesmas necessárias para destruí-lo.

---

# Pegadinhas importantes para a CLF-C02

* ECR é **Container Registry**.
* ECR armazena e distribui imagens.
* ECR não executa containers.
* ECS gerencia a execução dos containers.
* Image não é Container.
* Repository organiza imagens relacionadas.
* Tag é uma referência legível.
* Digest identifica o conteúdo da imagem.
* `latest` não significa necessariamente a imagem mais nova.
* ECR pode ser utilizado com ECS, mas não é exclusivo dele.
* ECR não precisa de VPC, subnet ou Security Group para criar um repository.
* IAM controla o acesso às operações do ECR.
* Secrets não devem ser embutidos em imagens Docker.
* Uma imagem armazenada no ECR não significa que exista um container em execução.

---

# Relação com os módulos anteriores

O Day 8 conecta diretamente os conhecimentos anteriores:

```text
IAM
 ↓
controle de acesso ao ECR

Docker
 ↓
criação da Image

ECR
 ↓
armazenamento da Image

VPC
 ↓
rede onde os workloads podem executar

ECS
 ↓
execução dos Containers
```

A arquitetura planejada começa a ganhar forma:

```text
Docker
   ↓
ECR
   ↓
ECS
   ↓
RDS
```

---

# Checklist

* [x] Estudar ECR
* [x] Diferenciar Image e Container
* [x] Compreender Container Registry
* [x] Compreender Repository
* [x] Compreender Tag
* [x] Compreender Digest
* [x] Diferenciar ECR e ECS
* [x] Criar ECR Repository com Terraform
* [x] Aplicar Least Privilege
* [x] Criar Docker Image
* [x] Autenticar Docker no ECR
* [x] Fazer push da imagem
* [x] Validar imagem no ECR
* [x] Remover imagem local
* [x] Fazer pull da imagem
* [x] Observar o Digest
* [x] Corrigir problema de cleanup do repository
* [x] Corrigir permissão de `iam:DetachUserPolicy`
* [x] Destruir o repository
* [x] Consolidar conceitos da CLF-C02

---

# Resultado

**Day 8 — ECR: concluído.**

O principal modelo mental consolidado foi:

```text
Image
  ↓
ECR Repository
  ↓
ECR
  ↓
ECS
  ↓
Container
```

O ECR fornece o artefato necessário para que um ambiente de execução como ECS possa iniciar os containers.
