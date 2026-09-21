terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 6.0"
    }
  }

  required_version = ">= 1.5.0"
}

provider "aws" {
  region = "sa-east-1"
}

data "aws_vpc" "lab" {
  filter {
    name   = "tag:Name"
    values = ["aws-cloud-practitioner-lab-vpc"]
  }
}

data "aws_subnets" "public" {
  filter {
    name   = "vpc-id"
    values = [data.aws_vpc.lab.id]
  }

  filter {
    name = "tag:Name"
    values = [
      "aws-cloud-practitioner-lab-public-a",
      "aws-cloud-practitioner-lab-public-b"
    ]
  }
}

data "aws_ecr_repository" "app" {
  name = "aws-cloud-practitioner-lab"
}

data "aws_caller_identity" "current" {}

resource "aws_iam_policy" "ecs" {
  name        = "aws-cloud-practitioner-lab-ecs"
  description = "Permissions for the AWS Cloud Practitioner ECS lab"

  policy = jsonencode({
    Version = "2012-10-17"

    Statement = [
      {
        Sid    = "ECSManagement"
        Effect = "Allow"

        Action = [
          "ecs:CreateCluster",
          "ecs:DescribeClusters",
          "ecs:DeleteCluster",
          "ecs:ListClusters",

          "ecs:RegisterTaskDefinition",
          "ecs:DescribeTaskDefinition",
          "ecs:DeregisterTaskDefinition",
          "ecs:ListTaskDefinitions",

          "ecs:CreateService",
          "ecs:DescribeServices",
          "ecs:UpdateService",
          "ecs:DeleteService",
          "ecs:ListServices",

          "ecs:ListTasks",
          "ecs:DescribeTasks",
          "ecs:StopTask",

          "ecs:TagResource",
          "ecs:UntagResource"
        ]

        Resource = "*"
      },

      {
        Sid    = "ECSTaskExecutionRole"
        Effect = "Allow"

        Action = [
          "iam:CreateRole",
          "iam:DeleteRole",
          "iam:GetRole",
          "iam:ListRolePolicies",
          "iam:ListAttachedRolePolicies",
          "iam:AttachRolePolicy",
          "iam:DetachRolePolicy",
          "iam:PassRole",
          "iam:TagRole",
          "iam:ListInstanceProfilesForRole",
        ]

        Resource = "arn:aws:iam::${data.aws_caller_identity.current.account_id}:role/aws-cloud-practitioner-lab-ecs-task-execution"
      },

      {
        "Sid": "ECSServiceLinkedRole",
        "Effect": "Allow",
        "Action": "iam:CreateServiceLinkedRole",
        "Resource": "arn:aws:iam::*:role/aws-service-role/ecs.amazonaws.com/AWSServiceRoleForECS*",
        "Condition": {
            "StringLike": {
            "iam:AWSServiceName": "ecs.amazonaws.com"
            }
        }
      },

      {
        Sid    = "CloudWatchLogsManagement"
        Effect = "Allow"

        Action = [
          "logs:CreateLogGroup",
          "logs:DescribeLogGroups",
          "logs:PutRetentionPolicy",
          "logs:DeleteLogGroup",
          "logs:ListTagsForResource",
          "logs:TagResource",
          "logs:UntagResource",
          "logs:DescribeLogStreams",
        ]

        Resource = "*"
      },

      {
        Sid    = "EC2SecurityGroupManagement"
        Effect = "Allow"

        Action = [
          "ec2:CreateSecurityGroup",
          "ec2:DescribeSecurityGroups",
          "ec2:DeleteSecurityGroup",
          "ec2:AuthorizeSecurityGroupEgress",
          "ec2:RevokeSecurityGroupEgress",
          "ec2:CreateTags",
          "ec2:DeleteTags"
        ]

        Resource = "*"
      }
    ]
  })
}

resource "aws_iam_user_policy_attachment" "ecs" {
  user       = "aws-cloud-practitioner-lab"
  policy_arn = aws_iam_policy.ecs.arn
}

resource "aws_cloudwatch_log_group" "ecs" {
  name              = "/aws/ecs/aws-cloud-practitioner-lab"
  retention_in_days = 1
}

resource "aws_iam_role" "ecs_task_execution" {
  name = "aws-cloud-practitioner-lab-ecs-task-execution"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"

    Statement = [
      {
        Effect = "Allow"

        Principal = {
          Service = "ecs-tasks.amazonaws.com"
        }

        Action = "sts:AssumeRole"
      }
    ]
  })

  tags = {
    Name = "aws-cloud-practitioner-lab-ecs-task-execution"
  }
}

resource "aws_iam_role_policy_attachment" "ecs_task_execution" {
  role       = aws_iam_role.ecs_task_execution.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
}

resource "aws_security_group" "ecs" {
  name        = "aws-cloud-practitioner-lab-ecs"
  description = "Security group for ECS Fargate tasks"
  vpc_id      = data.aws_vpc.lab.id

  egress {
    description = "Allow outbound traffic"
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "aws-cloud-practitioner-lab-ecs"
  }
}

resource "aws_ecs_cluster" "main" {
  name = "aws-cloud-practitioner-lab"

  tags = {
    Name = "aws-cloud-practitioner-lab-ecs"
  }
}

resource "aws_ecs_task_definition" "app" {
  family                   = "aws-cloud-practitioner-lab"
  requires_compatibilities = ["FARGATE"]
  network_mode             = "awsvpc"
  cpu                      = "256"
  memory                   = "512"

  execution_role_arn = aws_iam_role.ecs_task_execution.arn

  container_definitions = jsonencode([
    {
      name  = "aws-cloud-practitioner-lab"
      image = "${data.aws_ecr_repository.app.repository_url}:1.0"

      essential = true

      logConfiguration = {
        logDriver = "awslogs"

        options = {
          awslogs-group         = aws_cloudwatch_log_group.ecs.name
          awslogs-region        = "sa-east-1"
          awslogs-stream-prefix = "ecs"
        }
      }
    }
  ])

  tags = {
    Name = "aws-cloud-practitioner-lab-ecs-task"
  }
}

resource "aws_ecs_service" "app" {
  name            = "aws-cloud-practitioner-lab"
  cluster         = aws_ecs_cluster.main.id
  task_definition = aws_ecs_task_definition.app.arn

  desired_count = 1

  launch_type = "FARGATE"

  network_configuration {
    subnets = data.aws_subnets.public.ids

    security_groups = [
      aws_security_group.ecs.id
    ]

    assign_public_ip = true
  }

  tags = {
    Name = "aws-cloud-practitioner-lab-ecs-service"
  }
}
