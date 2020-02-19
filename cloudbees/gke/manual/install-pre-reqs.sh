

echo "# Setting Cluster Role Binding"
kubectl create clusterrolebinding cluster-admin-binding \
  --clusterrole cluster-admin \
  --user $(gcloud config get-value account)

echo "# Configuring SSD Storage Class"
kubectl apply -f ssd-storageclass.yaml

#echo "# Installing Nginx Ingress Controller"
#kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/nginx-0.26.2/deploy/static/mandatory.yaml
#kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/nginx-0.26.2/deploy/static/provider/cloud-generic.yaml

echo "# Creating 'cloudbees-core' Namespace"
kubectl create namespace cloudbees-core

echo "# Setting 'cloudbees-core' Namespace as default"
kubens cloudbees-core
