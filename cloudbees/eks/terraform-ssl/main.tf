
provider "aws" {
    access_key = var.access_key
    secret_key = var.secret_key
    token = var.aws_session_token
    region = var.region
    version = "~> 2.54.0"
}

# data "aws_vpc" "eks" {
#     id = var.vpc_id
# }

# data "aws_subnet_ids" "eks" {
#     vpc_id = var.vpc_id
#     filter {
#         name   = "tag:kubernetes.io/role/elb"
#         values = ["1"] # insert values here
#     }
# }

# data "aws_subnet" "eks" {
#     for_each = data.aws_subnet_ids.eks.ids
#     id       = each.value
# }

# resource "aws_lb" "eks" {
#     name               = "eks-lb-tf"
#     internal           = false
#     load_balancer_type = "network"
#     subnets            = data.aws_subnet_ids.eks.ids

#     enable_cross_zone_load_balancing = true
#     enable_deletion_protection = true

#     tags = {
#         Owner = "jvandergriendt"
#     }
# }

resource "aws_route53_zone" "eks" {
    name    = var.zone_name

    tags = {
        Owner = "jvandergriendt"
    }
}

# resource "aws_route53_record" "zone" {
#     zone_id = "${aws_route53_zone.eks.zone_id}"
#     name    = var.zone_name
#     type    = "NS"
#     ttl     = "30"

#     records = [
#         "${aws_route53_zone.eks.name_servers.0}",
#         "${aws_route53_zone.eks.name_servers.1}",
#         "${aws_route53_zone.eks.name_servers.2}",
#         "${aws_route53_zone.eks.name_servers.3}",
#     ]
# }

# resource "aws_route53_record" "main" {
#     zone_id = "${aws_route53_zone.eks.zone_id}"
#     name    = var.dns_name
#     type    = "A"
#     ttl     = "300"
#     records = [var.lb_ip]
# }



resource "aws_acm_certificate" "eks_nlb_cert" {
    domain_name       = var.dns_name
    validation_method = "DNS"

    tags = {
        Owner = "jvandergriendt"
    }

}
