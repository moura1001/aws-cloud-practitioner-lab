output "s3_policy_arn" {
  description = "ARN da policy utilizada pelo laboratório S3"
  value       = aws_iam_policy.s3.arn
}

output "s3_bucket_name" {
  description = "Nome do bucket do laboratório"
  value       = aws_s3_bucket.lab.bucket
}

output "s3_bucket_arn" {
  description = "ARN do bucket do laboratório"
  value       = aws_s3_bucket.lab.arn
}
