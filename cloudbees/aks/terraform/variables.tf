variable "client_id" {}
variable "client_secret" {}

variable "kubernetes_version" {
    default = "1.16.4"
}

variable "agent_count" {
    default = 3
}

variable "ssh_public_key" {
    default = "~/.ssh/id_rsa.pub"
}

variable "dns_prefix" {
    default = "jvdg"
}

variable cluster_name {
    default = "jvandergriendt"
}

variable resource_group_name {
    default = "jvandergriendt"
}

variable container_registry_name {
    default = "jvandergriendtacr"
}

variable location {
    default = "westeurope"
}
