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

data "aws_caller_identity" "current" {}

locals {
  bucket_name = "aws-cloud-practitioner-lab-s3-${data.aws_caller_identity.current.account_id}"
}

resource "aws_s3_bucket" "lab" {
  bucket        = local.bucket_name
  force_destroy = true

  tags = {
    Name = "aws-cloud-practitioner-lab-s3"
  }
}

resource "aws_s3_bucket_public_access_block" "lab" {
  bucket = aws_s3_bucket.lab.id

  block_public_acls       = true
  block_public_policy     = true
  ignore_public_acls      = true
  restrict_public_buckets = true
}

resource "aws_s3_bucket_versioning" "lab" {
  bucket = aws_s3_bucket.lab.id

  versioning_configuration {
    status = "Enabled"
  }
}

resource "aws_iam_policy" "s3" {
  name        = "aws-cloud-practitioner-lab-s3"
  description = "Permissions for the AWS Cloud Practitioner S3 lab"

  policy = jsonencode({
    Version = "2012-10-17"

    Statement = [
      {
        Sid    = "BucketCreation"
        Effect = "Allow"

        Action = [
          "s3:CreateBucket"
        ]

        Resource = "*"
      },
      {
        Sid    = "BucketManagement"
        Effect = "Allow"

        Action = [
          "s3:DeleteBucket",
          "s3:ListBucket",
          "s3:GetBucketVersioning",
          "s3:PutBucketVersioning",
          "s3:GetBucketPublicAccessBlock",
          "s3:PutBucketPublicAccessBlock",
          "s3:GetBucketTagging",
          "s3:PutBucketTagging",
          "s3:GetBucketPolicy",
          "s3:GetBucketAcl",
          "s3:GetBucketCORS",
          "s3:GetBucketWebsite",
          "s3:GetAccelerateConfiguration",
          "s3:GetBucketRequestPayment",
          "s3:GetBucketLogging",
          "s3:GetLifecycleConfiguration",
          "s3:GetReplicationConfiguration",
          "s3:GetEncryptionConfiguration",
          "s3:GetBucketObjectLockConfiguration",
          "s3:ListBucketVersions",
        ]

        Resource = "arn:aws:s3:::${local.bucket_name}"
      },
      {
        Sid    = "ObjectManagement"
        Effect = "Allow"

        Action = [
          "s3:GetObject",
          "s3:PutObject",
          "s3:DeleteObject",
          "s3:DeleteObjectVersion",
          "s3:GetObjectVersion",
        ]

        Resource = "arn:aws:s3:::${local.bucket_name}/*"
      }
    ]
  })
}

resource "aws_iam_user_policy_attachment" "s3" {
  user       = "aws-cloud-practitioner-lab"
  policy_arn = aws_iam_policy.s3.arn
}
