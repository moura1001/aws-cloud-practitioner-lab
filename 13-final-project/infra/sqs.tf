# Dead-letter queue for messages that fail repeatedly.
resource "aws_sqs_queue" "dlq" {
  name                       = "${var.project_name}-final-dlq"
  message_retention_seconds  = 1209600 # 14 days
  visibility_timeout_seconds = 90
  sqs_managed_sse_enabled    = true

  tags = {
    Name = "${var.project_name}-final-dlq"
  }
}

# Main input queue. The API publishes here and the consumer polls it.
resource "aws_sqs_queue" "input" {
  name                       = "${var.project_name}-final-input"
  visibility_timeout_seconds = 90
  message_retention_seconds  = 345600 # 4 days
  receive_wait_time_seconds  = 10     # long polling
  sqs_managed_sse_enabled    = true

  redrive_policy = jsonencode({
    deadLetterTargetArn = aws_sqs_queue.dlq.arn
    maxReceiveCount     = var.sqs_max_receive_count
  })

  tags = {
    Name = "${var.project_name}-final-input"
  }
}
