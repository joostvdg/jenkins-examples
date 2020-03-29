data "aws_lb" "main" {
    arn  = var.lb_arn
}

resource "aws_route53_record" "main" {
    zone_id = "${aws_route53_zone.eks.zone_id}"
    name    = var.dns_name
    type    = "A"

    alias {
        name                   = "${data.aws_lb.main.dns_name}"
        zone_id                = "${aws_route53_zone.eks.zone_id}"
        evaluate_target_health = false
    }
}