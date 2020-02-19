helm repo add joostvdg https://raw.githubusercontent.com/joostvdg/helm-repo/master/
helm repo update
helm install ldap joostvdg/opendj4 --version 0.3.1
