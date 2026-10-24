output "alb_dns_name" {
  description = "Public DNS name of the Application Load Balancer (API entry point)."
  value       = aws_lb.main.dns_name
}

output "api_url" {
  description = "Base URL of the API through the ALB."
  value       = "http://${aws_lb.main.dns_name}"
}

output "ecr_api_repository_url" {
  description = "ECR repository URL for base-api images."
  value       = aws_ecr_repository.api.repository_url
}

output "ecr_consumer_repository_url" {
  description = "ECR repository URL for base-consumer images."
  value       = aws_ecr_repository.consumer.repository_url
}

output "input_queue_url" {
  description = "SQS input queue URL."
  value       = aws_sqs_queue.input.url
}

output "dlq_queue_url" {
  description = "SQS dead-letter queue URL."
  value       = aws_sqs_queue.dlq.url
}

output "ecs_cluster_name" {
  description = "ECS cluster name."
  value       = aws_ecs_cluster.main.name
}

output "autoscaling_group_name" {
  description = "Auto Scaling Group that provides EC2 capacity to the cluster."
  value       = aws_autoscaling_group.ecs.name
}

output "log_groups" {
  description = "CloudWatch log groups created for the project."
  value = [
    aws_cloudwatch_log_group.api.name,
    aws_cloudwatch_log_group.consumer.name,
    aws_cloudwatch_log_group.valkey.name,
    aws_cloudwatch_log_group.external_service.name
  ]
}
