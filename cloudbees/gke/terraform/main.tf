terraform {
  required_version = "~> 0.12"
}

# https://www.terraform.io/docs/providers/google/index.html
provider "google" {
  version   = "~> 2.18.1"
  project   = "${var.project}"
  region    = "europe-west4"
  zone      = "europe-west4-b"
}

resource "google_compute_disk" "default" {
  name  = "gce-nfs-disk"
  type  = "pd-ssd"
  zone  = "europe-west4-b"
  labels = {
    environment = "dev"
  }
  physical_block_size_bytes = 4096
}
