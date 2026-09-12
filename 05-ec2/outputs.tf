output "instance_id" {
  description = "ID da EC2 criada pelo laboratório"
  value       = aws_instance.ec2.id
}

output "instance_private_ip" {
  description = "IP privado da EC2"
  value       = aws_instance.ec2.private_ip
}

output "instance_public_ip" {
  description = "IP público da EC2"
  value       = aws_instance.ec2.public_ip
}

output "instance_state" {
  description = "Estado atual da EC2"
  value       = aws_instance.ec2.instance_state
}

output "instance_type" {
  description = "Tipo da instância EC2"
  value       = aws_instance.ec2.instance_type
}

output "security_group_id" {
  description = "ID do Security Group da EC2"
  value       = aws_security_group.ec2.id
}