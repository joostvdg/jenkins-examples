resource "azurerm_storage_account" "example" {
    name                     = "jvandergriendtfs"
    resource_group_name      = azurerm_resource_group.k8s.name
    location                      = azurerm_resource_group.k8s.location
    account_tier              = "Premium"
    account_replication_type = "LRS"
    account_kind = "FileStorage"

    tags = {
        Environment = "Development"
        CreatedBy = "Jvandergriendt"
    }
}