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

| #  | Lab / Topic                   | Hands-on | Status |
|----|-------------------------------|----------|--------|
| 01 | Cloud Concepts                | N/A      | ✅ Completed |
| 02 | Global Infrastructure         | N/A      | ✅ Completed |
| 03 | IAM                           | Hands-on | ✅ Completed |
| 04 | VPC                           | —        | ⬜ Not started |
| 05 | EC2                           | —        | ⬜ Not started |
| 06 | S3                            | —        | ⬜ Not started |
| 07 | RDS                           | —        | ⬜ Not started |
| 08 | ECR                           | —        | ⬜ Not started |
| 09 | ECS                           | —        | ⬜ Not started |
| 10 | SQS                           | —        | ⬜ Not started |
| 11 | CloudWatch                    | —        | ⬜ Not started |
| 12 | Auto Scaling                  | —        | ⬜ Not started |
| 13 | Final Project                 | —        | ⬜ Not started |

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

├── 01-cloud-concepts/
├── 02-global-infrastructure/
├── 03-iam/
├── 04-vpc/
├── 05-ec2/
├── 06-s3/
├── 07-rds/
├── 08-ecr/
├── 09-ecs/
├── 10-sqs/
├── 11-cloudwatch/
├── 12-auto-scaling/
├── 13-final-project/
│
├── docs/
│   ├── architecture/
│   ├── cost-control/
│   ├── notes/
│   └── learning-log.md
│
└── README.md
```

## 📈 Progress

This repository will evolve throughout the study plan as new AWS concepts, services, Terraform configurations, and practical exercises are completed.

---

**Study goal:** AWS Certified Cloud Practitioner (CLF-C02)
**Focus:** AWS fundamentals + hands-on practice + Terraform
