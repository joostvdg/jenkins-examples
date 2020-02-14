# resource "azurerm_virtual_network" "example" {
#     name                = "example-network"
#     address_space       = ["10.0.0.0/8"]
#     location            = azurerm_resource_group.k8s.location
#     resource_group_name = azurerm_resource_group.k8s.name
# }

# resource "azurerm_subnet" "example" {
#     name                 = "aks"
#     resource_group_name  = azurerm_resource_group.k8s.name
#     virtual_network_name = azurerm_virtual_network.example.name
#     address_prefix       = "10.0.2.0/24"
#     route_table_id       = azurerm_route_table.example.id
# }

# resource "azurerm_route_table" "example" {
#     name                = "example-routetable"
#     location            = azurerm_resource_group.k8s.location
#     resource_group_name = azurerm_resource_group.k8s.name

#     route {
#         name                   = "example"
#         address_prefix         = "10.100.0.0/14"
#         next_hop_type          = "VirtualAppliance"
#         next_hop_in_ip_address = "10.10.1.1"
#     }
# }

# resource "azurerm_subnet_route_table_association" "example" {
#     subnet_id      = azurerm_subnet.example.id
#     route_table_id = azurerm_route_table.example.id
#     depends_on = ["azurerm_subnet.example"]
# }