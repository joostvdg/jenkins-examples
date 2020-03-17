resource "google_compute_instance" "node1" {
    name         = "node1"
    machine_type = var.compute_machine_type
    zone         = var.master_zone
    allow_stopping_for_update = true

    boot_disk {
        initialize_params {
            image = var.vm_image
            size = 250
        }
    }

    // Local SSD disk
    scratch_disk {
        interface = "SCSI"
    }

    network_interface {
        network = "default"
        # network_ip = google_compute_address.node1ip.address
        access_config {
            # external address
        }
    }
    metadata = {
        ssh-keys = "joostvdg:${file("~/.ssh/id_rsa.pub")}"
    }

    service_account {
        scopes = [
            "https://www.googleapis.com/auth/compute",
            "https://www.googleapis.com/auth/devstorage.read_only",
            "https://www.googleapis.com/auth/logging.write",
            "https://www.googleapis.com/auth/monitoring",
            "https://www.googleapis.com/auth/ndev.clouddns.readwrite",
            "https://www.googleapis.com/auth/cloud-platform"
        ]
    }
}

resource "google_compute_instance" "node2" {
    name         = "node2"
    machine_type = "n1-standard-4"
    zone         = var.master_zone
    allow_stopping_for_update = true

    boot_disk {
        initialize_params {
            image = var.vm_image
            size = 250
        }
    }

    // Local SSD disk
    scratch_disk {
        interface = "SCSI"
    }

    network_interface {
        network = "default"
        # network_ip = google_compute_address.node2ip.address
        access_config {
            # external address
        }
    }
    metadata = {
        ssh-keys = "joostvdg:${file("~/.ssh/id_rsa.pub")}"
    }

    service_account {
        scopes = [
            "https://www.googleapis.com/auth/compute",
            "https://www.googleapis.com/auth/devstorage.read_only",
            "https://www.googleapis.com/auth/logging.write",
            "https://www.googleapis.com/auth/monitoring",
            "https://www.googleapis.com/auth/ndev.clouddns.readwrite",
            "https://www.googleapis.com/auth/cloud-platform"
        ]
    }
}

resource "google_compute_instance" "node3" {
    name         = "node3"
    machine_type = "n1-standard-4"
    zone         = var.master_zone
    allow_stopping_for_update = true

    boot_disk {
        initialize_params {
            image = var.vm_image
            size = 250
        }
    }

    // Local SSD disk
    scratch_disk {
        interface = "SCSI"
    }

    network_interface {
        network = "default"
        # network_ip = google_compute_address.node2ip.address
        access_config {
            # external address
        }
    }
    metadata = {
        ssh-keys = "joostvdg:${file("~/.ssh/id_rsa.pub")}"
    }

    service_account {
        scopes = [
            "https://www.googleapis.com/auth/compute",
            "https://www.googleapis.com/auth/devstorage.read_only",
            "https://www.googleapis.com/auth/logging.write",
            "https://www.googleapis.com/auth/monitoring",
            "https://www.googleapis.com/auth/ndev.clouddns.readwrite",
            "https://www.googleapis.com/auth/cloud-platform"
        ]
    }
}

resource "google_compute_instance" "node4" {
    name         = "node4"
    machine_type = "n1-standard-4"
    zone         = var.master_zone
    allow_stopping_for_update = true

    boot_disk {
        initialize_params {
            image = var.vm_image
            size = 250
        }
    }

    // Local SSD disk
    scratch_disk {
        interface = "SCSI"
    }

    network_interface {
        network = "default"
        # network_ip = google_compute_address.node2ip.address
        access_config {
            # external address
        }
    }
    metadata = {
        ssh-keys = "joostvdg:${file("~/.ssh/id_rsa.pub")}"
    }

    service_account {
        scopes = [
            "https://www.googleapis.com/auth/compute",
            "https://www.googleapis.com/auth/devstorage.read_only",
            "https://www.googleapis.com/auth/logging.write",
            "https://www.googleapis.com/auth/monitoring",
            "https://www.googleapis.com/auth/ndev.clouddns.readwrite",
            "https://www.googleapis.com/auth/cloud-platform"
        ]
    }
}

