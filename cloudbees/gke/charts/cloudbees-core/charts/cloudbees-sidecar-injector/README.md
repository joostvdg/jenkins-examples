# Using self-signed certificates in CloudBees Core

This optional component of CloudBees Core allows to use self-signed certificates or custom root CA.

# Prerequisites

Kubernetes 1.9 or later, with admission controller `MutatingAdmissionWebhook` enabled.

In order to check whether it is enabled for your cluster, you can run the following command:
                              
```
kubectl api-versions | grep admissionregistration.k8s.io/v1beta1
```

The result should be:

```
admissionregistration.k8s.io/v1beta1
```

# Installation

This procedure requires the ability to install helm charts.

## Create a certificate bundle

Assuming you are working in the namespace where CloudBees Core is installed,
and the certificate you want to install is named `mycertificate.pem`.

For a self-signed certificate, add the certificate itself.
If the certificate has been issued from a custom root CA, add the root CA itself.

```
kubectl cp cjoc-0:/etc/ssl/certs/ca-certificates.crt .
kubectl cp cjoc-0:/etc/ssl/certs/java/cacerts .
cat mycertificate.pem >> ca-certificates.crt
keytool -import -noprompt -keystore cacerts -file mycertificate.pem -storepass changeit -alias service-mycertificate;
kubectl create configmap --from-file=ca-certificates.crt,cacerts ca-bundles
```

## Setup injector

1. Create a namespace to deploy the sidecar injector

   ```
   kubectl create namespace sidecar-injector
   ```

2. Review chart values
   ```
   helm inspect values cloudbees/cloudbees-sidecar-injector
   ```
   In particular, pay attention to the `caBundleCrt` field if your kubernetes cluster has been set up to use a different signing cert than the root CA used for api server. 

3. Install the CloudBees sidecar injector using helm. Inspect the possible values using
   

   ```
   helm install cloudbees/cloudbees-sidecar-injector --name cloudbees-sidecar-injector --namespace cloudbees-sidecar-injector
   ```

## Configure namespace

1. Label the namespace where CloudBees Core is installed with `sidecar-injector=enabled`

   ```
   kubectl label namespace mynamespace sidecar-injector=enabled
   ```

2. Check
   ```
   # kubectl get namespace -L sidecar-injector
   NAME          STATUS    AGE       SIDECAR-INJECTOR
   default       Active    18h
   mynamespace   Active    18h       enabled
   kube-public   Active    18h
   kube-system   Active    18h
   ```

## Verify

1. Deploy an app in Kubernetes cluster, take `sleep` app as an example

   ```
   kubectl run sleep --generator=run-pod/v1 --image tutum/curl --command /bin/sleep infinity
   ```

2. Verify injection has happened
   ```
   kubectl get pods/sleep -o 'go-template={{.metadata.name}}{{"\n"}}{{range $key,$value := .metadata.annotations}}* {{$key}}: {{$value}}{{"\n"}}{{end}}{{"\n"}}'
   sleep
   * com.cloudbees.sidecar-injector/status: injected
   ```

## Conclusion

You are now all set to use your custom CA. If you restart CJOC and running masters, they will pick up the new certificate bundle.
When scheduling new build agents, they will also pick up the certificate bundle and allow connection to remote endpoints using your certificates.

## FAQ

### Disable injection for a specific pod

Annotate the pod `com.cloudbees.sidecar-injector/inject: no`

### Injection doesn't work and pod has tls errors.

The sidecar-injector logs contains the following
`http: TLS handshake error from aaa.bbb.ccc.ddd:nnnnn: remote error: tls: bad certificate`

This can happen if the API server TLS certificate differs from the cluster signing certificate. To fix this, you need to provide the cluster signing certificate as an input to the installation.

Create a `values.yaml` file as follow (replace the content by your own CA cert)
```
caBundleCrt: |-
  -----BEGIN CERTIFICATE-----
  MIICyDCCAbCgAwIBAgIBADANBgkqhkiG9w0BAQsFADAVMRMwEQYDVQQDEwprdWJl
  cm5ldGVzMB4XDTE5MDcyOTA3MzQyMloXDTI5MDcyNjA3MzQyMlowFTETMBEGA1UE
  AxMKa3ViZXJuZXRlczCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBALgQ
  fjIm8iVEpmX3tlcUzuH2BAQGzK0utC6S0hhqnK6zlQ9iGDAwDpAiwGB9VzcJSfK7
  fr3rx4zT9rWfVeot+ARQV/NPxSUpPlGK8WsRleg5wKyUdnE1xKZDly2l2Vpqlr0J
  GnMXb2A0roi685XZo6iQALLfo+rtWQ2y2JLXzGYYCB1sAUX3hM3qbYmIMReBIyMX
  YGUUdaMuWU1YazKy3eJ84Am7l9ZXlMm7infJlAFsM3BCKed9ZxO2KxTvhWv1qbUk
  Bj3GJrL2bJfQi3B6h0piiBDt6YeI3U8yU4EyxtMKQwQXs9T1zHloc6RmVGYYVAEl
  HquW/XIk4ebWTxYND6UCAwEAAaMjMCEwDgYDVR0PAQH/BAQDAgKkMA8GA1UdEwEB
  /wQFMAMBAf8wDQYJKoZIhvcNAQELBQADggEBABsJvnyo14a8FR6y4JGSX1SXSmtS
  uRiWH6qo5Ou+4zIw0LIhDQTtaN44G86BzhvQFdeQzfvyrKXfuYUTOQz/iYPNFsO6
  FOcDg9EcA19n5tSGp8SniyDEe6EhBWa5A9UR2RPEg/8NZRoeZ/2G9SjUqoa/Erxn
  IPlZvNu+gMEK7etysUQ33s4fp+jD6p0pbWKgSQAiDRVHi3Khlhcn7DfM0ncrSQBs
  vgFPSEczjpl8LR6c0pLSdPUHgdK6pDLTYdtdytRNfJAVjAREYL4uSb8I5NKdkgEz
  BLvnDfdDoCeOZoaeLR68jCltNfdzT5/d1v086i7uRepGhQ5w7ehtPuZ+0U8=
  -----END CERTIFICATE-----
```

Then re-run the installation

```
helm del --purge cloudbees-sidecar-injector
helm install cloudbees/cloudbees-sidecar-injector --name cloudbees-sidecar-injector --namespace cloudbees-sidecar-injector -f values.yaml
```