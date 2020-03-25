pipeline {
    options {
        buildDiscarder logRotator(artifactDaysToKeepStr: '5', artifactNumToKeepStr: '5', daysToKeepStr: '5', numToKeepStr: '5')
        durabilityHint 'PERFORMANCE_OPTIMIZED'
        timeout(5)
    }
    triggers {
        eventTrigger jmespathQuery('ArtifactEvent.Artifacts[?artifactId == \'maven-demo-lib\'] && ArtifactEvent.Artifacts[?groupId == \'com.github.joostvdg.demo\']')
    }
    agent {
        kubernetes {
            label 'mypod'
            defaultContainer 'jnlp'
            yaml """
apiVersion: v1
kind: Pod
metadata:
  labels:
    some-label: some-label-value
spec:
  containers:
  - name: maven
    image: maven:3-jdk-8
    command:
    - cat
    tty: true
"""
        }
    }
    stages {
        stage('Test versions') {
            steps {
                container('maven') {
                    sh 'uname -a'
                    sh 'mvn -version'
                }
                script {
                    def cause = currentBuild.getBuildCauses()
                    echo "cause=${cause}"
                }
            }
        }
        stage('OnlyIfEvent') {
            when { triggeredBy 'EventTriggerCause' }
            steps {
                echo "We're trigged by an event!"
                script {
                    def eventCause = currentBuild.getBuildCauses("com.cloudbees.jenkins.plugins.pipeline.events.EventTriggerCause")
                    echo "eventCause=${eventCause}"
                    def version = eventCause[0].event.ArtifactEvent.Artifacts[0].version
                    echo "version=${version}"
                }
            }
        }
    }
}
