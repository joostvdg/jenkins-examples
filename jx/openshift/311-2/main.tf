terraform {
  required_version = "~> 0.12"
}

# https://www.terraform.io/docs/providers/google/index.html
provider "google" {
  version   = "~> 2.18.1"
  project   = var.project
  region    =  var.region
  zone      = "europe-west4-b"
}

# resource "google_compute_disk" "default" {
#   name  = "gce-nfs-disk"
#   type  = "pd-ssd"
#   zone  = "europe-west4-b"
#   labels = {
#     environment = "dev"
#   }
#   physical_block_size_bytes = 4096
# }

module "vpc" {
  source = "./modules/vpc"
  region = var.region
  vpc_name = "vpc-joostvdg-rhos311"
  network_name_int = "network-joostvdg-rhos311-int"
  network_name_ext = "network-joostvdg-rhos311-ext"
}

module "master1" {
  source = "./modules/instance"
  machine_type = "n1-standard-8"
  instance_name = "master1"
  prefix = var.instance_prefix
  network = module.vpc.vpc_link
  vpc_subnet_int = module.vpc.vpc_subnet_int
  vpc_subnet_ext = module.vpc.vpc_subnet_ext
  zone = var.zone
  ssh_key = "joostvdg:${file("~/.ssh/id_rsa.pub")}"
}

module "infra1" {
  source = "./modules/instance"
  machine_type = "n1-standard-8"
  instance_name = "infra1"
  prefix = var.instance_prefix
  network = module.vpc.vpc_link
  vpc_subnet_int = module.vpc.vpc_subnet_int
  vpc_subnet_ext = module.vpc.vpc_subnet_ext
  zone = var.zone
  ssh_key = "joostvdg:${file("~/.ssh/id_rsa.pub")}"
}

module "node1" {
  source = "./modules/instance"
  instance_name = "node1"
  prefix = var.instance_prefix
  network = module.vpc.vpc_link
  vpc_subnet_int = module.vpc.vpc_subnet_int
  vpc_subnet_ext = module.vpc.vpc_subnet_ext
  zone = var.zone
  ssh_key = "joostvdg:${file("~/.ssh/id_rsa.pub")}"
}

module "node2" {
  source = "./modules/instance"
  instance_name = "node2"
  prefix = var.instance_prefix
  network = module.vpc.vpc_link
  vpc_subnet_int = module.vpc.vpc_subnet_int
  vpc_subnet_ext = module.vpc.vpc_subnet_ext
  zone = var.zone
  ssh_key = "joostvdg:${file("~/.ssh/id_rsa.pub")}"
}

module "node3" {
  source = "./modules/instance"
  instance_name = "node3"
  prefix = var.instance_prefix
  network = module.vpc.vpc_link
  vpc_subnet_int = module.vpc.vpc_subnet_int
  vpc_subnet_ext = module.vpc.vpc_subnet_ext
  zone = var.zone
  ssh_key = "joostvdg:${file("~/.ssh/id_rsa.pub")}"
}

module "node4" {
  source = "./modules/instance"
  instance_name = "node4"
  prefix = var.instance_prefix
  network = module.vpc.vpc_link
  vpc_subnet_int = module.vpc.vpc_subnet_int
  vpc_subnet_ext = module.vpc.vpc_subnet_ext
  zone = var.zone
  ssh_key = "joostvdg:${file("~/.ssh/id_rsa.pub")}"
}

resource "google_compute_instance_group" "master" {
    name = "joostvdg-rhos311-master"
    zone =  var.master_zone
    instances = [
        module.master1.link,
    ]

    named_port {
        name = "ocp-api"
        port = "443"
    }
}

resource "google_compute_instance_group" "nodes" {
    name = "joostvdg-rhos311-nodes"
    zone =  var.master_zone
    instances = [
        module.infra1.link,
        module.node1.link,
        module.node2.link,
        module.node3.link,
        module.node4.link
    ]

    named_port {
        name = "ocp-api"
        port = "1936"
    }
}
module "ips" {
    source = "./modules/ip"
}

module "lb" {
    source = "./modules/lb"
    region = var.region
    instance_group_master = "${google_compute_instance_group.master.self_link}"
    instance_group_nodes = [
      "${var.zone}/${var.instance_prefix}-infra1",
      "${var.zone}/${var.instance_prefix}-node1",
      "${var.zone}/${var.instance_prefix}-node2",
      "${var.zone}/${var.instance_prefix}-node3",
      "${var.zone}/${var.instance_prefix}-node4"
      ]
    master_ip = module.ips.master_ip
    nodes_ip =  module.ips.nodes_ip
}




# resource "google_dns_managed_zone" "rhos311" {
#   name        = "sample-platform-com"
#   dns_name    = "sample-platform.com."
#   description = "sample-platform.com DNS zone"
# }