# AWS Cloud Practitioner Lab

Hands-on AWS and Terraform laboratory created as part of my preparation for the **AWS Certified Cloud Practitioner (CLF-C02)** certification.

The goal of this project is to combine theoretical study with practical experience by building, testing, documenting, and destroying AWS resources using **Terraform** and the **AWS CLI**.

## 🎯 Objectives

* Prepare for the AWS Certified Cloud Practitioner (CLF-C02) certification
* Strengthen understanding of core AWS concepts and services
* Practice AWS IAM and security principles
* Learn and reinforce Infrastructure as Code with Terraform
* Gain hands-on experience with AWS services
* Understand AWS pricing, billing, and cost management
* Document the learning process and practical labs

## 🏗️ Labs

| #  | Lab / Topic                   | Hands-on  | Status |
|----|-------------------------------|-----------|--------|
| 01 | Cloud Concepts                | N/A       | ✅ Completed |
| 02 | IAM                           | —         | ⬜ Not started |
| 03 | VPC                           | —         | ⬜ Not started |
| 04 | EC2                           | —         | ⬜ Not started |
| 05 | S3                            | —         | ⬜ Not started |
| 06 | RDS                           | —         | ⬜ Not started |
| 07 | ECR                           | —         | ⬜ Not started |
| 08 | ECS                           | —         | ⬜ Not started |
| 09 | SQS                           | —         | ⬜ Not started |
| 10 | CloudWatch                    | —         | ⬜ Not started |
| 11 | Auto Scaling                  | —         | ⬜ Not started |
| 12 | Final Project                 | —         | ⬜ Not started |

## 📚 Study Topics

The project covers the main areas of the CLF-C02 exam:

* Cloud Concepts
* AWS Global Infrastructure
* AWS Well-Architected Framework
* Shared Responsibility Model
* IAM and Security
* Compute
* Storage
* Databases
* Networking
* Containers
* Messaging
* Monitoring and Observability
* Scalability and High Availability
* Billing and Pricing
* AWS Support

## 🛠️ Technologies

* **AWS**
* **Terraform**
* **AWS CLI**
* **Docker**
* **Git / GitHub**

## 💰 Cost Management

This repository is developed using a personal AWS account.

Cost control is therefore a fundamental part of the project.

Labs are designed to follow a **create → test → document → destroy** workflow whenever possible.

Resources that may incur charges will be explicitly identified before deployment, and infrastructure will be destroyed after completing the corresponding exercises.

> **Important:** This project is intended for learning purposes. AWS resources may incur charges depending on the services, configurations, region, and current AWS pricing.

## 🔐 Security

No AWS credentials, access keys, passwords, tokens, or other secrets should ever be committed to this repository.

Terraform state files and other sensitive or local files will be excluded through `.gitignore`.

## 📁 Project Structure

```text
aws-cloud-practitioner-lab/
│
├── 01-cloud-concepts/
├── 02-iam/
├── 03-vpc/
├── 04-ec2/
├── 05-s3/
├── 06-rds/
├── 07-ecr/
├── 08-ecs/
├── 09-sqs/
├── 10-cloudwatch/
├── 11-auto-scaling/
├── 12-final-project/
│
├── docs/
│   ├── architecture/
│   ├── cost-control/
│   └── notes/
│
└── README.md
```

## 📈 Progress

This repository will evolve throughout the study plan as new AWS concepts, services, Terraform configurations, and practical exercises are completed.

---

**Study goal:** AWS Certified Cloud Practitioner (CLF-C02)
**Focus:** AWS fundamentals + hands-on practice + Terraform
