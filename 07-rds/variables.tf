variable "db_password" {
  description = "Master password for the RDS lab database"
  type        = string
  sensitive   = true
}
