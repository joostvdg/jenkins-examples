variable "project" { }

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

variable "vm_image" {
  default ="rhel-7-v20200205"
}

variable "master_zone" {
    description = "Zone in which the Master Node will be created"
    default = "europe-west4-a"
}
