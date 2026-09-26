output "vpc_id" {
  description = "Existing VPC used by the Auto Scaling lab"
  value       = data.aws_vpc.lab.id
}

output "public_subnet_ids" {
  description = "Existing public subnets used by the Auto Scaling Group"
  value       = data.aws_subnets.public.ids
}

output "ami_id" {
  description = "Amazon Linux 2023 AMI used by the Launch Template"
  value       = data.aws_ami.amazon_linux.id
}

output "security_group_id" {
  description = "Security group created for the Auto Scaling instances"
  value       = aws_security_group.autoscaling.id
}

output "launch_template_id" {
  description = "Launch Template ID"
  value       = aws_launch_template.autoscaling.id
}

output "autoscaling_group_name" {
  description = "Auto Scaling Group name"
  value       = aws_autoscaling_group.autoscaling.name
}
