# Runbook — Teste ponta a ponta e teardown

Guia operacional para subir, testar e destruir o projeto final na AWS
(`sa-east-1`), validando o fluxo **API → SQS → consumer → HTTP externo → Valkey**.

> Aviso de custo: o `terraform apply` cria ALB + EC2 + EBS + ECR + SSM +
> CloudWatch, que **cobram por hora** enquanto existirem. Sempre execute o
> teardown ao terminar.

## Pré-requisitos

- Terraform >= 1.5 e AWS CLI v2 autenticado como `aws-cloud-practitioner-lab`.
- Docker em execução (BuildKit ativo — os Dockerfiles usam `RUN --mount=type=cache`).
- **`AdministratorAccess` anexado no primeiro `apply`** (bootstrap: ele cria a
  policy scoped `aws-cloud-practitioner-lab-final` e a anexa ao usuário).
- VPC e sub-redes públicas do laboratório `04-vpc` existentes.

```bash
export AWS_REGION=sa-east-1 AWS_DEFAULT_REGION=sa-east-1
ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text)
REGISTRY="$ACCOUNT_ID.dkr.ecr.sa-east-1.amazonaws.com"
```

## Fase 1 — Pre-flight (sem criar recursos)

```bash
cd ~/Documentos/github/aws-cloud-practitioner-lab/13-final-project/infra
terraform init
terraform fmt -check
terraform validate
terraform plan
```
Esperado em instalação limpa: `Plan: 45 to add, 0 to change, 0 to destroy`.

## Fase 2 — Criar o ECR e enviar as imagens

Os serviços ECS só conseguem puxar a imagem se ela já existir no ECR. Crie
primeiro apenas os repositórios:

```bash
terraform apply -target=aws_ecr_repository.api -target=aws_ecr_repository.consumer

aws ecr get-login-password --region sa-east-1 \
  | docker login --username AWS --password-stdin "$REGISTRY"

cd ../final_project
docker build -f docker/dev/Dockerfile-api -t "$REGISTRY/aws-cloud-practitioner-lab-final-api:latest" .
docker build -f docker/dev/Dockerfile     -t "$REGISTRY/aws-cloud-practitioner-lab-final-consumer:latest" .
docker push "$REGISTRY/aws-cloud-practitioner-lab-final-api:latest"
docker push "$REGISTRY/aws-cloud-practitioner-lab-final-consumer:latest"

aws ecr describe-images --repository-name aws-cloud-practitioner-lab-final-api \
  --query 'imageDetails[].imageTags' --output json
aws ecr describe-images --repository-name aws-cloud-practitioner-lab-final-consumer \
  --query 'imageDetails[].imageTags' --output json
```

> Se você já rodou o `apply` completo antes (os repositórios existem), pode pular
> o `-target`, fazer o build/push e só então rodar o `apply` restante. Se preferir
> aplicar tudo primeiro, force um novo deploy depois do push:
> `aws ecs update-service --cluster aws-cloud-practitioner-lab-final --service base-api --force-new-deployment`
> (idem `base-consumer`).

## Fase 3 — Aplicar a infraestrutura completa

```bash
cd ../infra
terraform apply
```
Cria ALB, SQS, ECS (cluster/ASG/tasks/services), Valkey, mock, logs, alarmes,
roles IAM e a policy de deploy scoped.

## Fase 4 — Aguardar estabilizar

```bash
CLUSTER=aws-cloud-practitioner-lab-final
aws ecs wait services-stable --cluster $CLUSTER \
  --services base-api base-consumer valkey external-service

aws ecs describe-services --cluster $CLUSTER \
  --services base-api base-consumer valkey external-service \
  --query 'services[].{name:serviceName,desired:desiredCount,running:runningCount}' --output table
```
> Pode levar 1–3 min. Enquanto as tarefas não estiverem `RUNNING`, o ALB responde **502**.

## Fase 5 — Teste de requisição no ALB

```bash
ALB=$(terraform output -raw alb_dns_name)
echo "ALB: http://$ALB"

# 5.1 health (rota pública, sem API key)
curl -s -w '\nHTTP %{http_code}\n' "http://$ALB/health"          # {"status":"UP"}

# 5.2 API key (segredo SSM)
API_KEY=$(aws ssm get-parameter --name /practitioner-lab/final/api-key \
  --with-decryption --query Parameter.Value --output text)

# 5.3 publicar um evento
curl -s -w '\nHTTP %{http_code}\n' -X POST "http://$ALB/events" \
  -H "Content-Type: application/json" \
  -H "X-API-Key: $API_KEY" \
  -d '{"id":"evt-1","type":"TEST_EVENT","data":"sample1"}'        # HTTP 202
```

**5.4** confirmar o processamento (fila esvazia, DLQ vazia, mock recebeu `/process`):

```bash
IN=$(terraform output -raw input_queue_url)
DL=$(terraform output -raw dlq_queue_url)

aws sqs get-queue-attributes --queue-url "$IN" \
  --attribute-names ApproximateNumberOfMessages ApproximateNumberOfMessagesNotVisible --query 'Attributes'
aws sqs get-queue-attributes --queue-url "$DL" \
  --attribute-names ApproximateNumberOfMessages --query 'Attributes'

# logs (o mock deve registrar um POST /process 200)
aws logs tail /aws/ecs/aws-cloud-practitioner-lab/final/external-service --since 10m --format short
aws logs tail /aws/ecs/aws-cloud-practitioner-lab/final/consumer --since 10m --format short
```

**5.5 (opcional) DLQ + circuit breaker** — derrube o mock e envie eventos:

```bash
aws ecs update-service --cluster $CLUSTER --service external-service --desired-count 0
for i in 2 3 4 5; do
  curl -s -o /dev/null -X POST "http://$ALB/events" -H "Content-Type: application/json" \
    -H "X-API-Key: $API_KEY" -d "{\"id\":\"evt-$i\",\"type\":\"TEST_EVENT\",\"data\":\"fail$i\"}"
done
# após ~2-3 min (retry + visibility timeout + maxReceiveCount):
aws sqs get-queue-attributes --queue-url "$DL" --attribute-names ApproximateNumberOfMessages --query 'Attributes'
aws ecs update-service --cluster $CLUSTER --service external-service --desired-count 1
```

## Fase 6 — (Opcional) Validar o least privilege

Com a infra no ar e o `AdministratorAccess` ainda anexado:

1. Remova o `AdministratorAccess` (IAM → Users → `aws-cloud-practitioner-lab`).
2. Valide que a policy scoped basta, sem alterar nada:
   ```bash
   terraform plan      # read-only; deve funcionar e mostrar "No changes"
   ```
3. **Antes de destruir, reanexe o `AdministratorAccess`.**
   O `terraform destroy` também remove a policy scoped; se ela for a única
   permissão, a desanexação no meio do destroy faria as remoções seguintes
   (ELB/SSM/IAM) falharem e deixariam recursos órfãos.

## Fase 7 — Destruir

```bash
cd ~/Documentos/github/aws-cloud-practitioner-lab/13-final-project/infra
terraform destroy
```
Esperado: `Destroy complete! Resources: 45 destroyed.`

## Fase 8 — Validar que tudo foi destruído

Cada lista deve sair **vazia** (ou sem o prefixo do projeto):

```bash
export AWS_REGION=sa-east-1
echo "ALB:";       aws elbv2 describe-load-balancers --query 'LoadBalancers[].LoadBalancerName'
echo "TG:";        aws elbv2 describe-target-groups --query 'TargetGroups[].TargetGroupName'
echo "ASG:";       aws autoscaling describe-auto-scaling-groups --query 'AutoScalingGroups[].AutoScalingGroupName'
echo "EC2:";       aws ec2 describe-instances --filters Name=instance-state-name,Values=running,pending,stopping,stopped --query 'Reservations[].Instances[].InstanceId'
echo "ECS:";       aws ecs list-clusters --query 'clusterArns'
echo "ECR:";       aws ecr describe-repositories --query 'repositories[].repositoryName'
echo "SQS:";       aws sqs list-queues --query 'QueueUrls'
echo "Logs:";      aws logs describe-log-groups --query 'logGroups[].logGroupName'
echo "Alarms:";    aws cloudwatch describe-alarms --query 'MetricAlarms[].AlarmName'
echo "SSM:";       aws ssm describe-parameters --query 'Parameters[].Name'
echo "Roles:";     aws iam list-roles --query 'Roles[?starts_with(RoleName,`aws-cloud-practitioner-lab-final`)].RoleName'
echo "Profiles:";  aws iam list-instance-profiles --query 'InstanceProfiles[?starts_with(InstanceProfileName,`aws-cloud-practitioner-lab-final`)].InstanceProfileName'
echo "Policies:";  aws iam list-policies --scope Local --query 'Policies[?starts_with(PolicyName,`aws-cloud-practitioner-lab-final`)].PolicyName'
echo "SGs:";       aws ec2 describe-security-groups --filters Name=group-name,Values='aws-cloud-practitioner-lab-final-*' --query 'SecurityGroups[].GroupName'
echo "Volumes:";   aws ec2 describe-volumes --filters Name=status,Values=available --query 'Volumes[].VolumeId'
```
> **Deve permanecer:** a VPC/sub-redes do lab `04-vpc` (reutilizadas) e as
> policies antigas dos labs. Confirme no **Cost Explorer** que ALB/EC2 pararam
> de acumular custo.

## Solução de problemas

- **502 no curl:** API ainda não saudável — aguarde a Fase 4 e veja
  `aws logs tail /aws/ecs/aws-cloud-practitioner-lab/final/api`.
- **Task reiniciando / "CannotPullContainerImage":** imagem ausente no ECR ou
  tag diferente de `latest`; reenvie e force
  `aws ecs update-service --cluster $CLUSTER --service base-api --force-new-deployment`
  (idem `base-consumer`).
- **`services-stable` estoura timeout:** veja logs do consumer; se faltar RAM na
  instância, suba `instance_type = "t3.medium"`.
- **`RegisterTaskDefinition ... memory required`:** todo container no launch type
  EC2 precisa de `memory`/`memoryReservation` (já configurado).
- **`PutParameter ... reserved parameter name`:** nomes SSM não podem começar
  com `aws`/`ssm` (usamos `/practitioner-lab/final/...`).
- **`CreatePolicy ... PolicySize`:** a policy de deploy deve ficar < 6144 chars
  (está em ~5.8 KB).
- **`docker build` reclama de `--mount`:** habilite BuildKit (`export DOCKER_BUILDKIT=1`).
- **`destroy` com erro de IAM:** reanexe o `AdministratorAccess` e rode `terraform destroy` novamente.
