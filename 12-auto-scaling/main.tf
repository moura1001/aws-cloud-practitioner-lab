terraform {
  required_version = ">= 1.5.0"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 6.0"
    }
  }
}

provider "aws" {
  region = "sa-east-1"
}

data "aws_vpc" "lab" {
  filter {
    name   = "tag:Name"
    values = [var.vpc_name]
  }
}

data "aws_subnets" "public" {
  filter {
    name   = "vpc-id"
    values = [data.aws_vpc.lab.id]
  }

  filter {
    name   = "tag:Name"
    values = ["${var.public_subnet_name_prefix}*"]
  }
}

data "aws_ami" "amazon_linux" {
  most_recent = true
  owners      = ["amazon"]

  filter {
    name   = "name"
    values = ["al2023-ami-*-kernel-6.1-x86_64"]
  }

  filter {
    name   = "architecture"
    values = ["x86_64"]
  }

  filter {
    name   = "virtualization-type"
    values = ["hvm"]
  }

  filter {
    name   = "root-device-type"
    values = ["ebs"]
  }

  filter {
    name   = "state"
    values = ["available"]
  }
}

resource "aws_security_group" "autoscaling" {
  name        = "aws-cloud-practitioner-lab-autoscaling"
  description = "Security group for the AWS Cloud Practitioner Auto Scaling lab"
  vpc_id      = data.aws_vpc.lab.id

  tags = {
    Name = "aws-cloud-practitioner-lab-autoscaling"
  }
}

resource "aws_launch_template" "autoscaling" {
  name        = "aws-cloud-practitioner-lab-autoscaling"
  description = "Launch template for the AWS Cloud Practitioner Auto Scaling lab"

  image_id      = data.aws_ami.amazon_linux.id
  instance_type = var.instance_type

  vpc_security_group_ids = [
    aws_security_group.autoscaling.id
  ]

  tag_specifications {
    resource_type = "instance"

    tags = {
      Name = "aws-cloud-practitioner-lab-asg-instance"
    }
  }

  tags = {
    Name = "aws-cloud-practitioner-lab-autoscaling"
  }
}

resource "aws_autoscaling_group" "autoscaling" {
  name = "aws-cloud-practitioner-lab"

  min_size         = var.min_size
  desired_capacity = var.desired_capacity
  max_size         = var.max_size

  vpc_zone_identifier = data.aws_subnets.public.ids

  launch_template {
    id      = aws_launch_template.autoscaling.id
    version = "$Latest"
  }

  health_check_type         = "EC2"
  health_check_grace_period = 60

  tag {
    key                 = "Name"
    value               = "aws-cloud-practitioner-lab-asg-instance"
    propagate_at_launch = true
  }

  lifecycle {
    create_before_destroy = true
  }
}

resource "aws_iam_policy" "autoscaling" {
  name        = "aws-cloud-practitioner-lab-autoscaling"
  description = "Least-privilege permissions for the AWS Cloud Practitioner Auto Scaling lab"

  policy = jsonencode({
    Version = "2012-10-17"

    Statement = [
      {
        Sid    = "EC2Read"
        Effect = "Allow"

        Action = [
          "ec2:DescribeImages",
          "ec2:DescribeSecurityGroups",
          "ec2:DescribeSubnets",
          "ec2:DescribeVpcs",
          "ec2:DescribeLaunchTemplates",
          "ec2:DescribeLaunchTemplateVersions"
        ]

        Resource = "*"
      },

      {
        Sid    = "LaunchTemplateManagement"
        Effect = "Allow"

        Action = [
          "ec2:CreateLaunchTemplate",
          "ec2:DeleteLaunchTemplate"
        ]

        Resource = "*"
      },

      {
        Sid    = "SecurityGroupManagement"
        Effect = "Allow"

        Action = [
          "ec2:CreateSecurityGroup",
          "ec2:DeleteSecurityGroup"
        ]

        Resource = "*"
      },

      {
        Sid    = "AutoScalingManagement"
        Effect = "Allow"

        Action = [
          "autoscaling:CreateAutoScalingGroup",
          "autoscaling:UpdateAutoScalingGroup",
          "autoscaling:DeleteAutoScalingGroup",
          "autoscaling:PutScalingPolicy",
          "autoscaling:DeletePolicy",
          "autoscaling:SetDesiredCapacity"
        ]

        Resource = "*"
      },

      {
        Sid    = "AutoScalingRead"
        Effect = "Allow"

        Action = [
          "autoscaling:DescribeAutoScalingGroups",
          "autoscaling:DescribeAutoScalingInstances",
          "autoscaling:DescribePolicies",
          "autoscaling:DescribeScalingActivities"
        ]

        Resource = "*"
      },

      {
        Sid    = "CreateAutoScalingServiceLinkedRole"
        Effect = "Allow"

        Action = "iam:CreateServiceLinkedRole"

        Resource = "arn:aws:iam::*:role/aws-service-role/autoscaling.amazonaws.com/AWSServiceRoleForAutoScaling"

        Condition = {
          StringLike = {
            "iam:AWSServiceName" = "autoscaling.amazonaws.com"
          }
        }
      }
    ]
  })
}

resource "aws_iam_user_policy_attachment" "autoscaling" {
  user       = "aws-cloud-practitioner-lab"
  policy_arn = aws_iam_policy.autoscaling.arn
}
