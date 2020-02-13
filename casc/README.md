# Configuration As Code

This is about [Configuration as Code for CloudBees Core](https://docs.beescloud.com/docs/cloudbees-core/latest/cloud-admin-guide/core-casc-modern).

## Steps

* install a git client on Operations Center
    * for example: `github-branch-source`
* create a Freestyle job
    * checks out repository
    * copies files to `${JENKINS_HOME}/jcasc-bundles-store`
* create a Master with a naming matching a bundle

## Update Bundle Configuration

If you're not sure what you'd want to configure in the bundle, or which plugins you really need.

You can first create a Managed Master how you want it to be. Then export its CASC configuration by the built-in `casc-exporter`.

You do this, by going to the following URL `<masterUrl>/core-casc-export`.

## Freestyle Job

The bash command to execute.

```bash
cp -R casc/* ${JENKINS_HOME}/jcasc-bundles-store
ls -lath ${JENKINS_HOME}/jcasc-bundles-store
```

### XML

```xml
<?xml version='1.1' encoding='UTF-8'?>
<project>
  <actions/>
  <description></description>
  <keepDependencies>false</keepDependencies>
  <properties/>
  <scm class="hudson.plugins.git.GitSCM" plugin="git@4.1.1">
    <configVersion>2</configVersion>
    <userRemoteConfigs>
      <hudson.plugins.git.UserRemoteConfig>
        <url>https://github.com/joostvdg/jenkins-examples.git</url>
      </hudson.plugins.git.UserRemoteConfig>
    </userRemoteConfigs>
    <branches>
      <hudson.plugins.git.BranchSpec>
        <name>*/master</name>
      </hudson.plugins.git.BranchSpec>
    </branches>
    <doGenerateSubmoduleConfigurations>false</doGenerateSubmoduleConfigurations>
    <submoduleCfg class="list"/>
    <extensions/>
  </scm>
  <canRoam>true</canRoam>
  <disabled>false</disabled>
  <blockBuildWhenDownstreamBuilding>false</blockBuildWhenDownstreamBuilding>
  <blockBuildWhenUpstreamBuilding>false</blockBuildWhenUpstreamBuilding>
  <triggers/>
  <concurrentBuild>false</concurrentBuild>
  <builders>
    <hudson.tasks.Shell>
      <command>cp -R casc/* ${JENKINS_HOME}/jcasc-bundles-store
ls -lath ${JENKINS_HOME}/jcasc-bundles-store</command>
    </hudson.tasks.Shell>
  </builders>
  <publishers/>
  <buildWrappers/>
</project>
```

## Repository Structure

```bash
.
├── README.md
├── mm1
│   ├── bundle.yaml
│   ├── jenkins.yaml
│   └── plugins.yaml
├── mm2
│   ├── bundle.yaml
│   ├── jenkins.yaml
│   └── plugins.yaml
├── mm3
│   ├── bundle.yaml
│   ├── jenkins.yaml
│   └── plugins.yaml
├── mm4
│   ├── bundle.yaml
│   ├── jenkins.yaml
│   └── plugins.yaml
└── security.xml
```

