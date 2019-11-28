# Jenkins CLI

This assumes we use the CLI with the alias `cboc` and we have the tool [jq](https://stedolan.github.io/jq/).

* https://go.cloudbees.com/docs/cloudbees-core/operations-center-admin-guide/managing/#accessing-jenkins-cli

## Get CLI

First we need to retrieve the CloudBees CLI from the Operations Center.

### Retrieve client Jar

```bash
export CJOC_URL=https://core.cb-gke.kearos.net/cjoc/
http --download ${CJOC_URL}/jnlpJars/jenkins-cli.jar --verify false
```

### Configure CLI Alias

The CLI base command is a bit long, due to the parameters we have to give it.
So I recommend creating a alias (`cbc`) to make it easier to use.

#### By User/Password

We can use the CLI via User:Password, but it is more secure to do it via User:Token.

```bash
USR=jvandergriendt
PSS=
```

```bash
alias cboc="java -jar jenkins-cli.jar -noKeyAuth -auth ${USR}:${PSS} -s ${CJOC_URL}"
```

#### By User/Token

To generate a token, log in and go your personal information (top right corner, click on your name).
And generate a new token via the UI.

```bash
USR=jvandergriendt
TKN=11b9902cdef8805aa5c19c6198a54ea0ee
```

```bash
alias cboc="java -jar jenkins-cli.jar -noKeyAuth -auth ${USR}:${TKN} -s ${CJOC_URL}"
```

### Verify

The `cboc version` command should return the version of the Operations Center.

```bash
cboc version
```

```bash
2.190.3.2
```

## Configure LDAP & RBAC

```bash
SERVER=opendj4:389
PASSWORD=
```

```bash
cboc groovy = < ../scripts/rbac/jenkins-ldap-config.groovy ${SERVER} ${PASSWORD}
```

### Result

```bash
=== Configuring the LDAP == Start
= validation: OK
= connnectionCheck: OK: <div/>
=== Configuring the LDAP == End
=== Configuring the RBAC == Start
= Enabling role based authorisation strategy...
= Initializing RBAC with Typical Setup
= Can we do the Typical Setup? true
= Found group: Administrators
= Found group: Developers
= Found group: Browsers
=== Configuring the RBAC == End
```

You should now be able to login with users from the LDAP configuration.
An example configuration can be found in [single-sign-on/ldap](../single-sign-on/ldap/Example.ldif).

## Create Managed Master In Separate Namespace

### Configure Namespace

```yaml
nginx-ingress:
  Enabled: false

Persistence:
    StorageClass: ssd

Master:
    OperationsCenterNamespace: cloudbees

OperationsCenter:
    Enabled: false
```

```bash
helm template -f ../cloudbees/gke/helm/master-namespace-values.yaml \
    ../cloudbees/gke/cloudbees-core-3.7.0+ffcae9c08fc6.tgz \
    --namespace cbmasters\
    > cloudbees-core-cbmasters-namespace.yml
```

```bash
kubectl create namespace cbmasters
kubectl apply -f cloudbees-core-cbmasters-namespace.yml --namespace cbmasters
```

#### Result

```bash
namespace/cbmasters created
configmap/jenkins-agent created
serviceaccount/jenkins created
role.rbac.authorization.k8s.io/cjoc-master-management created
role.rbac.authorization.k8s.io/cjoc-agents created
rolebinding.rbac.authorization.k8s.io/cjoc-role-binding created
rolebinding.rbac.authorization.k8s.io/cjoc-master-role-binding created
```

### Create Master Via Script

```bash
cboc groovy = < ../scripts/create-managed-master.groovy sm1 scripted-masters cbmasters
```

#### Result

```bash
= Request for creating a Managed Master = Start
== Creating Bare Master
== Applying configuration to the master
=== Name=sm1
=== JavaOptions=-XshowSettings:vm -XX:+AlwaysPreTouch -XX:+UseG1GC -XX:+ExplicitGCInvokesConcurrent -XX:+ParallelRefProcEnabled -XX:+UseStringDeduplication -Dhudson.slaves.NodeProvisioner.initialDelay=0 -XX:+UseContainerSupport -XX:InitialRAMPercentage=60.0 -XX:MaxRAMPercentage=75.0 -XX:MinRAMPercentage=50.0 -Djenkins.install.runSetupWizard=false
=== Namespace=cbmasters
=== StorageClass=null
== Saving Master
== Apply onModified()
== Provision and start Master
= Request for creating a Managed Master = End
```