resource "google_container_cluster" "primary" {
  name        = "${var.name}"
  location    = "${var.location}"

  # We can't create a cluster with no node pool defined, but we want to only use
  # separately managed node pools. So we create the smallest possible default
  # node pool and immediately delete it.
  remove_default_node_pool  = true
  initial_node_count        = 1
  min_master_version        = "${var.cluster_master_version}"
  resource_labels           = {
    environment = "development"
    created-by  = "terraform"
    owner       = "joostvdg"
  }

  # Configuration options for the NetworkPolicy feature.
  network_policy {
    # Whether network policy is enabled on the cluster. Defaults to false.
    # In GKE this also enables the ip masquerade agent
    # https://cloud.google.com/kubernetes-engine/docs/how-to/ip-masquerade-agent
    enabled = true

    # The selected network policy provider. Defaults to PROVIDER_UNSPECIFIED.
    provider = "CALICO"
  }

}