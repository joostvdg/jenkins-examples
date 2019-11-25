package com.cloudbees.opscenter.server.model

// This will only work when run against an Operations Center instance.
// WARNING:: This is HIGHLY EXPERIMENTAL and should NOT be run against a production system. No guarantees of support are provided.
// cboc groovy = < create-managed-master.groovy sm2 scripted-masters jx-staging managed-premium

import jenkins.*
import jenkins.model.*
import hudson.*
import hudson.model.*
import com.cloudbees.masterprovisioning.kubernetes.KubernetesMasterProvisioning
import com.cloudbees.opscenter.server.model.ManagedMaster
import com.cloudbees.opscenter.server.properties.ConnectedMasterLicenseServerProperty
import com.cloudbees.opscenter.server.model.OperationsCenter
import com.cloudbees.hudson.plugins.folder.*

println '= Request for creating a Managed Master = Start'
if (this.args.length < 2) {
    println '= Requires at least one parameter, found none [name*, folderName*, namespace, storageClass] (*=required)'
}

String javaOptions = '-XshowSettings:vm -XX:+AlwaysPreTouch -XX:+UseG1GC -XX:+ExplicitGCInvokesConcurrent -XX:+ParallelRefProcEnabled -XX:+UseStringDeduplication -Dhudson.slaves.NodeProvisioner.initialDelay=0 -XX:+UseContainerSupport -XX:InitialRAMPercentage=60.0 -XX:MaxRAMPercentage=75.0 -XX:MinRAMPercentage=50.0 -Djenkins.install.runSetupWizard=false'
String masterName = this.args[0]
String folderName = this.args[1]
def namespace = null
def storageClass = null

if (this.args.length > 2) {
    namespace = this.args[2]
}

if (this.args.length > 3) {
    storageClass = this.args[3]
}

if(OperationsCenter.getInstance().getConnectedMasters().any { it?.getName()==masterName }) {
    println '== Master with this name already exists, script will exit'
    return
}

Map props = [
//    allowExternalAgents: false, //boolean
//    clusterEndpointId: "default", //String
//    cpus: 1.0, //Double
//    disk: 50, //Integer //50gb
//    domain: "", //String
//    envVars: "", //String
//    fsGroup: "1000", //String
//    image: "custom-image-name", //String -- set this up in Operations Center Docker Image configuration
      javaOptions: javaOptions, //String
//    jenkinsOptions:"", //String
//    kubernetesInternalDomain: "cluster.local", //String
//    livenessInitialDelaySeconds: 300, //Integer
//    livenessPeriodSeconds: 10, //Integer
//    livenessTimeoutSeconds: 10, //Integer
//    memory: 3072, //Integer mb
      namespace: namespace, //String
//    nodeSelectors: null, //String
//    ratio: 0.7, //Double
      storageClassName: storageClass //String
//    systemProperties:"", //String
//    terminationGracePeriodSeconds: 1200, //Integer
//    yaml:"" //String
]

def configuration = new KubernetesMasterProvisioning()
props.each { key, value ->
    configuration."$key" = value
}

def instance = Jenkins.instance
def folder = instance.getItem(folderName)

def folderExists = false
if (folder == null || !folder instanceof Folder) {
    println '== Folder does not exist, will not continue'
    return
}

println '== Creating Bare Master'
ManagedMaster master = folder.createProject(ManagedMaster.class, masterName)

println '== Applying configuration to the master'
println "=== Name=${masterName}"
println "=== JavaOptions=${javaOptions}"
println "=== Namespace=${namespace}"
println "=== StorageClass=${storageClass}"
master.setConfiguration(configuration)
master.properties.replace(new ConnectedMasterLicenseServerProperty(null))

println '== Saving Master'
master.save()

println '== Apply onModified()'
master.onModified()

println '== Provision and start Master'
master.provisionAndStartAction()
println '= Request for creating a Managed Master = End'