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
| 04 | VPC                           | Hands-on | ✅ Completed |
| 05 | EC2                           | Hands-on | ✅ Completed |
| 06 | S3                            | Hands-on | ✅ Completed |
| 07 | RDS                           | Hands-on | ✅ Completed |
| 08 | ECR                           | Hands-on | ✅ Completed |
| 09 | ECS                           | Hands-on | ✅ Completed |
| 10 | SQS                           | Hands-on | ✅ Completed |
| 11 | CloudWatch                    | Hands-on | ✅ Completed |
| 12 | Auto Scaling                  | Hands-on | ✅ Completed |
| 13 | Final Project                 | Hands-on | ✅ Completed |

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
│   ├── README.md          # diagnóstico + arquitetura final
│   ├── final_project/     # API + consumer (Spring Boot)
│   └── infra/             # Terraform (ECS on EC2, ALB, SQS, Valkey, ECR)
│
├── docs/
│   ├── learning-log.md
│   └── study-context.md
│
└── README.md
```

## 📈 Progress

All 13 modules are complete. The final project consolidates the whole study plan
into an event-driven application (API + consumer) deployed on AWS with **ECS on
EC2**, a public **ALB**, **SQS + DLQ**, a private **Valkey** task, **ECR** and
**CloudWatch** — validated end-to-end and then destroyed to keep costs at zero.

### 🎯 Next step

Practice exams (simulados) to prepare for the CLF-C02 exam:

- [ ] Timed full practice exams (65 questions)
- [ ] Review the weakest domains
- [ ] Revisit the "pontos de atenção" from each module
- [ ] Schedule the exam once scores are consistently high

---

**Study goal:** AWS Certified Cloud Practitioner (CLF-C02)
**Focus:** AWS fundamentals + hands-on practice + Terraform
