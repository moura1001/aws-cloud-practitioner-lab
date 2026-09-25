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

data "aws_region" "current" {}

resource "aws_sqs_queue" "app" {
  name = "aws-cloud-practitioner-lab-cloudwatch"

  visibility_timeout_seconds = 30
  message_retention_seconds  = 86400
  receive_wait_time_seconds  = 10

  depends_on = [
    aws_iam_user_policy_attachment.cloudwatch
  ]

  tags = {
    Name = "aws-cloud-practitioner-lab-cloudwatch-sqs"
  }
}

resource "aws_iam_policy" "cloudwatch" {
  name        = "aws-cloud-practitioner-lab-cloudwatch"
  description = "Permissões mínimas para o laboratório de CloudWatch com SQS"

  policy = jsonencode({
    Version = "2012-10-17"

    Statement = [
      {
        Effect = "Allow"

        Action = [
          "sqs:CreateQueue",
          "sqs:DeleteQueue",
          "sqs:GetQueueAttributes",
          "sqs:GetQueueUrl",
          "sqs:ListQueueTags",
          "sqs:TagQueue",
          "sqs:UntagQueue",
          "sqs:SendMessage",
          "sqs:ReceiveMessage",
          "sqs:DeleteMessage",
          "sqs:ChangeMessageVisibility"
        ]

        Resource = "arn:aws:sqs:${data.aws_region.current.region}:${data.aws_caller_identity.current.account_id}:aws-cloud-practitioner-lab-cloudwatch"
      },
      {
        Effect = "Allow"

        Action = [
          "cloudwatch:PutMetricAlarm",
          "cloudwatch:DescribeAlarms",
          "cloudwatch:DeleteAlarms",
          "cloudwatch:ListTagsForResource"
        ]

        Resource = "*"
      }
    ]
  })
}

resource "aws_iam_user_policy_attachment" "cloudwatch" {
  user       = "aws-cloud-practitioner-lab"
  policy_arn = aws_iam_policy.cloudwatch.arn
}

resource "aws_cloudwatch_metric_alarm" "sqs_messages" {
  alarm_name          = "aws-cloud-practitioner-lab-sqs-messages"
  alarm_description   = "Alarme quando houver mensagens visíveis na fila do laboratório"
  namespace           = "AWS/SQS"
  metric_name         = "ApproximateNumberOfMessagesVisible"
  statistic           = "Maximum"
  period              = 60
  evaluation_periods  = 1
  threshold           = 1
  comparison_operator = "GreaterThanOrEqualToThreshold"

  dimensions = {
    QueueName = aws_sqs_queue.app.name
  }

  treat_missing_data = "notBreaching"

  depends_on = [
    aws_iam_user_policy_attachment.cloudwatch
  ]
}
