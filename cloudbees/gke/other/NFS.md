* https://cloud.google.com/community/tutorials/gke-filestore-dynamic-provisioning

```bash
gcloud services enable file.googleapis.com
```

```bash
FS=[NAME FOR THE FILESTORE YOU WILL CREATE]
gcloud beta filestore instances create ${FS} \
    --project=${PROJECT} \
    --zone=${ZONE} \
    --tier=STANDARD \
    --file-share=name="volumes",capacity=1TB \
    --network=name="default",reserved-ip-range="10.5.0.0/29"
```

```bash
FSADDR=$(gcloud beta filestore instances describe ${FS} \
     --project=${PROJECT} \
     --zone=${ZONE} \
     --format="value(networks.ipAddresses[0])")
```

## PVC

```yaml
apiVersion: "v1"
kind: "PersistentVolumeClaim"
metadata: 
  name: "docker-cache"
  namespace: "cloudbees"
spec: 
  accessModes:
    - ReadWriteMany
  resources:
    requests:
      storage: 50Gi
```

## Jenkins X

### Requirements

```yaml
- name: nfs-client-provisioner
  version: 1.2.8
  repository: https://kubernetes-charts.storage.googleapis.com
  alias: nfs
```

### Values

The value `10.5.0.2` depends on the created Filestore.
If using the config above, it would be the first one created.

```yaml
nfs:
  nfs:
    server: 10.5.0.2
    path: "/volumes"
```