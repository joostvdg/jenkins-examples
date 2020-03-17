resource "google_compute_instance" "master" {
    name         = "master"
    machine_type = var.master_machine_type
    zone         = var.master_zone
    allow_stopping_for_update = true

    boot_disk {
        initialize_params {
            image = var.vm_image
            size = 100
        }
    }

    // Local SSD disk
    scratch_disk {
        interface = "SCSI"
    }

    network_interface {
        network = "default"
        # network_ip = google_compute_address.masterip.address
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