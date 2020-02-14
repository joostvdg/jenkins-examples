resource "azurerm_resource_group" "ecr" {
    name     = "${var.resource_group_name}-acr"
    location = var.location
}

resource "azurerm_container_registry" "acr" {
    name                            = var.container_registry_name
    resource_group_name = azurerm_resource_group.ecr.name
    location                        = azurerm_resource_group.k8s.location
    sku                                = "Premium"
    admin_enabled              = false
}
