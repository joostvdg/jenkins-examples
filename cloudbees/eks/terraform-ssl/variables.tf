
variable "region" {
    default = "us-east-1"
}

variable "access_key" {}

variable "secret_key" {}

variable "aws_session_token" {}

variable "vpc_id" {}
variable "dns_name" {
    default = "*.eks.kearos.net"
}

variable "zone_name" {
    default = "eks.kearos.net"
}



