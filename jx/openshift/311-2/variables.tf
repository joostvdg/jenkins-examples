variable "project" { }

variable "region" {
  default ="europe-west4"
}


variable "name" {
  description = "The name of the cluster (required)"
  default     = "jx-openshift-311"
}

variable "compute_machine_type" {
  default = "n1-standard-4"
}

variable "master_machine_type" {
  default = "n1-standard-8"
}

variable "instance_prefix" {
    default ="joostvdg-rhos311"
}


variable "vm_image" {
  default ="rhel-7-v20200205"
}

variable "master_zone" {
    description = "Zone in which the Master Node will be created"
    default = "europe-west4-a"
}

variable "zone" {
    default = "europe-west4-a"
}


# variable "uc1_public_subnet" {
#       default = "10.26.2.0/24"
# }
# variable "ue1_private_subnet" {
#       default = "10.26.3.0/24"
# }
# variable "ue1_public_subnet" {
#     default = "10.26.4.0/24"
# }