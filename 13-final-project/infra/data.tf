data "aws_caller_identity" "current" {}

data "aws_region" "current" {}

# Existing VPC and subnets created by the VPC lab (04-vpc).
# They are reused by reference only - this project never creates or destroys them.
data "aws_vpc" "lab" {
  filter {
    name   = "tag:Name"
    values = [var.vpc_name]
  }
}

data "aws_subnet" "public_a" {
  filter {
    name   = "tag:Name"
    values = [var.public_subnet_name_a]
  }
}

data "aws_subnet" "public_b" {
  filter {
    name   = "tag:Name"
    values = [var.public_subnet_name_b]
  }
}

# ECS-optimized Amazon Linux 2023 AMI, resolved through the public SSM parameter.
data "aws_ssm_parameter" "ecs_ami" {
  name = "/aws/service/ecs/optimized-ami/amazon-linux-2023/recommended"
}

locals {
  account_id = data.aws_caller_identity.current.account_id
  region     = data.aws_region.current.region
  ecs_ami_id = jsondecode(data.aws_ssm_parameter.ecs_ami.value)["image_id"]
}
