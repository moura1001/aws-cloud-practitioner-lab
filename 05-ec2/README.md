# 05 — EC2 (Elastic Compute Cloud)

## Objetivo

Estudar o Amazon EC2 (**Elastic Compute Cloud**) e compreender os principais conceitos relacionados à execução de máquinas virtuais na AWS.

O laboratório tem como objetivos:

* compreender EC2 como serviço de computação;
* entender AMI e Instance Type;
* compreender EBS e seu ciclo de vida;
* diferenciar Security Group de IAM;
* compreender IP privado e IP público;
* utilizar IAM Role com EC2;
* diferenciar Stop e Terminate;
* praticar o ciclo de vida de uma instância;
* observar o comportamento da infraestrutura através da AWS CLI;
* utilizar Terraform para provisionamento e gerenciamento;
* praticar controle de custos e limpeza dos recursos.

---

## Conceitos estudados

### EC2

Amazon EC2 fornece capacidade computacional sob demanda.

Uma instância EC2 pode ser entendida como uma máquina virtual configurável, na qual podemos definir:

* sistema operacional;
* CPU;
* memória;
* armazenamento;
* rede;
* Security Groups;
* IAM Role;
* entre outras características.

A EC2 é utilizada para executar aplicações, serviços e workloads que precisam de capacidade computacional.

---

### AMI

AMI significa **Amazon Machine Image**.

É uma imagem/template utilizada para iniciar uma instância EC2.

Uma AMI pode conter:

* sistema operacional;
* softwares instalados;
* configurações;
* informações relacionadas ao armazenamento necessário para iniciar a instância.

**Importante para a CLF-C02:**

> AMI não é simplesmente o sistema operacional. É a imagem/template utilizada para lançar a instância.

No laboratório foi utilizada uma AMI **Amazon Linux 2023 Minimal**.

---

### Instance Type

O Instance Type define as características computacionais da instância.

Entre elas estão:

* vCPUs;
* memória;
* características de rede;
* características de armazenamento;
* capacidade computacional.

No laboratório foi utilizado:

```text
t3.nano
```

A escolha foi feita priorizando o menor custo possível para o experimento.

---

### EBS

**Amazon Elastic Block Store (EBS)** fornece armazenamento em bloco para instâncias EC2.

O EBS é utilizado como armazenamento persistente.

No laboratório:

```text
Tipo: gp3
Tamanho: 2 GiB
Device: /dev/xvda
```

O volume raiz estava associado à EC2 e aparecia na AWS como:

```text
State: in-use
Type: gp3
Size: 2 GiB
AZ: sa-east-1a
```

### Persistência

EBS é normalmente persistente em relação ao ciclo de vida da instância.

Entretanto, o comportamento durante o **Terminate** depende da configuração `DeleteOnTermination`.

Neste laboratório:

```text
delete_on_termination = true
```

Portanto, o EBS raiz foi excluído quando a instância foi terminada.

---

### EBS x Instance Store

É importante não confundir:

```text
EBS
→ armazenamento persistente

Instance Store
→ armazenamento temporário/efêmero
```

O Instance Store está associado ao hardware da instância e seus dados podem ser perdidos quando a instância é parada ou terminada, dependendo do ciclo de vida.

Para a CLF-C02, a associação principal a memorizar é:

> **EBS → persistent block storage**

---

## Security Group x IAM

Security Group e IAM possuem funções completamente diferentes.

### Security Group

É um firewall virtual associado às interfaces de rede da instância.

Controla o tráfego de rede permitido.

Pergunta que ajuda a memorizar:

> **"Esse tráfego de rede pode passar?"**

### IAM

Controla permissões relacionadas às ações que identidades podem executar na AWS.

Pergunta que ajuda a memorizar:

> **"Essa identidade pode executar essa ação?"**

Portanto:

```text
Security Group
→ controle de tráfego de rede

IAM
→ controle de permissões na AWS
```

Security Group **não é um subconjunto do IAM**.

---

## IP privado x IP público

### IP privado

É utilizado para comunicação dentro da rede privada da VPC.

No laboratório:

```text
10.0.1.188
```

O IP privado permaneceu o mesmo durante o experimento de Stop/Start.

### IP público

Permite comunicação direta com a Internet quando os demais requisitos de rede também estão presentes.

A subnet pública utilizada no laboratório tinha:

```text
map_public_ip_on_launch = true
```

Isso permite que instâncias lançadas nessa subnet recebam automaticamente um IPv4 público.

Entretanto:

> Uma subnet pública não significa que toda EC2 nela terá necessariamente um IP público.

O IP público pode ser obtido por outros mecanismos, como Elastic IP.

---

## Acesso direto à EC2 pela Internet

Para uma EC2 receber conexões diretamente da Internet, não basta estar em uma subnet pública.

É necessário considerar:

```text
EC2
 │
 ├── IP público
 │
 ├── subnet pública
 │     └── Route Table → Internet Gateway
 │
 ├── Security Group
 │     └── permite a porta necessária
 │
 └── serviço correspondente em execução
```

Para SSH, por exemplo:

```text
TCP/22
```

deveria estar permitido no Security Group.

Neste laboratório não foi aberta nenhuma regra de entrada e não foi configurada uma chave SSH.

Isso foi intencional para reduzir a complexidade e evitar exposição desnecessária.

---

## IAM Role e Instance Profile

A EC2 utilizou a Role criada no módulo de IAM:

```text
aws-cloud-practitioner-lab-ec2-s3-read
```

A associação ocorreu através de um **Instance Profile**:

```text
EC2
 ↓
Instance Profile
 ↓
IAM Role
 ↓
AmazonS3ReadOnlyAccess
```

O Instance Profile é o mecanismo utilizado para disponibilizar a IAM Role para a instância EC2.

### Por que utilizar Role?

A Role fornece credenciais temporárias para a aplicação/recurso, evitando a necessidade de armazenar Access Keys permanentes dentro da EC2.

Isso reduz o risco associado ao vazamento de credenciais.

**Para a CLF-C02:**

> EC2 → IAM Role → credenciais temporárias

é preferível a:

> EC2 → Access Keys permanentes armazenadas na máquina.

---

# Hands-on

## Recursos utilizados

### VPC existente

```text
VPC: aws-cloud-practitioner-lab-vpc
CIDR: 10.0.0.0/16
```

### Subnet pública

```text
Name: aws-cloud-practitioner-lab-public-a
CIDR: 10.0.1.0/24
AZ: sa-east-1a
```

### AMI

Amazon Linux 2023 Minimal.

A AMI foi localizada dinamicamente através do Terraform:

```hcl
data "aws_ami" "amazon_linux" {
  most_recent = true
  owners      = ["amazon"]

  filter {
    name   = "name"
    values = ["al2023-ami-minimal-*"]
  }

  filter {
    name   = "architecture"
    values = ["x86_64"]
  }

  filter {
    name   = "virtualization-type"
    values = ["hvm"]
  }
}
```

---

## Terraform

Estrutura do módulo:

```text
05-ec2/
├── main.tf
├── outputs.tf
├── .terraform.lock.hcl
└── terraform.tfstate
```

O `main.tf` contém a infraestrutura e os `data sources`.

O `outputs.tf` contém os outputs da instância.

### Outputs

```hcl
output "instance_id" {
  description = "ID da EC2 criada pelo laboratório"
  value       = aws_instance.ec2.id
}

output "instance_private_ip" {
  description = "IP privado da EC2"
  value       = aws_instance.ec2.private_ip
}

output "instance_public_ip" {
  description = "IP público da EC2"
  value       = aws_instance.ec2.public_ip
}

output "instance_state" {
  description = "Estado atual da EC2"
  value       = aws_instance.ec2.instance_state
}

output "instance_type" {
  description = "Tipo da instância EC2"
  value       = aws_instance.ec2.instance_type
}

output "security_group_id" {
  description = "ID do Security Group da EC2"
  value       = aws_security_group.ec2.id
}
```

---

## Security Group

Foi criado um Security Group específico para o laboratório:

```text
Name: aws-cloud-practitioner-lab-ec2
ID: sg-0dff74ea5acf38dff
```

Não foram configuradas regras de entrada.

Isso significa que a EC2 não ficou exposta para conexões de entrada como SSH ou HTTP.

---

## Instance Profile

Foi criado o Instance Profile:

```text
aws-cloud-practitioner-lab-ec2
```

utilizando a Role:

```text
aws-cloud-practitioner-lab-ec2-s3-read
```

---

## EC2 criada

A instância utilizada no experimento apresentou:

```text
Instance Type: t3.nano
AMI: Amazon Linux 2023 Minimal
AZ: sa-east-1a
Subnet: aws-cloud-practitioner-lab-public-a
Private IP: 10.0.1.188
```

O IPv4 público inicialmente atribuído foi posteriormente alterado após Stop/Start.

---

# Experimento — Stop x Start

A instância foi parada através da AWS CLI:

```bash
aws ec2 stop-instances \
  --instance-ids "$(terraform output -raw instance_id)"
```

Depois foi aguardado o estado:

```bash
aws ec2 wait instance-stopped \
  --instance-ids "$(terraform output -raw instance_id)"
```

Durante o estado `stopped`:

```text
Private IP: 10.0.1.188
Public IP: None
State: stopped
```

A instância foi então iniciada:

```bash
aws ec2 start-instances \
  --instance-ids "$(terraform output -raw instance_id)"
```

Após o Start:

```text
Private IP: 10.0.1.188
Public IP: 18.228.199.166
State: running
```

### Resultado

O experimento demonstrou que:

* o IP privado permaneceu o mesmo;
* o IPv4 público foi liberado durante o Stop;
* um novo IPv4 público foi atribuído no Start.

Para manter um endereço IPv4 público estático, pode-se utilizar um **Elastic IP**.

Elastic IP não foi utilizado no laboratório para evitar recursos e complexidade desnecessários.

---

# Experimento — EBS

O volume raiz foi consultado através da AWS CLI:

```bash
aws ec2 describe-volumes \
  --volume-ids vol-0a9d0e74182b38ca8 \
  --query 'Volumes[].{State:State,Type:VolumeType,Size:Size,Encrypted:Encrypted,AZ:AvailabilityZone,Instance:Attachments[0].InstanceId,Device:Attachments[0].Device}' \
  --output table
```

Resultado:

```text
AZ:          sa-east-1a
Device:      /dev/xvda
Encrypted:   False
Instance:    i-0f376ee15a44d8cf3
Size:        2 GiB
State:       in-use
Type:        gp3
```

Isso confirmou que o EBS estava anexado à EC2 como armazenamento raiz.

---

# Experimento — Terminate

Depois de testar Stop/Start, a instância foi terminada:

```bash
aws ec2 terminate-instances \
  --instance-ids "$(terraform output -raw instance_id)"
```

Foi aguardada a conclusão:

```bash
aws ec2 wait instance-terminated \
  --instance-ids "$(terraform output -raw instance_id)"
```

A consulta posterior mostrou:

```text
State: terminated
PrivateIP: None
PublicIP: None
```

Em seguida, foi feita uma tentativa de consultar o volume EBS:

```bash
aws ec2 describe-volumes \
  --volume-ids vol-0a9d0e74182b38ca8 \
  --query 'Volumes[].{State:State,Type:VolumeType,Size:Size,Encrypted:Encrypted,AZ:AvailabilityZone}' \
  --output table
```

A AWS retornou:

```text
InvalidVolume.NotFound
```

Isso confirmou que o volume havia sido excluído junto com a EC2.

O motivo foi:

```text
delete_on_termination = true
```

### Regra importante

```text
Stop
→ EC2 parada
→ EBS permanece

Terminate
→ EC2 encerrada
→ EBS depende de DeleteOnTermination
```

No laboratório:

```text
DeleteOnTermination = true
→ EBS excluído
```

---

# Terraform State x infraestrutura real

A EC2 foi terminada utilizando a AWS CLI, fora do Terraform.

Inicialmente, o Terraform ainda possuía a instância no state.

Foi utilizado:

```bash
terraform apply -refresh-only
```

O Terraform detectou:

```text
aws_instance.ec2 has been deleted
```

e atualizou o state sem modificar a infraestrutura AWS.

Os outputs da EC2 passaram a apresentar:

```text
instance_id         = null
instance_private_ip = null
instance_public_ip  = null
instance_state      = null
instance_type       = null
```

### Conceito importante

Terraform mantém um **state** para representar os recursos que gerencia.

Quando uma alteração é feita diretamente na AWS, pode existir uma diferença entre:

```text
Terraform State
        ×
Infraestrutura real
```

`terraform apply -refresh-only` pode ser utilizado para atualizar o state de acordo com o estado real sem aplicar mudanças de infraestrutura.

---

# Terraform Destroy e Least Privilege

Durante a limpeza, o `terraform destroy` conseguiu remover recursos como:

* EC2;
* Security Group;
* Instance Profile, após as permissões necessárias;
* associação da IAM Policy, após as permissões necessárias.

Inicialmente, o destroy falhou com:

```text
iam:DetachUserPolicy
iam:RemoveRoleFromInstanceProfile
```

Essas permissões não estavam presentes na policy do usuário.

Foram adicionadas temporariamente:

```text
iam:DeleteInstanceProfile
iam:DetachUserPolicy
iam:RemoveRoleFromInstanceProfile
iam:ListPolicyVersions
```

Não foi adicionada:

```text
iam:DeletePolicy
```

para evitar conceder ao usuário a capacidade de excluir a própria policy do laboratório.

### Aprendizado

O princípio de **Least Privilege** também influencia operações de cleanup.

As permissões necessárias para criar um recurso não são necessariamente as mesmas necessárias para removê-lo.

O erro foi útil para demonstrar isso na prática.

---

# Perguntas de revisão

## 1. O que é EC2?

O que é o Amazon EC2? Em suas palavras, o que é uma instância EC2 e para que ela serve?

### Minha resposta:

> É como se fosse uma máquina virtual no sentido de ser isolada e podermos customizá-la seus recursos computacionais, sistema operacional, etc, para ser utilizada para executar as aplicações que precisamos

### Correção:

Resposta correta.

Para a CLF-C02, associar EC2 principalmente a **capacidade computacional sob demanda**.

---

## 2. O que é uma AMI?

O que é uma AMI? Quando criamos uma EC2, precisamos escolher uma AMI. O que você entende que é uma AMI?

### Minha resposta:

> AMI é justamente o tipo de sistema operacional com que a EC2 vai rodar

### Correção:

Parcialmente correta.

AMI significa **Amazon Machine Image** e é uma imagem/template utilizada para lançar uma EC2.

Ela pode incluir o sistema operacional, softwares, configurações e informações relacionadas ao armazenamento.

---

## 3. O que define um Instance Type?

O que significa o tipo de instância?
Por exemplo: t3.micro. O que você acha que estamos escolhendo quando selecionamos esse tipo?

### Minha resposta:

> Estamos escolhendo o poder computacional que a instância terá para executar, no sentido de CPU e memória

### Correção:

Correto.

O Instance Type define características como:

* vCPU;
* memória;
* rede;
* armazenamento;
* capacidade computacional.

---

## 4. O que é EBS?

Onde ficam os dados de uma EC2? Imagine que criamos uma EC2 e instalamos uma aplicação nela. Onde ficam armazenados o sistema operacional, arquivos e demais dados? Você conhece o EBS? Se sim, explique o que sabe.

### Minha resposta:

> Os dados ficam num armazenamento interno específico e não persistente no sentido em que só consegue guardar os dados enquanto a instância estiver em execução, que só pode ser acessado pela EC2 em que está anexado, que é justamente o EBS

### Correção:

A principal correção é sobre persistência.

**EBS é armazenamento persistente em bloco**, e não armazenamento temporário por padrão.

O conceito de armazenamento temporário/efêmero está associado ao **Instance Store**.

No EBS, a permanência do volume após o Terminate depende de configurações como `DeleteOnTermination`.

---

## 5. Qual a diferença entre Security Group e IAM?

O que é um Security Group? Você já estudou Security Group no contexto da VPC. O que ele controla em uma EC2? E qual seria a diferença entre: Security Group vs IAM?

### Minha resposta:

> Security Group vai controlar o tráfego de rede que pode chegar na EC2. Security Group é um subconjunto do IAM no sentido dele tratar da parte de autorização especificamente da parte de rede

### Correção:

A primeira parte está correta.

A segunda está incorreta.

Security Group **não é um subconjunto do IAM**.

```text
Security Group
→ controla tráfego de rede

IAM
→ controla permissões de identidades e recursos AWS
```

Regra para memorizar:

> Security Group → "Pode passar esse tráfego?"

> IAM → "Essa identidade pode executar essa ação?"

---

## 6. Qual a diferença entre IP público e IP privado?

Se nossa EC2 estiver na public-a: 10.0.1.0/24 ela terá necessariamente um IP público? E qual é a função do IP privado da EC2 dentro da VPC?

### Minha resposta:

> A EC2 apenas terá o IP público apenas se a propriedade map_public_ip_on_launch for true. O IP privado serve para os diferentes recursos dentro da VPC consigam se comunicar (EC2, bancos de dados, etc)

### Correção:

O conceito do IP privado está correto.

A primeira parte precisa de uma pequena precisão:

`map_public_ip_on_launch = true` faz com que instâncias lançadas na subnet possam receber automaticamente um IPv4 público.

Não é a única forma de obter um IP público.

Uma instância pode receber um endereço público por outros mecanismos, como Elastic IP.

Além disso:

> Uma subnet pública não significa que toda EC2 terá automaticamente um IP público.

---

## 7. Qual a diferença entre IAM User e IAM Role?

No Dia 3 criamos a Role: aws-cloud-practitioner-lab-ec2-s3-read. Por que seria melhor colocar essa Role em uma EC2 do que configurar Access Keys de um IAM User diretamente dentro da máquina?

### Minha resposta:

> A Role foi justamente pensada para ser um mecanismo temporário e apenas utilizada por recursos dentro da AWS, o que evita os perigos de um vazamento de Access Keys que podem ser utilizadas fora indevidamente

### Correção:

Resposta muito boa.

IAM Role pode ser assumida e normalmente fornece **credenciais temporárias**.

Ela é especialmente importante para aplicações executadas em recursos AWS porque evita armazenar Access Keys permanentes.

No caso da EC2:

```text
EC2
 ↓
Instance Profile
 ↓
IAM Role
 ↓
credenciais temporárias
```

---

## 8. Qual a diferença entre Stop e Terminate?

Qual você acha que é a diferença entre: Stop EC2 e Terminate EC2? E qual deles você considera mais apropriado quando queremos encerrar definitivamente uma instância de laboratório?

### Minha resposta:

> O Stop seria como a opção de hibernar/suspender dos computadores, onde a EC2 entraria num estado de consumo mínimo sem executar nada de processamento. O Terminate por sua vez seria como desligar o computador da energia e jogar ele no lixo eletrônico

### Correção:

A analogia ajuda, mas precisa ser refinada.

### Stop

A instância fica parada e pode ser iniciada novamente.

O EBS normalmente permanece.

### Terminate

A instância é encerrada definitivamente e não pode ser iniciada novamente.

O destino do EBS depende de `DeleteOnTermination`.

Para este laboratório:

```text
DeleteOnTermination = true
```

portanto o volume raiz também foi excluído.

---

## 9. O que é necessário para acessar uma EC2 por SSH diretamente pela Internet?

```
Cenário prático
Temos:
Internet
   ↓
Internet Gateway
   ↓
public-a
   ↓
EC2

O que ainda precisamos configurar para conseguirmos acessar essa EC2 pela Internet usando, por exemplo, SSH?
```

### Minha resposta:

> Precisamos configurar uma Route table que envia para o Internet Gateway

### Correção:

Isso é necessário, mas não suficiente.

Para SSH direto pela Internet precisamos, de forma simplificada:

```text
IP público
      +
Subnet pública
      +
Route Table → Internet Gateway
      +
Security Group → TCP/22 permitido
      +
SSH em execução
```

Além disso, a regra de Security Group deve preferencialmente restringir a origem ao IP necessário, evitando:

```text
0.0.0.0/0
```

para SSH.

---

# Pegadinhas da CLF-C02

### EC2

> EC2 = computação.

### AMI

> AMI = imagem/template para lançar a instância.

### Instance Type

> Define recursos computacionais da instância.

### EBS

> EBS = armazenamento em bloco persistente.

### Security Group

> Firewall virtual e stateful.

### IAM

> Controle de permissões AWS.

### Public Subnet

> Ter rota para Internet Gateway não significa que toda EC2 terá IP público.

### Stop

> Instância parada e pode ser iniciada novamente.

### Terminate

> Instância encerrada definitivamente.

### IAM Role

> Preferível a Access Keys permanentes para aplicações executadas em recursos AWS.

---

# Controle de custos

Este laboratório foi realizado em uma conta AWS pessoal.

Foram adotadas as seguintes medidas:

* utilização de `t3.nano` para reduzir o custo;
* utilização de EBS pequeno, com apenas `2 GiB`;
* nenhuma Elastic IP foi criada;
* nenhuma NAT Gateway foi criada;
* nenhum acesso SSH foi configurado;
* recursos temporários foram destruídos ao final;
* a EC2 foi terminada;
* o EBS raiz foi excluído automaticamente;
* o Security Group foi removido;
* recursos relacionados ao Instance Profile foram removidos;
* a associação da policy foi removida.

A principal regra utilizada foi:

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

# Resultado

* [x] Compreendi EC2
* [x] Compreendi AMI
* [x] Compreendi Instance Type
* [x] Compreendi EBS
* [x] Diferenciei EBS de Instance Store
* [x] Compreendi Security Group
* [x] Diferenciei Security Group de IAM
* [x] Compreendi IP privado e público
* [x] Compreendi subnet pública e acesso via Internet Gateway
* [x] Compreendi IAM Role em EC2
* [x] Compreendi Instance Profile
* [x] Pratiquei Stop/Start
* [x] Observei a alteração do IP público após Start
* [x] Pratiquei Terminate
* [x] Observei `DeleteOnTermination`
* [x] Confirmei a exclusão do EBS após Terminate
* [x] Pratiquei `terraform apply -refresh-only`
* [x] Observei Terraform detectando alteração externa
* [x] Pratiquei `terraform destroy`
* [x] Identifiquei limitações de Least Privilege durante o cleanup
* [x] Laboratório limpo ao final

---

# Conclusão

O Day 5 consolidou o funcionamento básico do EC2 e sua relação com outros componentes AWS estudados anteriormente.

O laboratório permitiu observar na prática que:

```text
EC2
 ├── Compute → Instance Type
 ├── Sistema → AMI
 ├── Storage → EBS
 ├── Network → VPC / Subnet / IP / Security Group
 └── Permissions → IAM Role / Instance Profile
```

Também foi possível observar o ciclo de vida:

```text
Launch
  ↓
Running
  ↓
Stop → Start
  ↓
Running
  ↓
Terminate
```

O módulo foi concluído com os recursos temporários destruídos.
