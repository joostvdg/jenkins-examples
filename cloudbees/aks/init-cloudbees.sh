NAMESPACES_RAW=$(kubectl get namespace -ojsonpath="{.items[*].metadata.name}")
CLOUDBEES_NAMESPACE="cloudbees"
CLOUDBEES_NAMESPACE_EXISTS=0
IFS=' ' # space is set as delimiter
read -ra NAMESPACES <<< "$NAMESPACES_RAW"
for i in "${NAMESPACES[@]}"; do # access each element of array
    if [ "$i" == "${CLOUDBEES_NAMESPACE}" ]; then
        CLOUDBEES_NAMESPACE_EXISTS=1
    fi
done

if [ "$CLOUDBEES_NAMESPACE_EXISTS" == 1 ]; then
    echo " > ${CLOUDBEES_NAMESPACE} namespace exists, doing nothing"
else
    echo " > ${CLOUDBEES_NAMESPACE} namespace does NOT exist, creating..."
    kubectl create namespace ${CLOUDBEES_NAMESPACE}
fi

cd cloudbees && helm dependency build
cd ..

echo " > Doing Helm Install on ${CLOUDBEES_NAMESPACE}"
helm install cloudbees ./cloudbees --values cloudbees-values.yml --namespace ${CLOUDBEES_NAMESPACE} 

echo " > Listing Resources in Namespace ${CLOUDBEES_NAMESPACE}"
kubectl get all -n ${CLOUDBEES_NAMESPACE}