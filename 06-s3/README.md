# 06 — S3 (Simple Storage Service)

## Objetivo

Estudar o Amazon S3 (Simple Storage Service) e compreender, na prática:

* Object Storage;
* Bucket e Object;
* Object Key;
* Storage Classes;
* Versioning;
* Delete Marker;
* permissões IAM para S3;
* Block Public Access;
* diferenças entre S3, EBS e EFS;
* utilização do S3 pela AWS CLI;
* gerenciamento do S3 com Terraform.

O laboratório segue o fluxo:

**entender → criar → testar → observar → documentar → destruir**

---

## 1. Conceitos fundamentais

### 1.1 Amazon S3

Amazon S3 é um serviço de **Object Storage** da AWS utilizado para armazenar e recuperar objetos.

Pode ser utilizado para armazenar:

* documentos;
* imagens;
* vídeos;
* backups;
* logs;
* arquivos JSON, XML e CSV;
* arquivos utilizados por aplicações.

S3 não é um banco de dados NoSQL. Seu objetivo principal é fornecer armazenamento de objetos acessível por APIs, SDKs, CLI e outros serviços da AWS.

---

### 1.2 Bucket

O **Bucket** é o recipiente onde os objetos são armazenados.

Exemplo:

```text
S3
└── Bucket
    ├── example.txt
    ├── relatorio.pdf
    └── imagem.jpg
```

Os nomes dos buckets precisam ser globalmente únicos.

No laboratório foi utilizado:

```text
aws-cloud-practitioner-lab-s3-<account-id>
```

---

### 1.3 Object

Um **Object** é o dado armazenado dentro de um bucket.

Um objeto possui, entre outras informações:

* conteúdo;
* Key;
* metadata;
* Storage Class;
* informações de versionamento, quando habilitado.

Exemplo:

```text
Bucket
└── Object
    ├── Key: example.txt
    └── Conteúdo: Arquivo de teste...
```

O S3 não utiliza diretórios tradicionais como um sistema de arquivos. Caminhos como:

```text
documentos/contrato.pdf
```

são representados por uma **Key** contendo um prefixo:

```text
documentos/contrato.pdf
```

---

### 1.4 Object Key

A **Key** identifica um objeto dentro do bucket.

Por exemplo:

```text
Bucket:
aws-cloud-practitioner-lab-s3-123456789012

Key:
example.txt
```

Quando o Versioning está habilitado, diferentes versões podem possuir a mesma Key, mas diferentes `VersionId`.

---

## 2. S3, EBS e EFS

É importante diferenciar os três principais modelos de armazenamento estudados:

| Serviço | Tipo           | Conceito                                                            |
| ------- | -------------- | ------------------------------------------------------------------- |
| S3      | Object Storage | Armazena objetos acessados pelo serviço S3                          |
| EBS     | Block Storage  | Armazenamento de bloco utilizado como disco, principalmente com EC2 |
| EFS     | File Storage   | Sistema de arquivos compartilhado                                   |

Memorização:

```text
S3  → Object
EBS → Block
EFS → File
```

### S3 × EBS

O EBS funciona conceitualmente como um disco associado a uma EC2:

```text
EC2
└── EBS
    └── Sistema de arquivos
        ├── arquivo1
        └── arquivo2
```

Já o S3 trabalha com objetos:

```text
S3
└── Bucket
    ├── arquivo1
    └── arquivo2
```

Portanto, S3 e EBS possuem modelos de armazenamento diferentes e atendem a necessidades diferentes.

---

## 3. Storage Classes

Uma **Storage Class** define a categoria de armazenamento utilizada por um objeto, considerando características como frequência de acesso, custo e necessidade de recuperação.

Exemplos:

* **S3 Standard** — objetos acessados frequentemente;
* **S3 Standard-IA** — objetos acessados com pouca frequência;
* **S3 Glacier** — armazenamento voltado para arquivamento.

A escolha da Storage Class permite equilibrar custo e necessidade de acesso aos dados.

No laboratório, os objetos foram armazenados utilizando:

```text
StorageClass: STANDARD
```

---

## 4. Versioning

O **Versioning** permite manter múltiplas versões do mesmo objeto.

Sem Versioning:

```text
example.txt
└── conteúdo atual
```

Ao enviar novamente um objeto utilizando a mesma Key, o conteúdo atual é substituído.

Com Versioning:

```text
example.txt
├── Version A → conteúdo anterior
└── Version B → conteúdo atual
```

A Key continua sendo:

```text
example.txt
```

mas cada versão possui um `VersionId` diferente.

### Utilidade

Versioning pode ajudar a:

* recuperar versões anteriores;
* proteger contra sobrescritas acidentais;
* recuperar objetos após exclusões.

Versioning não deve ser tratado simplesmente como backup, pois versões antigas continuam ocupando armazenamento.

---

## 5. Delete Marker

Com Versioning habilitado, executar um `DELETE` sobre um objeto normalmente não remove imediatamente suas versões anteriores.

O S3 cria um **Delete Marker**.

Exemplo:

```text
example.txt
├── Delete Marker
│   └── IsLatest: true
│
├── Version B
│   └── conteúdo atual anterior
│
└── Version A
    └── conteúdo antigo
```

O Delete Marker faz com que o objeto deixe de aparecer normalmente nas consultas do bucket, mas as versões anteriores continuam disponíveis.

Uma versão específica pode ser acessada utilizando seu `VersionId`.

---

## 6. Permissões IAM utilizadas

Foi criada uma policy específica para o laboratório:

```text
aws-cloud-practitioner-lab-s3
```

A policy foi anexada ao usuário:

```text
aws-cloud-practitioner-lab
```

As permissões foram divididas em grupos.

### Criação do bucket

```text
s3:CreateBucket
```

Essa ação utiliza `Resource = "*"` porque o bucket ainda não existe no momento da criação.

### Gerenciamento do bucket

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
```

Essas permissões foram restringidas ao bucket do laboratório.

### Gerenciamento dos objetos

```text
s3:GetObject
s3:PutObject
s3:DeleteObject
s3:DeleteObjectVersion
s3:GetObjectVersion
```

Essas permissões foram restringidas aos objetos do bucket:

```text
arn:aws:s3:::<bucket>/*
```

Essa separação demonstra o princípio de **Least Privilege**.

---

## 7. Block Public Access

O bucket foi configurado com o bloqueio de acesso público:

```hcl
resource "aws_s3_bucket_public_access_block" "lab" {
  bucket = aws_s3_bucket.lab.id

  block_public_acls       = true
  block_public_policy     = true
  ignore_public_acls      = true
  restrict_public_buckets = true
}
```

O objetivo foi manter o bucket privado durante todo o laboratório.

É importante diferenciar:

**IAM Policy**

> Determina se uma identidade possui permissão para executar determinada ação.

**Block Public Access**

> Impede ou restringe configurações que poderiam tornar o bucket acessível publicamente.

---

## 8. Terraform

O bucket foi criado com nome baseado no ID da conta para garantir unicidade:

```hcl
data "aws_caller_identity" "current" {}

locals {
  bucket_name = "aws-cloud-practitioner-lab-s3-${data.aws_caller_identity.current.account_id}"
}
```

Bucket:

```hcl
resource "aws_s3_bucket" "lab" {
  bucket       = local.bucket_name
  force_destroy = true

  tags = {
    Name = "aws-cloud-practitioner-lab-s3"
  }
}
```

### `force_destroy`

Foi utilizado:

```hcl
force_destroy = true
```

para permitir que o Terraform remova o bucket mesmo quando existirem objetos e versões.

Essa configuração é adequada para este laboratório, pois facilita o cleanup, mas deve ser utilizada com cuidado em ambientes de produção.

---

## 9. Hands-on com AWS CLI

### Criar arquivo

```bash
echo "Arquivo de teste do AWS Cloud Practitioner Lab" > example.txt
```

### Upload

```bash
aws s3 cp example.txt \
  s3://aws-cloud-practitioner-lab-s3-<account-id>/
```

Demonstrou:

```text
s3:PutObject
```

### Listar objetos

```bash
aws s3 ls \
  s3://aws-cloud-practitioner-lab-s3-<account-id>/
```

Demonstrou:

```text
s3:ListBucket
```

### Download

```bash
aws s3 cp \
  s3://aws-cloud-practitioner-lab-s3-<account-id>/example.txt \
  example.txt
```

Demonstrou:

```text
s3:GetObject
```

---

## 10. Experimento: sobrescrita sem Versioning

Inicialmente o bucket estava com Versioning desabilitado.

O mesmo objeto foi enviado novamente utilizando a mesma Key:

```text
example.txt
```

O conteúdo anterior foi substituído pelo novo conteúdo.

Conclusão:

> Com Versioning desabilitado, um upload utilizando a mesma Key substitui o objeto atual.

---

## 11. Experimento: Versioning

O Versioning foi habilitado pelo Terraform:

```hcl
resource "aws_s3_bucket_versioning" "lab" {
  bucket = aws_s3_bucket.lab.id

  versioning_configuration {
    status = "Enabled"
  }
}
```

Depois, o mesmo objeto foi enviado novamente.

O comando:

```bash
aws s3api list-object-versions \
  --bucket aws-cloud-practitioner-lab-s3-<account-id> \
  --prefix example.txt
```

demonstrou a existência de múltiplas versões.

Foi observado também que o objeto criado antes da ativação do Versioning possuía:

```text
VersionId: null
```

enquanto o novo objeto possuía um `VersionId` específico.

---

## 12. Experimento: Delete Marker

Com Versioning habilitado, foi executado:

```bash
aws s3 rm \
  s3://aws-cloud-practitioner-lab-s3-<account-id>/example.txt
```

Depois, `list-object-versions` demonstrou:

```text
DeleteMarkers
└── VersionId: <id>
    IsLatest: true
```

As versões anteriores permaneceram armazenadas.

Também foi possível recuperar uma versão anterior utilizando:

```bash
aws s3api get-object \
  --bucket aws-cloud-practitioner-lab-s3-<account-id> \
  --key example.txt \
  --version-id '<version-id>' \
  recovered.txt
```

Esse experimento demonstrou na prática como o Versioning protege contra perda acidental decorrente de sobrescrita ou exclusão.

---

## 13. Cleanup

Após os experimentos, foi executado:

```bash
terraform destroy
```

O bucket foi destruído com sucesso.

Como o bucket possuía objetos, versões e Delete Marker, o:

```hcl
force_destroy = true
```

permitiu que o Terraform realizasse a limpeza do conteúdo antes da remoção do bucket.

O cleanup foi concluído sem infraestrutura S3 restante do laboratório.

---

# 14. Perguntas de revisão

## 1. O que é o Amazon S3 e para que você utilizaria ele?

**Minha resposta:**

> S3 é um recurso de armazenamento que segue a arquitetura de um banco de dados NoSQL de documentos

**Correção:**

S3 é um serviço de **Object Storage**, não um banco de dados NoSQL. É utilizado para armazenar e recuperar objetos como documentos, imagens, vídeos, backups e logs.

---

## 2. Qual a diferença entre um Bucket e um Object no S3?

**Minha resposta:**

> O Bucket seria equivalente  a base de dados de um banco de dados relacional e o Object seria o que é armazenado dentro do Bucket, equivalente as tabelas do SQL

**Correção:**

A analogia com banco de dados não é adequada.

* **Bucket** → recipiente onde os objetos são armazenados.
* **Object** → dado armazenado dentro do bucket.

---

## 3. Se eu tenho um arquivo `relatorio.pdf`, onde exatamente esse arquivo fica armazenado no S3?

**Minha resposta:**

> Fica armazenado num Bucket

**Correção:**

Correto. O arquivo é armazenado como um **Object** dentro de um Bucket, identificado por uma Key, por exemplo:

```text
Bucket: meu-bucket
Key: relatorio.pdf
```

---

## 4. S3 é armazenamento de bloco, arquivo ou objeto? O que isso significa na prática?

**Minha resposta:**

> Não sei exatamente mas acredito que seja de arquivos

**Correção:**

S3 é **Object Storage**.

```text
S3  → Object Storage
EBS → Block Storage
EFS → File Storage
```

Na prática, objetos são enviados, recuperados e excluídos através de APIs, CLI, SDKs e outros mecanismos do serviço S3.

---

## 5. Qual a diferença conceitual entre S3 e EBS?

**Minha resposta:**

> A diferença está no tipo de armazenamento, sendo que o EBS é de bloco e o S3 é de arquivos

**Correção:**

A primeira parte está correta, mas S3 é **Object Storage**, não File Storage.

```text
EBS → Block Storage
S3  → Object Storage
```

EBS funciona como armazenamento de bloco associado principalmente a EC2, enquanto S3 armazena objetos por meio do serviço S3.

---

## 6. O que você entende por Storage Class no S3?

**Minha resposta:**

> Não sei exatamente mas acredito que seja algo relacionado aos atributos que podem ser consultados do objeto armazenado, semelhante as colunas do SQL

**Correção:**

Storage Class não é equivalente a uma coluna de banco de dados.

Ela representa a **categoria de armazenamento utilizada pelo objeto**, considerando características como frequência de acesso e custo.

Exemplos:

```text
S3 Standard
S3 Standard-IA
S3 Glacier
```

---

## 7. O que aconteceria se eu sobrescrevesse `documento.txt` por outro arquivo com o mesmo nome em um bucket com Versioning desabilitado?

**Minha resposta:**

> O documento teria o seu conteúdo sobrescrito com o conteúdo do novo arquivo

**Correção:**

Correto.

Com Versioning desabilitado, o upload utilizando a mesma Key substitui o objeto atual.

---

## 8. Para que serve o Versioning do S3?

**Minha resposta:**

> Não sei extamente mas acredito que seria semelhante a uma tabela de autiroria do SQL que fica guardando as modificações feitas que foram feitas ao longo do tempo num registro

**Correção:**

A ideia de histórico está correta, mas a implementação é diferente.

O Versioning mantém múltiplas versões de um mesmo objeto, permitindo recuperar versões anteriores e ajudar na proteção contra sobrescritas ou exclusões acidentais.

---

## 9. Se um bucket S3 está configurado como privado, o que determina se uma identidade pode acessar um objeto?

**Minha resposta:**

> Se ela tem as permissões corretas anexadas na policy

**Correção:**

Correto, considerando principalmente as políticas de acesso aplicáveis.

O acesso pode ser determinado por mecanismos como:

* IAM Policy;
* Bucket Policy;
* configurações de Block Public Access.

Para o Cloud Practitioner:

> Uma identidade precisa ter permissão para executar a ação solicitada sobre o recurso, respeitando também as demais configurações e políticas aplicáveis.

---

# 15. Principais pegadinhas para o CLF-C02

* S3 é **Object Storage**, não Block Storage.
* EBS é **Block Storage**.
* EFS é **File Storage**.
* Bucket é o recipiente; Object é o dado armazenado.
* Bucket names precisam ser globalmente únicos.
* S3 não possui diretórios tradicionais; utiliza Keys e prefixes.
* Versioning permite múltiplas versões do mesmo objeto.
* Delete com Versioning habilitado normalmente cria um Delete Marker.
* Delete Marker não significa que as versões anteriores foram imediatamente removidas.
* Storage Class está relacionada à forma/categoria de armazenamento, acesso e custo.
* S3 pode ser privado e ainda assim permitir acesso autorizado.
* Block Public Access é uma camada importante de proteção contra exposição pública.

---

# Resultado

* [x] Compreender S3 e Object Storage
* [x] Diferenciar Bucket e Object
* [x] Compreender Object Key
* [x] Diferenciar S3, EBS e EFS
* [x] Compreender Storage Classes
* [x] Criar Bucket com Terraform
* [x] Configurar Block Public Access
* [x] Configurar Versioning
* [x] Fazer upload e download com AWS CLI
* [x] Testar sobrescrita de objeto
* [x] Observar múltiplas versões
* [x] Observar Delete Marker
* [x] Recuperar uma versão anterior
* [x] Aplicar Least Privilege
* [x] Executar cleanup com Terraform
* [x] Destruir o bucket e os recursos do laboratório

**Status: Completed**
