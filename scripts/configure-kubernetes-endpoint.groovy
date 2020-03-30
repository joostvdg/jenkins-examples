// https://gist.github.com/jhoblitt/ce91b458526e3a03d365e2689db825f0

package com.cloudbees.opscenter.server.model

// This will only work when run against an Operations Center instance.
// WARNING:: This is HIGHLY EXPERIMENTAL and should NOT be run against a production system. No guarantees of support are provided.
// cboc groovy = < create-managed-master.groovy sm2 scripted-masters jx-staging managed-premium

import jenkins.*
import jenkins.model.*
import hudson.*
import hudson.model.*
import com.cloudbees.masterprovisioning.kubernetes.KubernetesMasterProvisioning
import com.cloudbees.masterprovisioning.kubernetes.KubernetesClusterEndpoint
import com.cloudbees.opscenter.server.model.ManagedMaster
import com.cloudbees.opscenter.server.properties.ConnectedMasterLicenseServerProperty
import com.cloudbees.opscenter.server.model.OperationsCenter
import com.cloudbees.hudson.plugins.folder.*
import hudson.slaves.Cloud;

def clouds = Cloud.all()
println "clouds=$clouds"
println "number of clouds: " + clouds.size()

for(int i=0; i < clouds.size(); i++) {
  def cloud = clouds[i]
  if (cloud instanceof org.csanchez.jenkins.plugins.kubernetes.KubernetesCloud$DescriptorImpl) {
    println "cloud is KubernetesCloud"
    println "Display name: "+ cloud.getDisplayName()
    println  "getServerUrl:" + cloud.toString()
  }
}

Jenkins jenkins = Jenkins.getInstance();
KubernetesMasterProvisioning.DescriptorImpl kmp = (KubernetesMasterProvisioning.DescriptorImpl) jenkins.getDescriptor(KubernetesMasterProvisioning.class);

def clusterEndpoints = kmp.getClusterEndpoints();
for (int i=0; i<clusterEndpoints.size();i++) {
    endpoint = clusterEndpoints[0]
    if ("kubernetes".equals(endpoint.getName())) {
        println "found the main kubernetes endpoint"
        println "Endpoint: " + endpoint
    }
}

def id = "openshift"
def name = "openshift"
def url = "https://api.ocp.kearos.net"
def credentialsId = "osuserpass"
def namespace = "cb-mm1"
def ingressClass = "nginx"
def clusterDomain = "myclusterdomain"
def resourceHost = "myresourceHost"

def openshiftEndpoint = new KubernetesClusterEndpoint(id, name, url, credentialsId, namespace, ingressClass)
openshiftEndpoint.setClusterDomain(clusterDomain)
// Setting a separate resource host is not currently supported on OpenShift (you would need to manually add a Route)
//openshiftEndpoint.setResourceHost(resourceHost)
// serverCertificate
// masterUrlPattern -> >https://core.apps.ocp.kearos.net/*/
openshiftEndpoint.setJenkinsUrl("https://core.cb-eks.kearos.net/cjoc/configure")
openshiftEndpoint.setMasterUrlPattern("https://core.apps.ocp.kearos.net/*/")
openshiftEndpoint.setSkipTlsVerify(true)
clusterEndpoints.add(openshiftEndpoint)
kmp.setClusterEndpoints(clusterEndpoints)
kmp.save()
