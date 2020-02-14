

resource "azurerm_kubernetes_cluster" "k8s" {
  name                = var.cluster_name
  location            = azurerm_resource_group.k8s.location
  resource_group_name = azurerm_resource_group.k8s.name
  dns_prefix          = "jvdg"
  kubernetes_version  = var.kubernetes_version

  default_node_pool {
    name            = "default"
    vm_size         = "Standard_D2s_v3"
    os_disk_size_gb = 30
    enable_auto_scaling = true
    min_count = 3
    max_count = 4
    type = "VirtualMachineScaleSets"
    # vnet_subnet_id = "${azurerm_subnet.example.id}"
  }

    role_based_access_control {
        enabled = true
    }

    service_principal {
        client_id     = var.client_id
        client_secret = var.client_secret
    }

    network_profile {
      network_plugin = "azure"
      load_balancer_sku = "standard"
    }

    windows_profile {
      admin_username = "windowsadmin"
      admin_password = "w3SdUKkWkbuZY799ndNfYvWH"
    }

    tags = {
        Environment = "Development"
        CreatedBy = "Jvandergriendt"
    }
}

resource "azurerm_kubernetes_cluster_node_pool" "pool-linux" {
  name                  = "plin"
  kubernetes_cluster_id = azurerm_kubernetes_cluster.k8s.id
  # vnet_subnet_id = "${azurerm_subnet.example.id}"
  vm_size               = "Standard_D4s_v3"
  os_type                = "Linux"
  enable_auto_scaling = true
  min_count = 2
  max_count = 3
}

resource "azurerm_kubernetes_cluster_node_pool" "pool-windows" {
  name                  = "pwin"
  kubernetes_cluster_id = azurerm_kubernetes_cluster.k8s.id
  # vnet_subnet_id = "${azurerm_subnet.example.id}"
  vm_size               = "Standard_D4s_v3"
  os_type                = "Windows"
  node_taints = ["windows=true:NoSchedule"]
  enable_auto_scaling = true
  min_count = 1
  max_count = 2
}

output "client_certificate" {
  value = "azurerm_kubernetes_cluster.k8s.kube_config.0.client_certificate}"
}

output "kube_config" {
  value = "azurerm_kubernetes_cluster.k8s.kube_config_raw}"
}