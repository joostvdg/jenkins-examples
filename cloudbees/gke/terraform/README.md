# GKE Via Terraform

## Prerequisites

* get CGP account
* add a billing account
* create GCP project
* enable Kubernetes Service (GKE) API on Project
* collect GCP Project ID
* install [gcloud]() cli utility
    * login with your `gcloud` -> `gcloud auth login`
    * setup your project
* install [Terraform]()

## Process

* Initialize Terraform project -> `terraform init`
* Plan the work for Terraform -> `terraform plan --out plan.out`
    * and supply it your GCP `project`
    * don't know it? `gcloud config list`
* Run Terraform -> `terraform apply "plan.out"`

### Delete

* Delete Cluster via Terraform -> `terraform destroy`
* Clean up GCP Disks
    * List disks -> `gcloud compute disks list --filter="-users:*" `
    * Delete disks -> `gcloud compute disks delete ${DISK} --zone=${ZONE} --quiet`

## Notes

### Get Kubernetes Credentials

```
gcloud container clusters get-credentials ${CLUSTER_NAME} --region ${REGION}
```

### Get Supported Kubernetes Versions

```
gcloud container get-server-config --region $REGION
```

Note: only look at the versions listed under `validMasterVersions:`.

### Get ClusterAdmin Rights

```
kubectl create clusterrolebinding cluster-admin-binding  --clusterrole cluster-admin  --user $(gcloud config get-value account)
```

## References

* 