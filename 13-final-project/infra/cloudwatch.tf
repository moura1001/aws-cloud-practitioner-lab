resource "aws_cloudwatch_log_group" "api" {
  name              = "/aws/ecs/${var.project_name}/final/api"
  retention_in_days = var.log_retention_days

  tags = {
    Name = "${var.project_name}-final-api-logs"
  }
}

resource "aws_cloudwatch_log_group" "consumer" {
  name              = "/aws/ecs/${var.project_name}/final/consumer"
  retention_in_days = var.log_retention_days

  tags = {
    Name = "${var.project_name}-final-consumer-logs"
  }
}

resource "aws_cloudwatch_log_group" "valkey" {
  name              = "/aws/ecs/${var.project_name}/final/valkey"
  retention_in_days = var.log_retention_days

  tags = {
    Name = "${var.project_name}-final-valkey-logs"
  }
}

resource "aws_cloudwatch_log_group" "external_service" {
  name              = "/aws/ecs/${var.project_name}/final/external-service"
  retention_in_days = var.log_retention_days

  tags = {
    Name = "${var.project_name}-final-external-service-logs"
  }
}

# Backlog alarm: many visible messages means the consumer cannot keep up or the
# external service is failing. Use it as the trigger for scaling out consumers.
resource "aws_cloudwatch_metric_alarm" "sqs_backlog" {
  alarm_name          = "${var.project_name}-final-sqs-backlog"
  alarm_description   = "Visible messages in the input queue above ${var.sqs_backlog_alarm_threshold}"
  namespace           = "AWS/SQS"
  metric_name         = "ApproximateNumberOfMessagesVisible"
  statistic           = "Maximum"
  period              = 60
  evaluation_periods  = 3
  threshold           = var.sqs_backlog_alarm_threshold
  comparison_operator = "GreaterThanThreshold"
  treat_missing_data  = "notBreaching"

  dimensions = {
    QueueName = aws_sqs_queue.input.name
  }

  tags = {
    Name = "${var.project_name}-final-sqs-backlog"
  }
}

# Any message in the DLQ deserves attention.
resource "aws_cloudwatch_metric_alarm" "dlq_not_empty" {
  alarm_name          = "${var.project_name}-final-dlq-not-empty"
  alarm_description   = "At least one message reached the dead-letter queue"
  namespace           = "AWS/SQS"
  metric_name         = "ApproximateNumberOfMessagesVisible"
  statistic           = "Maximum"
  period              = 60
  evaluation_periods  = 1
  threshold           = 1
  comparison_operator = "GreaterThanOrEqualToThreshold"
  treat_missing_data  = "notBreaching"

  dimensions = {
    QueueName = aws_sqs_queue.dlq.name
  }

  tags = {
    Name = "${var.project_name}-final-dlq-not-empty"
  }
}
