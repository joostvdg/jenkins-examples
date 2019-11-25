#!groovy

import org.jenkinsci.plugins.workflow.libs.GlobalLibraries
import org.jenkinsci.plugins.workflow.libs.LibraryConfiguration
import org.jenkinsci.plugins.workflow.libs.SCMSourceRetriever
import jenkins.plugins.git.GitSCMSource
import jenkins.model.*

println("=== Configuring the Global Shared Libraries == Start")


def configureLibrary(libConfigName, libConfigRemote) {
    def globalLibraries = GlobalLibraries.get()
    boolean exists = false
    for (LibraryConfiguration lib : globalLibraries.getLibraries()) {
        if (lib.getName().equals(libConfigName)) {
            exists = true
        }
    }

    if (exists) {
        println("= Global Library with name ${libConfigName} already exists")
    } else {
        globalLibraries = GlobalLibraries.get()
        if (globalLibraries.getLibraries().isEmpty()) {
            List<LibraryConfiguration> libraries = new ArrayList<>();
            globalLibraries.setLibraries(libraries)
        }
        def gitScmSource = new GitSCMSource(libConfigRemote)
        def scmSourceRetriever = new SCMSourceRetriever(gitScmSource)
        def libConfig = new LibraryConfiguration(libConfigName, scmSourceRetriever)
        libConfig.setDefaultVersion('master')
        println("= adding Global Library: ${libConfigName} - ${libConfigRemote}")
        globalLibraries.getLibraries().add(libConfig)
        globalLibraries.save()
    }
}

configureLibrary('core', 'https://github.com/joostvdg/jpl-core.git')
configureLibrary('gitops-k8s', 'https://github.com/joostvdg/jpl-gitops-kubernetes.git')
configureLibrary('maven', 'https://github.com/joostvdg/jpl-maven.git')

println("=== Configuring the Global Shared Libraries == End")
