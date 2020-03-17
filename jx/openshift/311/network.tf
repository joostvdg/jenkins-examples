# resource "google_compute_network" "default" {
#     name = "jx-rhos311-network"
# }

# resource "google_compute_subnetwork" "default" {
#     name                    = "subnet01"
#     ip_cidr_range      = "10.0.0.0/28"
#     region                  = var.location
#     network               = google_compute_network.default.self_link
# }

# resource "google_compute_address" "masterip" {
#     name         = "master-ip"
#     subnetwork   = google_compute_subnetwork.default.self_link
#     address_type = "INTERNAL"
#     address      = "10.0.0.11"
#     region       = var.location
# }

# resource "google_compute_address" "node1ip" {
#     name         = "node1-ip"
#     subnetwork   = google_compute_subnetwork.default.self_link
#     address_type = "INTERNAL"
#     address      = "10.0.0.12"
#     region       = var.location
# }

# resource "google_compute_address" "node2ip" {
#     name         = "node2-ip"
#     subnetwork   = google_compute_subnetwork.default.self_link
#     address_type = "INTERNAL"
#     address      = "10.0.0.13"
#     region       = var.location
# }