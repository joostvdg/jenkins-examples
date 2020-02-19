kubectl delete NetworkPolicy zookeeper-policy
kubectl delete NetworkPolicy dois-policy
kubectl delete NetworkPolicy repository-policy
kubectl delete NetworkPolicy server-policy
kubectl delete NetworkPolicy web-policy
kubectl delete PodDisruptionBudget zookeeper
kubectl delete secret flow-dois flow-credentials flow-db flow-dois
kubectl delete configmap flow-nginx-ingress-controller
kubectl delete jobs.batch flow-server-init-job
kubectl delete ConfigMap flow-nginx-ingress-tcp
kubectl delete ConfigMap zookeeper 
kubectl delete ConfigMap flow-init-scripts flow-logging-config ingress-controller-leader-flow-ingress
kubectl delete pvc flow-repo-artifacts flow-server-shared elasticsearch-data-flow-devopsinsight-0 data-zookeeper-0 data-zookeeper-1 elasticsearch-data-flow-devopsinsight-0
kubectl delete serviceaccount flow-nginx-ingress flow-nginx-ingress-backend
kubectl delete Role flow-nginx-ingress
kubectl delete RoleBinding flow-nginx-ingress
kubectl delete svc flow-nginx-ingress-controller flow-nginx-ingress-default-backend flow-bound-agent flow-devopsinsight flow-repository flow-server flow-web zookeeper zookeeper-headless
kubectl delete deployment flow-nginx-ingress-controller flow-nginx-ingress-default-backend flow-bound-agent flow-repository flow-server flow-web
kubectl delete sts flow-devopsinsight zookeeper
kubectl delete ing flow-ingress
kubectl delete svc flow-nginx-ingress-controller
kubectl delete secret flow-dois
kubectl delete job flow-server-init-job
kubectl delete pvc flow-server-shared