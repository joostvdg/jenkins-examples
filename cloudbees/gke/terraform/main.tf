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

