# cloudbees-core

[CloudBees Core](https://www.cloudbees.com/products/cloudbees-core) is the continuous delivery platform architected for the enterprise. It provides:

* DevOps at scale
* Resilience and high availability
* Easy management
* Enterprise grade security

## TL;DR;

```console
$ helm repo add cloudbees https://charts.cloudbees.com/public/cloudbees
$ helm install cloudbees-core --name <release name> --set OperationsCenter.HostName='<hostname>'
```

## Introduction

This chart bootstraps a CloudBees Core deployment on a [Kubernetes](http://kubernetes.io) cluster using the [Helm](https://helm.sh) package manager.

## Prerequisites
  - Kubernetes 1.10 or higher
  - Helm 2.12 or higher

## Installing the Chart

### Default installation

To install the chart with the release name `cloudbees-core` and hostname `cloudbees-core.example.com`. The default installation requires nginx-ingress controller to be installed. The chart can install the nginx-ingress controller for you. This installation is described in the next section.

```console
$ helm install --name cloudbees-core --set OperationsCenter.HostName='cloudbees-core.example.com' cloudbees/cloudbees-core
```

> **NOTE**: ` OperationsCenter.HostName` field is required. The chart will not install without a host name being provided.
If you you don't have Domain name, you can use a DNS wild card service like [xip.io](http://xip.io/) to provide `OperationsCenter.HostName` value.

The command deploys CloudBees Core on the Kubernetes cluster in the default configuration. The [configuration](#configuration) section lists the parameters that can be configured during installation.

### Ingress Controller Installation 

The chart is designed, so it can install an nginx-ingress controller. 
The `nginx.ingress.Enabled` field controls ingress controller installation and setup. 
To install the chart with the release name `cloudbees-core` and hostname cloudbees-core.example.com.

```console
$ helm install --name cloudbees-core --set OperationsCenter.HostName='cloudbees-core.example.com' --set nginx.ingress.Enabled=true cloudbees-core --set OperationsCenter.HostName.ServiceType='ClusterIP'
```

## Uninstalling the Chart

To uninstall/delete the `cloudbees-core` deployment:

```console
$ helm delete cloudbees-core
```
> **NOTE**: The current version of the CloudBees Core Helm Chart only manages the Operation Center.
Users should manage Managed Master using Operation Center. 

The `helm delete` command stops the CloudBees Core deployment than removes the OperationsCenter Center. 
The release is still stored in the Helm database, but it will now have the status deleted. 
If you wish to completely remove the release, use the following variation of the `helm delete` command.

```console
$ helm delete cloudbees-core --purge
``` 

> **IMPORTANT**: The `helm delete` command does NOT remove the persistent volume claims as precaution against data losss.
You will need to use the `kubectl delete pvc` command to delete the persistent volumn claims. 


The command removes all the Kubernetes components associated with the chart and deletes the release.

## Configuration

The following tables list the configurable parameters of the CloudBees Core chart and their default values. 
Each property can override a default value with a value that specific to your Kubernetes cluster
You can provide this values using the `--Set` flag on the Helm command line. 
Helm also support merging values files together, so that you can create a YAML file for each environment. 
We discuss this later in this document in the [Environment Property Value Files](#Environment Property Value Files) section.  

### Platform Configuration Value
The property `OperationsCenter.platform` is used to set platform and/or environment.
Support vales are:

|Platform|Description|
|---------|-----------|
|`aws`|Configures the cluster for [Amazon Elastic Load Balancing](https://aws.amazon.com/elasticloadbalancing/)|
|`openshift`| Configures the cluster for OpenShift 3.x
|`standard`|Install CloudBees Core for Modern Platforms with no customer annotations using an ingress.

### Operations Center Configuration Values
The following properties manage the Operation Center installation and operation. 
CloudBees provides recommended values for these options based on our experience of helping hundreds of customers utilize Jenkins or CloudBees Core. 
 

| Parameter                         | Description                          | Default                                                                      |
| --------------------------------- | ------------------------------------ | ---------------------------------------------------------------------------- |
| `OperationsCenter.HostName`                     | CloudBees Operation Center HostName <br /> This is a *required* field.                  | Not Set
| `OperationsCenter.Protocol`                     | Whether the cluster should be accessed through HTTPS or HTTP                  | http
| `OperationsCenter.Image.dockerImageTag`     | Operation Center's Docker image tag                     | The current Docker Image.                                                                    |
| `OperationsCenter.Image.dockerPullPolicy`   | Operation Center's Docker image pull policy             | `Always`                                                                     |
| `OperationsCenter.Image.pullSecret`          | Operation Center's Docker image pull secret             | Not set   | `Master.Name`                     | Jenkins master name                  | `jenkins-master`                                                             |a
| `OperationsCenter.resources.limits.cpu`                | Resources allocation limits | 1 |
|`OperationsCenter.ServiceAnnotations` | Service annotations for the Operations Center service. <BR /> Many cloud Service providers have an internal load balancer implementation. These can be configured using annotations on the service. For more information, see the [internal load balancer](https://kubernetes.io/docs/concepts/services-networking/service/#internal-load-balancer_) section Kubernetes documentation. | Not Set|
|`OperationsCenter.HealthProbes` | Enables Kubernetes Liveness and Readiness Probes | true |
|`OperationsCenter.HealthProbesTimeout` | Operations Center Health Probe Timeout. <br /> (This is set to approximately five minutes to allow Operations Center to restart when upgrading plugins. ) | 300 |
|`OperationsCenter.HealthLivenessFailureThreshold` | Set the failure threshold for the liveness probe. This number of times Kubernetes will attempt to the health provide before determining Operation Center is `unready` | 12 |
|`OperationsCenter.CSRF.ProxyCompatibility` | proxy compatibility for the default CSRF crumb issuer. | false |
|`OperationsCenter.JavaOpts` | Additional Java options to pass to Operation Center. For example, setting up a JMX port. | Not Set |
|`OperationsCenter.NodeSelector` | Node labels for pod assignment <br />This is an advanced Kubernetes feature, see [nodeSelector](https://kubernetes.io/docs/concepts/configuration/assign-pod-node/#nodeselector) section of Kubernetes documentation for information. | Not Set |
|`OperationsCenter.Tolerations` | This is an advanced Kubernetes feature, see [Tolerations](https://kubernetes.io/docs/concepts/configuration/assign-pod-node/#taints-and-tolerations-beta-feature) section of Kubernetes documentation for information. | Not Set |


 ### Master Configuration Values
 The following properties manage which Docker Image is used in the creation of the Managed Master. 
 
 
| Parameter                          | Description                          | Default                                                                      |
| ---------------------------------- | ------------------------------------ | ---------------------------------------------------------------------------- |
| `Master.Image`                     | Managed Master Docker image                  | `cloudbees/cloudbees-core-mm`                                                            |
| `Master.Image.dockerImageTag`      | Managed Master Docker image tag                     | The current Docker Image                                                                    |
| `Master.Image.dockerPullPolicy`    | Managed Master Docker image pull policy             | `Always`                                                                     |
| `Master.Image.longName`            | Managed Master Name displayed in the CloudBees Core User Interface, where `(version)` is the current version fo CloudBees Core.                                  | `CloudBees Core - Managed Master (version)`                              |
| `Master.JavaOpts`                  | Additional Java options to pass to managed masters. For example, setting up a JMX port. | Not Set |
| `Master.OperationsCenterNamespace` | When deploying Master resources, this grants an Operations Center deployed in another namespace the right to deploy masters | `null`                              |

### Persistence
The Persistent properties that allow the use of alternative storage classes and reusing an existing Persistent Volumes Claim. See migrating an existing CloudBees installation to the Helm installation package system in CloudBees Core instructions.
 

| Parameter                         | Description                          | Default                                                                      |
| --------------------------------- | ------------------------------------ | ---------------------------------------------------------------------------- |
| Persistence.ExistingClaim         | Instead of creating a new Persistent Volumes Claim, the Operation Center will use this existing Persistent Volumes Claim | Not Set| 
| Persistence.StorageClass          | If undefined, the default, the provisioner will use the default  `storageClassName`.  On AWS  gp2 is used. | Not Set

### RBAC
In case of a deployment in a restricted environment where cluster-admin privilege is not granted, or even rbac objects can't be created by the user deploying the product,
it is possible to omit the corresponding objects from deployment.

| Parameter                            | Description                                                                                 | Default                            |
| ------------------------------------ | ------------------------------------------------------------------------------------------- | ---------------------------------- |
| rbac.install                         | Whether to install RBAC objects in the cluster                                              | true                               | 
| rbac.installCluster                  | Whether to install Cluster-level RBAC objects in the cluster                                | true                               |
| rbac.serviceAccountName              | Name of the service account Operations Center will run as                                   | cjoc                               |
| masterServiceAccountName             | Name of the service account Jenkins masters will run as                                     | jenkins                            |
| hibernationMonitorServiceAccountName | Name of the service account the Hibernation Monitor will run as                             | managed-master-hibernation-monitor |
| apiGroup                             | API group for RBAC objects (can be overridden in specific cases of deployment to Openshift) | Not Set                            |
| apiVersion                           | API group for RBAC objects (can be overridden in specific cases of deployment to Openshift) | Not Set                            |


### Nginx Ingress 
The NGINX Ingress properties provided the option to install an NGINX Ingress controller.
The default option of false, does not install the NGINX Ingress controller and will require an existing NGINX Ingress controller to be installed.

| Parameter                         | Description                          | Default                                                                      |
| --------------------------------- | ------------------------------------ | ---------------------------------------------------------------------------- |
| nginx.ingress.Enabled             | Setting this property to true installs NGINX Ingress controller in addition to CloudBees Core.  | `false`

### Environment Property Value Files
Helm provides the option to use a custom property values file to override the default values set in the `values.yaml` file.  CloudBees recommend creating a custom properties file to override the default for your environments, instead of directly editing the included values.yaml file. 

To use an environment property value file with Helm, use the -f option as shown in the following example:
`helm install cloudbees-core --name cloudbees-core -f example-values.yaml`

You can download the latest version of the `example-values.yaml` file from CloudBees Examples GitHub repository at https://github.com/cloudbees/cloudbees-examples/tree/master/helm-custom-value-file-examples.

## Additional Documentation
CloudBees provides complete and more detail installation and operation documentation on the CloudBees web site at https://go.cloudbees.com/docs/cloudbees-core/cloud-install-guide/kubernetes-helm-install/
