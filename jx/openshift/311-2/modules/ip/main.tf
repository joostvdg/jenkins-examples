resource "google_compute_global_address" "master" {
    name = "master"
}

resource "google_compute_address" "nodes" {
    name = "nodes"
}

resource "google_dns_record_set" "ocp" {
    name = "api.${google_dns_managed_zone.ocp_kearos_net.dns_name}"
    type = "A"
    ttl  = 300

    managed_zone = google_dns_managed_zone.ocp_kearos_net.name

    rrdatas = [google_compute_global_address.master.address]
}

resource "google_dns_record_set" "apps" {
    name = "*.apps.${google_dns_managed_zone.ocp_kearos_net.dns_name}"
    type = "A"
    ttl  = 300

    managed_zone = google_dns_managed_zone.ocp_kearos_net.name

    rrdatas = [google_compute_address.nodes.address]
}

resource "google_dns_managed_zone" "ocp_kearos_net" {
    name     = "ocp-kearos-net"
    dns_name = "ocp.kearos.net."
}
