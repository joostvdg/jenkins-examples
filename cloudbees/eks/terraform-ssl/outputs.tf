# output "nlb_arn" {
#     value = aws_lb.eks.arn
# }

output "cert_arn" {
    value = aws_acm_certificate.eks_nlb_cert.arn
}

output "name_servers" {
    value = [for s in aws_route53_zone.eks.name_servers : s]
}
