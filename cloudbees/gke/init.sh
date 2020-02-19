NAMESPACES_RAW=$(kubectl get namespace -ojsonpath="{.items[*].metadata.name}")
BOOTSTRAP_NAMESPACE="bootstrap"
BOOTSTRAP_NAMESPACE_EXISTS=0
IFS=' ' # space is set as delimiter
read -ra NAMESPACES <<< "$NAMESPACES_RAW"
for i in "${NAMESPACES[@]}"; do # access each element of array
    if [ "$i" == "${BOOTSTRAP_NAMESPACE}" ]; then
        BOOTSTRAP_NAMESPACE_EXISTS=1
    fi
done

if [ "$BOOTSTRAP_NAMESPACE_EXISTS" == 1 ]; then
    echo " > Bootstrap namespace exists, doing nothing"
else
    echo " > Bootstrap namespace does NOT exist, creating..."
    kubectl create namespace ${BOOTSTRAP_NAMESPACE}
    echo " > Installing Cert Manager CRD's"
    kubectl apply --validate=false \
        -f https://raw.githubusercontent.com/jetstack/cert-manager/release-0.13/deploy/manifests/00-crds.yaml
    sleep 10
    echo " > Installing Cert Manager Cluster Issuer"
    kubectl apply -f bootstrap/cert-manager-cluster-issuer.yaml
fi

cd bootstrap && helm dependency build
cd ..

echo " > Doing Helm Install on Bootstrap"
helm upgrade bootstrap ./bootstrap --install --values bootstrap-values.yml --namespace ${BOOTSTRAP_NAMESPACE} 

echo " > Listing Resources in Bootstrap Namespace"
kubectl get all -n bootstrap