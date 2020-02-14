kubectl delete ClusterRole bootstrap-nginx
kubectl delete ClusterRoleBinding bootstrap-nginx
kubectl delete -f https://raw.githubusercontent.com/jetstack/cert-manager/release-0.13/deploy/manifests/00-crds.yaml