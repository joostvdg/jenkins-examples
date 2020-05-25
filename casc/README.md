# Configuration As Code

This is about [Configuration as Code for CloudBees Core](https://docs.beescloud.com/docs/cloudbees-core/latest/cloud-admin-guide/core-casc-modern).

As of this writing - February 2020 - there's no UI component. 
In order to see if the Master is picking up the bundle, check the Master's logs.

```bash
2020-02-13 17:45:33.342+0000 [id=26] INFO c.c.j.c.i.c.ConfigurationBundleManager$Loader#getLoader: Resolving loader from /var/casc-bundle/bundle-link.yaml
2020-02-13 17:45:42.185+0000 [id=26] INFO c.c.j.c.i.CJPPluginManager$StartUp$1#apply: Core Configuration as Code is enabled
2020-02-13 17:45:42.186+0000 [id=26] INFO c.c.j.c.i.CJPPluginManager$StartUp$1#apply: Using JCasC config: /var/jenkins_home/core-casc-bundle/jenkins.yaml
```

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

URL to checkout: `https://github.com/joostvdg/jenkins-examples.git`

The bash command to execute.

```bash
cp casc/core-casc-security.xml ${JENKINS_HOME}/
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

