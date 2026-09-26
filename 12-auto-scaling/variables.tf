variable "aws_region" {
  description = "AWS region used by the lab"
  type        = string
  default     = "sa-east-1"
}

variable "vpc_name" {
  description = "Name tag of the existing VPC"
  type        = string
  default     = "aws-cloud-practitioner-lab-vpc"
}

variable "public_subnet_name_prefix" {
  description = "Name tag prefix used by the existing public subnets"
  type        = string
  default     = "aws-cloud-practitioner-lab-public-"
}

variable "instance_type" {
  description = "EC2 instance type used by the Auto Scaling Group"
  type        = string
  default     = "t3.nano"
}

variable "min_size" {
  description = "Minimum number of instances"
  type        = number
  default     = 1
}

variable "desired_capacity" {
  description = "Initial desired number of instances"
  type        = number
  default     = 1
}

variable "max_size" {
  description = "Maximum number of instances"
  type        = number
  default     = 2
}
