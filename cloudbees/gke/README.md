# Install CloudBees Core Modern on GKE

## Create Cluster

* [Via GCloud](https://joostvdg.github.io/kubernetes/distributions/install-gke/)
* [Via Terraform](https://joostvdg.github.io/kubernetes/distributions/gke-terraform/)
    * You can find the [resources here](terraform/README.md)

## Install CloudBees Core

Currently (November 2019) you have three options:

* [Direct Yaml Installation]()
* [Helm with Tiller]()
* [Helm Template]()

The Helm template of CloudBees core is [available on Helm Hub](https://hub.helm.sh/charts/cloudbees/cloudbees-core).

### Pre-requisites

* Default Storage Class Available
* Nginx Ingress Controller
* CloudBees namespece 

### Create Namespace

```
kubectl create namespace cloudbees
```

### Install Storage Class

```
kubectl apply -f ssd-storageclass.yaml
```

```
kubectl get sc
```

### Install Nginx

```
kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/master/deploy/static/mandatory.yaml
```

```
kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/master/deploy/static/provider/cloud-generic.yaml
```

#### Get IP Address For Domain

```
kubectl get svc -n ingress-nginx ingress-nginx -o jsonpath="{.status.loadBalancer.ingress[0].ip}"
```

### Configure TLS With Cert Manager

#### Install Cert Manager

```
kubectl create namespace cert-manager
```

```
kubectl apply -f https://github.com/jetstack/cert-manager/releases/download/v0.11.0/cert-manager.yaml --validate=false
```

#### Configure Certificate

```
kubectl apply -f certmanager/
```

### Helm Install

* `helm repo add cloudbees https://charts.cloudbees.com/public/cloudbees`
* `helm repo update`
* create or update `helm/values.yaml` file

#### Helm V2 With Tiller

* https://docs.cloudbees.com/docs/cloudbees-core/latest/gke-install-guide/installing-gke-using-installer
* `helm install cloudbees-core --name cbcore -f helm/values.yaml`

#### Helm Template

```
helm fetch \
  --repo https://charts.cloudbees.com/public/cloudbees \
  --version 3.7.0+ffcae9c08fc6 \
    cloudbees-core
```

```
helm template -f helm/values.yaml \
    cloudbees-core-3.7.0+ffcae9c08fc6.tgz \
    > cloudbees-core.yml
```

```
kubectl apply --namespace cloudbees -f cloudbees-core.yml
```

#### Raw Yaml

* Not Recommended
* https://docs.cloudbees.com/docs/cloudbees-core/latest/gke-install-guide/installing-gke-using-installer#_installing_cloudbees_core_using_the_installer

## Post Install

### Get Operations Center Initial Password

```
kubectl exec cjoc-0 cat /var/jenkins_home/secrets/initialAdminPassword
```