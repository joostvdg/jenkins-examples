output "health_check_master" {
    value =  "${google_compute_health_check.master.self_link}"
}

output "health_check_nodes" {
    value =  "${google_compute_http_health_check.nodes.self_link}"
}
