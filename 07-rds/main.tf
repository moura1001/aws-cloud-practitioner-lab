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

data "aws_vpc" "main" {
  filter {
    name   = "tag:Name"
    values = ["aws-cloud-practitioner-lab-vpc"]
  }
}

data "aws_subnet" "private_a" {
  filter {
    name   = "tag:Name"
    values = ["aws-cloud-practitioner-lab-private-a"]
  }
}

data "aws_subnet" "private_b" {
  filter {
    name   = "tag:Name"
    values = ["aws-cloud-practitioner-lab-private-b"]
  }
}

resource "aws_db_subnet_group" "rds" {
  name        = "aws-cloud-practitioner-lab-rds"
  description = "DB subnet group for RDS lab"

  subnet_ids = [
    data.aws_subnet.private_a.id,
    data.aws_subnet.private_b.id
  ]

  tags = {
    Name = "aws-cloud-practitioner-lab-rds"
  }
}

resource "aws_security_group" "rds" {
  name        = "aws-cloud-practitioner-lab-rds"
  description = "Security Group for RDS lab"
  vpc_id      = data.aws_vpc.main.id

  tags = {
    Name = "aws-cloud-practitioner-lab-rds-sg"
  }
}

resource "aws_db_instance" "mysql" {
  identifier = "aws-cloud-practitioner-lab-rds"

  engine         = "mysql"
  engine_version = "8.0"
  instance_class = "db.t4g.micro"

  allocated_storage = 20
  storage_type      = "gp3"
  storage_encrypted = true

  db_name  = "labdb"
  username = "admin"
  password = var.db_password

  db_subnet_group_name   = aws_db_subnet_group.rds.name
  vpc_security_group_ids = [aws_security_group.rds.id]

  publicly_accessible = false
  multi_az            = false

  backup_retention_period = 1

  apply_immediately       = true
  deletion_protection     = false
  skip_final_snapshot     = true

  tags = {
    Name = "aws-cloud-practitioner-lab-rds"
  }
}

resource "aws_iam_policy" "rds" {
  name        = "aws-cloud-practitioner-lab-rds"
  description = "Permissões mínimas para o laboratório de RDS"

  policy = jsonencode({
    Version = "2012-10-17"

    Statement = [
      {
        Effect = "Allow"

        Action = [
          "rds:CreateDBInstance",
          "rds:DeleteDBInstance",
          "rds:ModifyDBInstance",

          "rds:CreateDBSubnetGroup",
          "rds:DeleteDBSubnetGroup",
          "rds:ModifyDBSubnetGroup",

          "rds:DescribeDBInstances",
          "rds:DescribeDBSubnetGroups",
          "rds:DescribeDBSubnetGroup",

          "rds:ListTagsForResource",
          "rds:AddTagsToResource",
          "rds:RemoveTagsFromResource",

          "ec2:CreateSecurityGroup",
          "ec2:DeleteSecurityGroup",
          "ec2:AuthorizeSecurityGroupIngress",
          "ec2:RevokeSecurityGroupIngress",
          "ec2:AuthorizeSecurityGroupEgress",
          "ec2:RevokeSecurityGroupEgress",

          "ec2:DescribeSecurityGroups",
          "ec2:DescribeSubnets",
          "ec2:DescribeVpcs",
          "ec2:DescribeNetworkInterfaces",

          "ec2:CreateTags",
          "ec2:DescribeTags",

          "iam:GetPolicy",
          "iam:GetPolicyVersion",
          "iam:ListAttachedUserPolicies",
          "iam:ListPolicyVersions",
        ]

        Resource = "*"
      },
      {
        Effect = "Allow"

        Action = "iam:CreateServiceLinkedRole"

        Resource = "arn:aws:iam::*:role/aws-service-role/rds.amazonaws.com/AWSServiceRoleForRDS"

        Condition = {
          StringLike = {
            "iam:AWSServiceName" = "rds.amazonaws.com"
          }
        }
      }
    ]
  })
}

resource "aws_iam_user_policy_attachment" "rds" {
  user       = "aws-cloud-practitioner-lab"
  policy_arn = aws_iam_policy.rds.arn
}
