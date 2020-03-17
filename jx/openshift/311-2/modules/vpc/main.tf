resource "google_compute_network" "vpc_network" {
    name                                 =  var.vpc_name
    auto_create_subnetworks = "false"
    routing_mode                   = "GLOBAL"
}

resource "google_compute_subnetwork" "vpc_subnet_int" {
    name                         =  var.network_name_int
    ip_cidr_range            = var.network_name_int_range
    network                     = google_compute_network.vpc_network.self_link
    region                        = var.region
    private_ip_google_access = true
} 

resource "google_compute_subnetwork" "vpc_subnet_ext" {
    name                         =  var.network_name_ext
    ip_cidr_range            = var.network_name_ext_range
    network                     = google_compute_network.vpc_network.self_link
    region                        = var.region
    private_ip_google_access = true
} 

resource "google_compute_firewall" "fw_access" {
    name    = "joostvdg-rhos311-fw-ext"
    network = google_compute_network.vpc_network.name

    allow {
        protocol = "icmp"
    }

    allow {
        protocol = "tcp"
        ports    = ["22", "80", "443"]
    }

    source_ranges = ["0.0.0.0/0"]
}

resource "google_compute_firewall" "fw_gcp_health_checks" {
    name    = "joostvdg-rhos311-fw-gcp-health-checks"
    network = google_compute_network.vpc_network.name

    allow {
        protocol = "tcp"
        ports    = ["0-65535"]
    }

    source_ranges = ["35.191.0.0/16","130.211.0.0/22"]
}


resource "google_compute_firewall" "allow-internal" {
    name    = "joostvdg-rhos311-fw-int"
    network = google_compute_network.vpc_network.name

    allow {
        protocol = "icmp"
    }
    
    allow {
        protocol = "tcp"
        ports    = ["0-65535"]
    }
    
    allow {
        protocol = "udp"
        ports    = ["0-65535"]
    }
    source_ranges = [
        var.network_name_int_range
    ]
}