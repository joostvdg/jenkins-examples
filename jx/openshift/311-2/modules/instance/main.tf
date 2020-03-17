resource "google_compute_instance" "instance" {
    name         = "${var.prefix}-${var.instance_name}"
    machine_type = var.machine_type
    zone         = var.zone
    allow_stopping_for_update = true

    boot_disk {
        initialize_params {
            image = var.vm_image
            size = var.disk_size
            type = "pd-ssd"
        }
    }

    network_interface {
        network = var.network
        subnetwork = var.vpc_subnet_int
        access_config  {
        }
    }

    metadata = {
        ssh-keys = var.ssh_key
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