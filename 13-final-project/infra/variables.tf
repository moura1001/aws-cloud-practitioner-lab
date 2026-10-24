variable "aws_region" {
  description = "AWS region. The lab is restricted to sa-east-1."
  type        = string
  default     = "sa-east-1"

  validation {
    condition     = var.aws_region == "sa-east-1"
    error_message = "This lab must run in sa-east-1 only."
  }
}

variable "project_name" {
  description = "Name prefix used for all resources created by this project."
  type        = string
  default     = "aws-cloud-practitioner-lab"
}

variable "vpc_name" {
  description = "Name tag of the existing VPC created by the VPC lab (04-vpc). Reused, never recreated."
  type        = string
  default     = "aws-cloud-practitioner-lab-vpc"
}

variable "public_subnet_name_a" {
  description = "Name tag of the existing public subnet in sa-east-1a."
  type        = string
  default     = "aws-cloud-practitioner-lab-public-a"
}

variable "public_subnet_name_b" {
  description = "Name tag of the existing public subnet in sa-east-1b."
  type        = string
  default     = "aws-cloud-practitioner-lab-public-b"
}

variable "instance_type" {
  description = "EC2 instance type used as ECS capacity. t3.small is the recommended minimum for the four containers."
  type        = string
  default     = "t3.small"
}

variable "asg_min_size" {
  description = "Minimum number of EC2 instances in the ECS capacity Auto Scaling Group."
  type        = number
  default     = 1
}

variable "asg_desired_capacity" {
  description = "Desired number of EC2 instances in the ECS capacity Auto Scaling Group."
  type        = number
  default     = 1
}

variable "asg_max_size" {
  description = "Maximum number of EC2 instances. Increase together with the consumer desired count to scale out."
  type        = number
  default     = 2
}

variable "api_image_tag" {
  description = "Image tag of base-api pushed to ECR."
  type        = string
  default     = "latest"
}

variable "consumer_image_tag" {
  description = "Image tag of base-consumer pushed to ECR."
  type        = string
  default     = "latest"
}

variable "valkey_image" {
  description = "Valkey (Redis-compatible) container image used as the private state/idempotency store."
  type        = string
  default     = "valkey/valkey:7"
}

variable "external_service_image" {
  description = "Container image that simulates the external HTTP service (process/health)."
  type        = string
  default     = "mendhak/http-https-echo:latest"
}

variable "log_retention_days" {
  description = "CloudWatch Logs retention in days. Keep it short to control cost."
  type        = number
  default     = 3
}

variable "sqs_backlog_alarm_threshold" {
  description = "ApproximateNumberOfMessagesVisible threshold that triggers the backlog alarm."
  type        = number
  default     = 100
}

variable "sqs_max_receive_count" {
  description = "Number of receives before a message is moved to the DLQ."
  type        = number
  default     = 3
}

variable "enable_container_insights" {
  description = "Enable ECS Container Insights (extra CloudWatch metrics cost). Disabled by default."
  type        = bool
  default     = false
}
