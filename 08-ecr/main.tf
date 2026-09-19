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

resource "aws_ecr_repository" "app" {
  name = "aws-cloud-practitioner-lab"

  image_tag_mutability = "MUTABLE"

  tags = {
    Name = "aws-cloud-practitioner-lab-ecr"
  }
}

resource "aws_iam_policy" "ecr" {
  name        = "aws-cloud-practitioner-lab-ecr"
  description = "Permissões mínimas para o laboratório de ECR"

  policy = jsonencode({
    Version = "2012-10-17"

    Statement = [
      {
        Effect = "Allow"

        Action = [
          "ecr:CreateRepository",
          "ecr:DeleteRepository",
          "ecr:DescribeRepositories",
          "ecr:ListTagsForResource",
          "ecr:TagResource",
          "ecr:UntagResource",
          "ecr:DescribeImages",
          "ecr:ListImages",
        ]

        Resource = "arn:aws:ecr:sa-east-1:*:repository/aws-cloud-practitioner-lab"
      },
      {
        Effect = "Allow"

        Action = [
          "ecr:GetAuthorizationToken"
        ]

        Resource = "*"
      },
      {
        Effect = "Allow"

        Action = [
          "ecr:BatchCheckLayerAvailability",
          "ecr:BatchGetImage",
          "ecr:CompleteLayerUpload",
          "ecr:GetDownloadUrlForLayer",
          "ecr:InitiateLayerUpload",
          "ecr:PutImage",
          "ecr:UploadLayerPart",
          "ecr:BatchDeleteImage",
        ]

        Resource = "arn:aws:ecr:sa-east-1:*:repository/aws-cloud-practitioner-lab"
      }
    ]
  })
}

resource "aws_iam_user_policy_attachment" "ecr" {
  user       = "aws-cloud-practitioner-lab"
  policy_arn = aws_iam_policy.ecr.arn
}
