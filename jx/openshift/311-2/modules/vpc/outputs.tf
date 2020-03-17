output "vpc_link" {
    value = "${google_compute_network.vpc_network.self_link}"
}

output "vpc_subnet_int" {
    value =   "${google_compute_subnetwork.vpc_subnet_int.self_link}"
}

output "vpc_subnet_ext" {
    value =   "${google_compute_subnetwork.vpc_subnet_ext.self_link}"
}