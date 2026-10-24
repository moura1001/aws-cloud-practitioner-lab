# Public Application Load Balancer security group.
# Only HTTP from the internet is allowed inbound.
resource "aws_security_group" "alb" {
  name        = "${var.project_name}-final-alb"
  description = "Public ALB for the final project - HTTP ingress only"
  vpc_id      = data.aws_vpc.lab.id

  ingress {
    description = "HTTP from the internet"
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    description = "Outbound to targets"
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "${var.project_name}-final-alb"
  }
}

# Security group attached to the ECS EC2 capacity instances.
# Containers share the host network, so this group is the network boundary:
#   - 8080 only from the ALB (base-api)
#   - 6379/8081/8888 only from the instance itself (Valkey, consumer, mock)
# Valkey additionally binds to 127.0.0.1 and requires authentication.
resource "aws_security_group" "ecs_instances" {
  name        = "${var.project_name}-final-ecs-instances"
  description = "ECS EC2 capacity instances - API exposed to ALB only, internal ports restricted to self"
  vpc_id      = data.aws_vpc.lab.id

  ingress {
    description     = "base-api from the ALB"
    from_port       = 8080
    to_port         = 8080
    protocol        = "tcp"
    security_groups = [aws_security_group.alb.id]
  }

  ingress {
    description = "Valkey reachable only from the instance"
    from_port   = 6379
    to_port     = 6379
    protocol    = "tcp"
    self        = true
  }

  ingress {
    description = "base-consumer reachable only from the instance"
    from_port   = 8081
    to_port     = 8081
    protocol    = "tcp"
    self        = true
  }

  ingress {
    description = "external service mock reachable only from the instance"
    from_port   = 8888
    to_port     = 8888
    protocol    = "tcp"
    self        = true
  }

  egress {
    description = "Outbound internet (ECR, SQS, CloudWatch, ECS) via the Internet Gateway"
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "${var.project_name}-final-ecs-instances"
  }
}
