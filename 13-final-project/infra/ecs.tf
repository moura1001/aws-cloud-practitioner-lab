resource "aws_ecs_cluster" "main" {
  name = "${var.project_name}-final"

  setting {
    name  = "containerInsights"
    value = var.enable_container_insights ? "enabled" : "disabled"
  }

  tags = {
    Name = "${var.project_name}-final-ecs"
  }
}

# ---------------------------------------------------------------------------
# EC2 capacity: an Auto Scaling Group of ECS-optimized instances.
# Instances run in a public subnet with a public IP so they can reach ECR, SQS,
# CloudWatch and ECS without a NAT Gateway or VPC interface endpoints.
# ---------------------------------------------------------------------------
resource "aws_launch_template" "ecs" {
  name_prefix   = "${var.project_name}-final-"
  image_id      = local.ecs_ami_id
  instance_type = var.instance_type

  vpc_security_group_ids = [aws_security_group.ecs_instances.id]

  iam_instance_profile {
    name = aws_iam_instance_profile.ecs.name
  }

  metadata_options {
    http_endpoint = "enabled"
    http_tokens   = "required"
  }

  user_data = base64encode(join("\n", [
    "#!/bin/bash",
    "echo 'ECS_CLUSTER=${aws_ecs_cluster.main.name}' >> /etc/ecs/ecs.config",
    "echo 'ECS_ENABLE_TASK_IAM_ROLE=true' >> /etc/ecs/ecs.config",
    "echo 'ECS_ENABLE_CONTAINER_METADATA=true' >> /etc/ecs/ecs.config",
    ""
  ]))

  tag_specifications {
    resource_type = "instance"

    tags = {
      Name = "${var.project_name}-final-ecs"
    }
  }

  tags = {
    Name = "${var.project_name}-final-ecs"
  }
}

resource "aws_autoscaling_group" "ecs" {
  name = "${var.project_name}-final-ecs"

  min_size         = var.asg_min_size
  desired_capacity = var.asg_desired_capacity
  max_size         = var.asg_max_size

  # Single AZ for the capacity layer to keep cost at the minimum.
  vpc_zone_identifier = [data.aws_subnet.public_a.id]

  launch_template {
    id      = aws_launch_template.ecs.id
    version = "$Latest"
  }

  protect_from_scale_in = false

  tag {
    key                 = "Name"
    value               = "${var.project_name}-final-ecs"
    propagate_at_launch = true
  }

  lifecycle {
    create_before_destroy = true
  }
}

# ---------------------------------------------------------------------------
# Task definitions (host network mode: all containers share the instance
# network, so they reach each other through localhost and no port mappings or
# extra ENIs are required).
# ---------------------------------------------------------------------------
resource "aws_ecs_task_definition" "api" {
  family                   = "${var.project_name}-final-api"
  requires_compatibilities = ["EC2"]
  network_mode             = "host"
  execution_role_arn       = aws_iam_role.task_execution.arn
  task_role_arn            = aws_iam_role.api_task.arn

  container_definitions = jsonencode([
    {
      name      = "base-api"
      image     = "${aws_ecr_repository.api.repository_url}:${var.api_image_tag}"
      essential = true
      memory    = 512

      # host network mode requires a static port mapping (host == container).
      portMappings = [
        { containerPort = 8080, hostPort = 8080, protocol = "tcp" }
      ]

      environment = [
        { name = "SPRING_PROFILES_ACTIVE", value = "prod" },
        { name = "SERVER_PORT", value = "8080" },
        { name = "AWS_REGION", value = local.region },
        { name = "AWS_SQS_QUEUES_INPUT_QUEUE_URL", value = aws_sqs_queue.input.url },
        { name = "VALKEY_HOST", value = "localhost" },
        { name = "VALKEY_PORT", value = "6379" },
        { name = "MANAGEMENT_TRACING_ENABLED", value = "false" },
        { name = "MANAGEMENT_TRACING_EXPORT_ENABLED", value = "false" }
      ]

      secrets = [
        { name = "APP_API_KEY", valueFrom = aws_ssm_parameter.api_key.arn },
        { name = "VALKEY_URL", valueFrom = aws_ssm_parameter.valkey_url.arn }
      ]

      logConfiguration = {
        logDriver = "awslogs"
        options = {
          awslogs-group         = aws_cloudwatch_log_group.api.name
          awslogs-region        = local.region
          awslogs-stream-prefix = "api"
        }
      }

      healthCheck = {
        command     = ["CMD-SHELL", "wget -qO- http://localhost:8080/health || exit 1"]
        interval    = 30
        timeout     = 5
        retries     = 3
        startPeriod = 60
      }
    }
  ])

  tags = {
    Name = "${var.project_name}-final-api"
  }
}

resource "aws_ecs_task_definition" "consumer" {
  family                   = "${var.project_name}-final-consumer"
  requires_compatibilities = ["EC2"]
  network_mode             = "host"
  execution_role_arn       = aws_iam_role.task_execution.arn
  task_role_arn            = aws_iam_role.consumer_task.arn

  container_definitions = jsonencode([
    {
      name      = "base-consumer"
      image     = "${aws_ecr_repository.consumer.repository_url}:${var.consumer_image_tag}"
      essential = true
      memory    = 640

      environment = [
        { name = "SPRING_PROFILES_ACTIVE", value = "prod" },
        { name = "SERVER_PORT", value = "8081" },
        { name = "AWS_REGION", value = local.region },
        { name = "AWS_SQS_QUEUES_INPUT_QUEUE_URL", value = aws_sqs_queue.input.url },
        { name = "AWS_SQS_QUEUES_DLQ_QUEUE_URL", value = aws_sqs_queue.dlq.url },
        { name = "VALKEY_HOST", value = "localhost" },
        { name = "VALKEY_PORT", value = "6379" },
        { name = "EXT_SERVICE_URL_PROCESS", value = "http://localhost:8888/process" },
        { name = "EXT_SERVICE_URL_HEALTH", value = "http://localhost:8888/health" },
        { name = "MANAGEMENT_TRACING_ENABLED", value = "false" },
        { name = "MANAGEMENT_TRACING_EXPORT_ENABLED", value = "false" }
      ]

      secrets = [
        { name = "VALKEY_URL", valueFrom = aws_ssm_parameter.valkey_url.arn }
      ]

      logConfiguration = {
        logDriver = "awslogs"
        options = {
          awslogs-group         = aws_cloudwatch_log_group.consumer.name
          awslogs-region        = local.region
          awslogs-stream-prefix = "consumer"
        }
      }

      healthCheck = {
        command     = ["CMD-SHELL", "wget -qO- http://localhost:8081/health || exit 1"]
        interval    = 30
        timeout     = 5
        retries     = 3
        startPeriod = 60
      }
    }
  ])

  tags = {
    Name = "${var.project_name}-final-consumer"
  }
}

resource "aws_ecs_task_definition" "valkey" {
  family                   = "${var.project_name}-final-valkey"
  requires_compatibilities = ["EC2"]
  network_mode             = "host"
  execution_role_arn       = aws_iam_role.task_execution.arn

  container_definitions = jsonencode([
    {
      name      = "valkey"
      image     = var.valkey_image
      essential = true
      memory    = 256

      # Bind to loopback only and require a password: the state store is never
      # reachable from outside the instance.
      command = [
        "sh",
        "-c",
        "exec valkey-server --bind 127.0.0.1 --protected-mode yes --requirepass \"$VALKEY_PASSWORD\""
      ]

      secrets = [
        { name = "VALKEY_PASSWORD", valueFrom = aws_ssm_parameter.valkey_password.arn }
      ]

      logConfiguration = {
        logDriver = "awslogs"
        options = {
          awslogs-group         = aws_cloudwatch_log_group.valkey.name
          awslogs-region        = local.region
          awslogs-stream-prefix = "valkey"
        }
      }
    }
  ])

  tags = {
    Name = "${var.project_name}-final-valkey"
  }
}

resource "aws_ecs_task_definition" "external_service" {
  family                   = "${var.project_name}-final-external-service"
  requires_compatibilities = ["EC2"]
  network_mode             = "host"
  execution_role_arn       = aws_iam_role.task_execution.arn

  container_definitions = jsonencode([
    {
      name      = "external-service-mock"
      image     = var.external_service_image
      essential = true
      memory    = 128

      environment = [
        { name = "HTTP_PORT", value = "8888" },
        { name = "ECHO_BACK_TO_CLIENT", value = "true" },
        { name = "RESPONSE_CODE", value = "200" },
        { name = "RESPONSE_CONTENT_TYPE", value = "application/json" },
        { name = "RESPONSE_BODY", value = "{\"status\":\"OK\",\"message\":\"processed\"}" }
      ]

      logConfiguration = {
        logDriver = "awslogs"
        options = {
          awslogs-group         = aws_cloudwatch_log_group.external_service.name
          awslogs-region        = local.region
          awslogs-stream-prefix = "external-service"
        }
      }
    }
  ])

  tags = {
    Name = "${var.project_name}-final-external-service"
  }
}

# ---------------------------------------------------------------------------
# Services
# ---------------------------------------------------------------------------
resource "aws_ecs_service" "valkey" {
  name            = "valkey"
  cluster         = aws_ecs_cluster.main.id
  task_definition = aws_ecs_task_definition.valkey.arn
  desired_count   = 1
  launch_type     = "EC2"

  tags = {
    Name = "${var.project_name}-final-valkey"
  }
}

resource "aws_ecs_service" "external_service" {
  name            = "external-service"
  cluster         = aws_ecs_cluster.main.id
  task_definition = aws_ecs_task_definition.external_service.arn
  desired_count   = 1
  launch_type     = "EC2"

  tags = {
    Name = "${var.project_name}-final-external-service"
  }
}

resource "aws_ecs_service" "api" {
  name            = "base-api"
  cluster         = aws_ecs_cluster.main.id
  task_definition = aws_ecs_task_definition.api.arn
  desired_count   = 1
  launch_type     = "EC2"

  load_balancer {
    target_group_arn = aws_lb_target_group.api.arn
    container_name   = "base-api"
    container_port   = 8080
  }

  health_check_grace_period_seconds = 120

  # Single instance per host port: allow stop-before-start on deploy.
  deployment_minimum_healthy_percent = 0
  deployment_maximum_percent         = 100

  depends_on = [aws_lb_listener.http]

  tags = {
    Name = "${var.project_name}-final-api"
  }
}

resource "aws_ecs_service" "consumer" {
  name            = "base-consumer"
  cluster         = aws_ecs_cluster.main.id
  task_definition = aws_ecs_task_definition.consumer.arn
  desired_count   = 1
  launch_type     = "EC2"

  depends_on = [
    aws_ecs_service.valkey,
    aws_ecs_service.external_service
  ]

  tags = {
    Name = "${var.project_name}-final-consumer"
  }
}
