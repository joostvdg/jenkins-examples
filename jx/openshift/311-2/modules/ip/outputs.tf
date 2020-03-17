output "master_ip" {
    value = google_compute_global_address.master.address
}

output "nodes_ip" {
    value = google_compute_address.nodes.address
}

