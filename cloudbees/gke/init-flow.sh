NAMESPACES_RAW=$(kubectl get namespace -ojsonpath="{.items[*].metadata.name}")
FLOW_NAMESPACE="flow"
FLOW_NAMESPACE_EXISTS=0
IFS=' ' # space is set as delimiter
read -ra NAMESPACES <<< "$NAMESPACES_RAW"
for i in "${NAMESPACES[@]}"; do # access each element of array
    if [ "$i" == "${FLOW_NAMESPACE}" ]; then
        FLOW_NAMESPACE_EXISTS=1
    fi
done

if [ "$FLOW_NAMESPACE_EXISTS" == 1 ]; then
    echo " > ${FLOW_NAMESPACE} namespace exists, doing nothing"
else
    echo " > ${FLOW_NAMESPACE} namespace does NOT exist, creating..."
    kubectl create namespace ${FLOW_NAMESPACE}
fi

rm flow-reqs/Chart.lock
rm flow-reqs/charts/*.tgz
cd flow-reqs && helm dependency build
cd ..

echo " > Doing Helm Install on ${FLOW_NAMESPACE}"
helm install flowrq ./flow-reqs --values flow-reqs-values.yml --namespace ${FLOW_NAMESPACE} --atomic --timeout 60m0s

echo " > Listing Resources in Namespace ${FLOW_NAMESPACE}"
kubectl get all -n ${FLOW_NAMESPACE}