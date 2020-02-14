# AKS Installation

## Pre-requisites

* Helm V3
* Terraform `0.12`+
* Azure CLI
* Bash capable shell

## Process

* tweak configuration to your liking
* use Terraform to create AKS cluster
    * with a node large enough for CloudBees Flow (Server)
    * with a Windows Node Pool
* run init scripts
    * `helm-repos-init.sh`
    * `init.sh`
    * PAUSE, add LoadBalancer IP to your DNS!
    * `init-cloudbees.sh`
    * `init-flow.sh`

## Troubleshooting

If flow times out and you cannot upgrade because

```bash
Error: UPGRADE FAILED: "flow" has no deployed releases
```

* https://github.com/helm/helm/issues/5595#issuecomment-580151340

```bash
kubectl get secrets --show-labels | grep sh.helm.release.v1
```

> So do for latest kubectl edit secret sh.helm.release.v1.helm-must-die.v36 and set label status=deployed and for release before it (v35) set label status=superseded next helm upgrade --install ... will work

## Zookeeper

```bash
kubectl run --attach bbox --image=busybox --restart=Never -- sh -c 'while true; do for i in 0 1 2; do echo zk-${i} $(echo stats | nc <pod-name>-${i}.<headless-service-name>:2181 | grep Mode); sleep 1; done; done';
```

## Terraform

### Create Service Principle

First, retrieve subscription ID.

```bash
az account show --query "{subscriptionId:id, tenantId:tenantId}"
```

Generate Service Principle (SP).

```bash
az ad sp create-for-rbac --role="Owner" --scopes="/subscriptions/${SUBSCRIPTION_ID}"
```

Fill in and "export" environment variables for Terraform.

```bash
ARM_SUBSCRIPTION_ID=
ARM_CLIENT_ID=
ARM_CLIENT_SECRET=
ARM_TENANT_ID=
```

`ARM_CLIENT_ID` is the same as `appId`.

For Terraform (not sure why again), set these as well:

```bash
export TF_VAR_client_id=<your-client-id>
export TF_VAR_client_secret=<your-client-secret>
```

### Create Resource Group

```bash
LOCATION=westeurope
RESOURCE_GROUP_NAME=joostvdg-cb-ext-storage
```

It is best to throw everything into a single-purpose Resource Group.

This way, when we're done, we only have to delete the Resource Group!

```bash
az group create \
    --name ${RESOURCE_GROUP_NAME} \
    --location ${LOCATION}
```

### Run Terraform To Create The Cluster

* initialize the Terraform provider
* validate the configuration
* plan the changes to be made
* apply the changes

#### Init

```bash
terraform init
```

#### Validate

```bash
terraform validate
```

#### Plan

```bash
terraform plan -out plan.out
```

#### Apply

```bash
terraform apply plan.out
```

### Fix Autoscaling Not Working

```bash
az aks nodepool update --resource-group <> --cluster-name <> --name nodepool --disable-cluster-autoscaler
```

```bash
az aks nodepool update --resource-group <> --cluster-name <> --name nodepool --enable-cluster-autoscaler --min-count 2 --max-count 3
```

### Retrieve Kubernetes Config (kubectl)

```bash
AKS_RESOURCE_GROUP=jvandergriendt
AKS_CLUSTER_NAME=jvandergriendt
```

```bash
az aks get-credentials --resource-group ${AKS_RESOURCE_GROUP} --name ${AKS_CLUSTER_NAME}
```

### Enable Windows Node Pools Preview

* https://docs.microsoft.com/en-us/azure/aks/windows-container-cli

Install the aks-preview extension

```bash
az extension add --name aks-preview
```

Update the extension to make sure you have the latest version installed

```bash
az extension update --name aks-preview
```

Register Windows Node Pools preview feature.

```bash
az feature register --name WindowsPreview --namespace Microsoft.ContainerService
```

Confirm the preview feature is enabled, it might be in "Registering" status for a while, so you're likely to have to repeat it.

```bash
az feature list -o table --query "[?contains(name, 'Microsoft.ContainerService/WindowsPreview')].{Name:name,State:properties.state}"
```

Once it is "Registered", update the provider configuration.

```bash
az provider register --namespace Microsoft.ContainerService
```

## Create Azure File Storage Class

* see: https://docs.microsoft.com/en-us/azure/aks/azure-files-dynamic-pv

### Storage Class

```yaml
kind: StorageClass
apiVersion: storage.k8s.io/v1
metadata:
  name: azurefile
provisioner: kubernetes.io/azure-file
mountOptions:
  - dir_mode=0777
  - file_mode=0777
  - uid=1000
  - gid=1000
  - mfsymlinks
  - nobrl
  - cache=none
parameters:
  skuName: Standard_LRS
```

### PVC Test

```yaml
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: azurefile
spec:
  accessModes:
    - ReadWriteMany
  storageClassName: azurefile
  resources:
    requests:
      storage: 5Gi
```

## Bootstrap Config

* execute `init.sh`

## PVCs Stuck In Terminating

*  https://stackoverflow.com/questions/51358856/kubernetes-cant-delete-persistentvolumeclaim-pvc

You might have to remove the finalizers.

Warning, I don't know what they do!

```bash
kubectl patch pvc PVC_NAME -p '{"metadata":{"finalizers": []}}' --type=merge
```

## Windows Node Pool

* https://docs.microsoft.com/en-us/azure/aks/windows-node-limitations

## CloudBees Config


### Flow

* add MySQL server
* create storage account
    * type `FileStorage`
* https://github.com/cloudbees/cloudbees-examples/blob/master/flow-on-kubernetes/values.yaml

```bash
helm install cloudbees/cloudbees-flow --name flow-server -f flow-values-sko.yaml --namespace flow --timeout 10000 --set-file ingress.certificate.key=key.pem --set-file ingress.certificate.crt=cert.pem --set ingress.host="cloudbees.flow.pscbdemos.com"
```