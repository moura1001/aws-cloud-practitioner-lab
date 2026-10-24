# ---------------------------------------------------------------------------
# ECS EC2 instance role (used by the ECS agent on the capacity instances)
# ---------------------------------------------------------------------------
resource "aws_iam_role" "ecs_instance" {
  name = "${var.project_name}-final-ec2"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Effect    = "Allow"
      Principal = { Service = "ec2.amazonaws.com" }
      Action    = "sts:AssumeRole"
    }]
  })

  tags = {
    Name = "${var.project_name}-final-ec2"
  }
}

resource "aws_iam_role_policy_attachment" "ecs_instance_ecs" {
  role       = aws_iam_role.ecs_instance.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonEC2ContainerServiceforEC2Role"
}

resource "aws_iam_instance_profile" "ecs" {
  name = "${var.project_name}-final-ec2"
  role = aws_iam_role.ecs_instance.name
}

# ---------------------------------------------------------------------------
# ECS task execution role (pull images from ECR, write logs, read secrets)
# ---------------------------------------------------------------------------
resource "aws_iam_role" "task_execution" {
  name = "${var.project_name}-final-task-execution"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Effect    = "Allow"
      Principal = { Service = "ecs-tasks.amazonaws.com" }
      Action    = "sts:AssumeRole"
    }]
  })

  tags = {
    Name = "${var.project_name}-final-task-execution"
  }
}

resource "aws_iam_role_policy_attachment" "task_execution_managed" {
  role       = aws_iam_role.task_execution.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
}

# Read only the two parameters this project owns.
resource "aws_iam_role_policy" "task_execution_secrets" {
  name = "${var.project_name}-final-task-execution-secrets"
  role = aws_iam_role.task_execution.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Sid    = "ReadProjectSecrets"
        Effect = "Allow"
        Action = [
          "ssm:GetParameters",
          "ssm:GetParameter"
        ]
        Resource = [
          aws_ssm_parameter.api_key.arn,
          aws_ssm_parameter.valkey_url.arn,
          aws_ssm_parameter.valkey_password.arn
        ]
      },
      {
        Sid      = "DecryptProjectSecrets"
        Effect   = "Allow"
        Action   = "kms:Decrypt"
        Resource = "*"
        Condition = {
          StringEquals = {
            "kms:ViaService" = "ssm.${local.region}.amazonaws.com"
          }
        }
      }
    ]
  })
}

# ---------------------------------------------------------------------------
# Task roles (permissions granted to the application code, least privilege)
# ---------------------------------------------------------------------------
resource "aws_iam_role" "api_task" {
  name = "${var.project_name}-final-api-task"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Effect    = "Allow"
      Principal = { Service = "ecs-tasks.amazonaws.com" }
      Action    = "sts:AssumeRole"
    }]
  })

  tags = {
    Name = "${var.project_name}-final-api-task"
  }
}

resource "aws_iam_role_policy" "api_task_sqs" {
  name = "${var.project_name}-final-api-task-sqs"
  role = aws_iam_role.api_task.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Sid    = "PublishToInputQueue"
      Effect = "Allow"
      Action = [
        "sqs:SendMessage",
        "sqs:GetQueueUrl",
        "sqs:GetQueueAttributes"
      ]
      Resource = aws_sqs_queue.input.arn
    }]
  })
}

resource "aws_iam_role" "consumer_task" {
  name = "${var.project_name}-final-consumer-task"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Effect    = "Allow"
      Principal = { Service = "ecs-tasks.amazonaws.com" }
      Action    = "sts:AssumeRole"
    }]
  })

  tags = {
    Name = "${var.project_name}-final-consumer-task"
  }
}

resource "aws_iam_role_policy" "consumer_task_sqs" {
  name = "${var.project_name}-final-consumer-task-sqs"
  role = aws_iam_role.consumer_task.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Sid    = "ConsumeQueues"
      Effect = "Allow"
      Action = [
        "sqs:ReceiveMessage",
        "sqs:DeleteMessage",
        "sqs:ChangeMessageVisibility",
        "sqs:GetQueueAttributes",
        "sqs:GetQueueUrl"
      ]
      Resource = [
        aws_sqs_queue.input.arn,
        aws_sqs_queue.dlq.arn
      ]
    }]
  })
}
