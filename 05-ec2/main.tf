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

data "aws_subnet" "public_a" {
  filter {
    name   = "tag:Name"
    values = ["aws-cloud-practitioner-lab-public-a"]
  }
}

data "aws_iam_role" "ec2" {
  name = "aws-cloud-practitioner-lab-ec2-s3-read"
}

resource "aws_security_group" "ec2" {
  name        = "aws-cloud-practitioner-lab-ec2"
  description = "Security Group for EC2 lab"
  vpc_id      = data.aws_vpc.main.id

  tags = {
    Name = "aws-cloud-practitioner-lab-ec2-sg"
  }
}

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

resource "aws_iam_instance_profile" "ec2" {
  name = "aws-cloud-practitioner-lab-ec2"
  role = data.aws_iam_role.ec2.name
}

resource "aws_instance" "ec2" {
  ami           = data.aws_ami.amazon_linux.id
  instance_type = "t3.nano"

  subnet_id = data.aws_subnet.public_a.id

  vpc_security_group_ids = [
    aws_security_group.ec2.id
  ]

  iam_instance_profile = aws_iam_instance_profile.ec2.name

  tags = {
    Name = "aws-cloud-practitioner-lab-ec2"
  }
}

resource "aws_iam_policy" "ec2" {
  name        = "aws-cloud-practitioner-lab-ec2"
  description = "Permissões mínimas para o laboratório de EC2"

  policy = jsonencode({
    Version = "2012-10-17"

    Statement = [
      {
        Effect = "Allow"

        Action = [
          "ec2:RunInstances",
          "ec2:TerminateInstances",
          "ec2:StopInstances",
          "ec2:StartInstances",
          "ec2:DescribeInstanceAttribute",
          "ec2:DescribeInstanceCreditSpecifications",

          "ec2:CreateSecurityGroup",
          "ec2:DeleteSecurityGroup",
          "ec2:AuthorizeSecurityGroupIngress",
          "ec2:RevokeSecurityGroupIngress",
          "ec2:RevokeSecurityGroupEgress",

          "ec2:DescribeInstances",
          "ec2:DescribeImages",
          "ec2:DescribeInstanceTypes",
          "ec2:DescribeSecurityGroups",
          "ec2:DescribeSubnets",
          "ec2:DescribeVpcs",
          "ec2:DescribeVolumes",
          "ec2:DescribeNetworkInterfaces",

          "ec2:DescribeTags",
          "ec2:CreateTags",

          "iam:GetRole",
          "iam:PassRole",
          "iam:GetPolicy",
          "iam:GetPolicyVersion",
          "iam:ListAttachedUserPolicies",
          "iam:CreateInstanceProfile",
          "iam:AddRoleToInstanceProfile",
          "iam:GetInstanceProfile",
          "iam:DeleteInstanceProfile",
          "iam:RemoveRoleFromInstanceProfile",
          "iam:ListPolicyVersions",
        ]

        Resource = "*"
      }
    ]
  })
}

resource "aws_iam_user_policy_attachment" "ec2" {
  user       = "aws-cloud-practitioner-lab"
  policy_arn = aws_iam_policy.ec2.arn
}
