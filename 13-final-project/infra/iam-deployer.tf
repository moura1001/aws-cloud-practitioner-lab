# ---------------------------------------------------------------------------
# Least-privilege policy for the identity that runs Terraform
# (user `aws-cloud-practitioner-lab`).
#
# It replaces the need for AdministratorAccess for this project. Actions are
# scoped to the resources this project creates (`...-final-*`) wherever AWS
# supports resource-level permissions; create/describe actions that AWS only
# allows on `*` stay on `*`.
#
# IAM limits a managed policy to 6144 characters and the user can have at most
# 10 managed policies, so this single policy keeps only the strict minimum
# needed to create, read and destroy this project.
#
# Bootstrap: this policy and its attachment must be applied once while
# AdministratorAccess is still attached. After that, AdministratorAccess can be
# detached and this policy is enough for future plans/applies.
# ---------------------------------------------------------------------------
locals {
  deployer_user_name = "aws-cloud-practitioner-lab"

  project_role_prefix    = "arn:aws:iam::${local.account_id}:role/${var.project_name}-final-*"
  project_profile_prefix = "arn:aws:iam::${local.account_id}:instance-profile/${var.project_name}-final-*"
}

resource "aws_iam_policy" "deployer" {
  name        = "${var.project_name}-final"
  description = "Least-privilege permissions to deploy the final project"

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Sid      = "CallerIdentity"
        Effect   = "Allow"
        Action   = ["sts:GetCallerIdentity"]
        Resource = "*"
      },
      {
        Sid    = "Ec2Read"
        Effect = "Allow"
        Action = [
          "ec2:DescribeAvailabilityZones",
          "ec2:DescribeAccountAttributes",
          "ec2:DescribeVpcs",
          "ec2:DescribeSubnets",
          "ec2:DescribeSecurityGroups",
          "ec2:DescribeSecurityGroupRules",
          "ec2:DescribeLaunchTemplates",
          "ec2:DescribeLaunchTemplateVersions"
        ]
        Resource = "*"
      },
      {
        Sid    = "Ec2SecurityGroupWrite"
        Effect = "Allow"
        Action = [
          "ec2:CreateSecurityGroup",
          "ec2:DeleteSecurityGroup",
          "ec2:CreateTags",
          "ec2:DeleteTags"
        ]
        Resource = "*"
      },
      {
        Sid    = "Ec2SecurityGroupRules"
        Effect = "Allow"
        Action = [
          "ec2:AuthorizeSecurityGroupIngress",
          "ec2:AuthorizeSecurityGroupEgress",
          "ec2:RevokeSecurityGroupIngress",
          "ec2:RevokeSecurityGroupEgress"
        ]
        Resource = "arn:aws:ec2:${local.region}:${local.account_id}:security-group/*"
      },
      {
        Sid    = "Ec2LaunchTemplates"
        Effect = "Allow"
        Action = [
          "ec2:CreateLaunchTemplate",
          "ec2:DeleteLaunchTemplate",
          "ec2:ModifyLaunchTemplate"
        ]
        Resource = "arn:aws:ec2:${local.region}:${local.account_id}:launch-template/*"
      },
      {
        Sid    = "LoadBalancing"
        Effect = "Allow"
        Action = [
          "elasticloadbalancing:CreateLoadBalancer",
          "elasticloadbalancing:DeleteLoadBalancer",
          "elasticloadbalancing:DescribeLoadBalancers",
          "elasticloadbalancing:CreateTargetGroup",
          "elasticloadbalancing:DeleteTargetGroup",
          "elasticloadbalancing:DescribeTargetGroups",
          "elasticloadbalancing:CreateListener",
          "elasticloadbalancing:DeleteListener",
          "elasticloadbalancing:DescribeListeners",
          "elasticloadbalancing:DescribeTags",
          "elasticloadbalancing:AddTags",
          "elasticloadbalancing:RemoveTags"
        ]
        Resource = "*"
      },
      {
        Sid    = "AutoScaling"
        Effect = "Allow"
        Action = [
          "autoscaling:CreateAutoScalingGroup",
          "autoscaling:UpdateAutoScalingGroup",
          "autoscaling:DeleteAutoScalingGroup",
          "autoscaling:DescribeAutoScalingGroups",
          "autoscaling:DescribeTags",
          "autoscaling:CreateOrUpdateTags",
          "autoscaling:DeleteTags"
        ]
        Resource = "*"
      },
      {
        Sid    = "Ecs"
        Effect = "Allow"
        Action = [
          "ecs:CreateCluster",
          "ecs:DescribeClusters",
          "ecs:DeleteCluster",
          "ecs:RegisterTaskDefinition",
          "ecs:DescribeTaskDefinition",
          "ecs:DeregisterTaskDefinition",
          "ecs:CreateService",
          "ecs:DescribeServices",
          "ecs:UpdateService",
          "ecs:DeleteService",
          "ecs:TagResource",
          "ecs:ListTagsForResource"
        ]
        Resource = "*"
      },
      {
        Sid    = "Logs"
        Effect = "Allow"
        Action = [
          "logs:CreateLogGroup",
          "logs:DeleteLogGroup",
          "logs:DescribeLogGroups",
          "logs:PutRetentionPolicy",
          "logs:TagResource",
          "logs:ListTagsForResource"
        ]
        Resource = "*"
      },
      {
        Sid    = "Alarms"
        Effect = "Allow"
        Action = [
          "cloudwatch:PutMetricAlarm",
          "cloudwatch:DescribeAlarms",
          "cloudwatch:DeleteAlarms",
          "cloudwatch:TagResource",
          "cloudwatch:ListTagsForResource"
        ]
        Resource = "*"
      },
      {
        Sid    = "EcrRepositories"
        Effect = "Allow"
        Action = [
          "ecr:CreateRepository",
          "ecr:DeleteRepository",
          "ecr:DescribeRepositories",
          "ecr:DescribeImages",
          "ecr:ListTagsForResource",
          "ecr:PutImageTagMutability",
          "ecr:PutImageScanningConfiguration",
          "ecr:PutLifecyclePolicy",
          "ecr:GetLifecyclePolicy",
          "ecr:DeleteLifecyclePolicy"
        ]
        Resource = "arn:aws:ecr:${local.region}:${local.account_id}:repository/${var.project_name}-final-*"
      },
      {
        Sid      = "EcrAuthorization"
        Effect   = "Allow"
        Action   = ["ecr:GetAuthorizationToken"]
        Resource = "*"
      },
      {
        Sid    = "Sqs"
        Effect = "Allow"
        Action = [
          "sqs:CreateQueue",
          "sqs:DeleteQueue",
          "sqs:GetQueueAttributes",
          "sqs:SetQueueAttributes",
          "sqs:TagQueue",
          "sqs:ListQueueTags"
        ]
        Resource = "arn:aws:sqs:${local.region}:${local.account_id}:${var.project_name}-final-*"
      },
      {
        Sid    = "SsmProjectParameters"
        Effect = "Allow"
        Action = [
          "ssm:PutParameter",
          "ssm:GetParameter",
          "ssm:GetParameters",
          "ssm:DeleteParameter",
          "ssm:AddTagsToResource",
          "ssm:ListTagsForResource"
        ]
        Resource = "arn:aws:ssm:${local.region}:${local.account_id}:parameter/${local.secrets_prefix}/*"
      },
      {
        Sid    = "SsmPublicAwsParameters"
        Effect = "Allow"
        Action = [
          "ssm:GetParameter",
          "ssm:GetParameters"
        ]
        Resource = [
          "arn:aws:ssm:${local.region}:*:parameter/aws/service/*",
          "arn:aws:ssm:${local.region}::parameter/aws/service/*"
        ]
      },
      {
        Sid    = "KmsForSsm"
        Effect = "Allow"
        Action = [
          "kms:Encrypt",
          "kms:Decrypt",
          "kms:GenerateDataKey"
        ]
        Resource = "*"
        Condition = {
          StringEquals = {
            "kms:ViaService" = "ssm.${local.region}.amazonaws.com"
          }
        }
      },
      {
        Sid    = "IamProjectRoles"
        Effect = "Allow"
        Action = [
          "iam:CreateRole",
          "iam:DeleteRole",
          "iam:GetRole",
          "iam:TagRole",
          "iam:ListRolePolicies",
          "iam:GetRolePolicy",
          "iam:PutRolePolicy",
          "iam:DeleteRolePolicy",
          "iam:AttachRolePolicy",
          "iam:DetachRolePolicy",
          "iam:ListAttachedRolePolicies",
          "iam:ListInstanceProfilesForRole"
        ]
        Resource = local.project_role_prefix
      },
      {
        Sid    = "IamProjectInstanceProfiles"
        Effect = "Allow"
        Action = [
          "iam:CreateInstanceProfile",
          "iam:DeleteInstanceProfile",
          "iam:GetInstanceProfile",
          "iam:AddRoleToInstanceProfile",
          "iam:RemoveRoleFromInstanceProfile"
        ]
        Resource = local.project_profile_prefix
      },
      {
        Sid      = "IamPassProjectRoles"
        Effect   = "Allow"
        Action   = ["iam:PassRole"]
        Resource = local.project_role_prefix
        Condition = {
          StringEquals = {
            "iam:PassedToService" = [
              "ecs-tasks.amazonaws.com",
              "ec2.amazonaws.com"
            ]
          }
        }
      },
      {
        Sid      = "IamServiceLinkedRoles"
        Effect   = "Allow"
        Action   = ["iam:CreateServiceLinkedRole"]
        Resource = "*"
        Condition = {
          StringEquals = {
            "iam:AWSServiceName" = [
              "ecs.amazonaws.com",
              "autoscaling.amazonaws.com",
              "elasticloadbalancing.amazonaws.com"
            ]
          }
        }
      },
      {
        Sid    = "IamProjectPolicies"
        Effect = "Allow"
        Action = [
          "iam:CreatePolicy",
          "iam:DeletePolicy",
          "iam:GetPolicy",
          "iam:GetPolicyVersion",
          "iam:ListPolicyVersions",
          "iam:CreatePolicyVersion",
          "iam:DeletePolicyVersion",
          "iam:SetDefaultPolicyVersion"
        ]
        Resource = "arn:aws:iam::${local.account_id}:policy/${var.project_name}-final*"
      },
      {
        Sid    = "IamSelfAttachment"
        Effect = "Allow"
        Action = [
          "iam:GetUser",
          "iam:ListAttachedUserPolicies",
          "iam:AttachUserPolicy",
          "iam:DetachUserPolicy"
        ]
        Resource = "arn:aws:iam::${local.account_id}:user/${local.deployer_user_name}"
      }
    ]
  })

  tags = {
    Name = "${var.project_name}-final"
  }
}

resource "aws_iam_user_policy_attachment" "deployer" {
  user       = local.deployer_user_name
  policy_arn = aws_iam_policy.deployer.arn
}
