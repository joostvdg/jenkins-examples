variable "project" { }

variable "name" {
  description = "The name of the cluster (required)"
  default     = "joost-cbcore"
}

variable "description" {
  description = "The description of the cluster"
  default     = "CloudBees Core environment for Joost"
}

variable "location" {
  description = "The location to host the cluster"
  default     = "europe-west4"
}

variable "cluster_master_version" {
  description = "The minimum kubernetes version for the master nodes"
  default     = "1.15.9-gke.8"
}

variable "np1count" {
  description = "The initial Node Count for NodePool 2"
  default     = 1
}

variable "np2count" {
  description = "The initial Node Count for NodePool 2"
  default     = 1
}