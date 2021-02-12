String getTriggerCauseEvent() {
    // we have to verify if there actually was event data
    // we can have more checks to be sure, but we shouldn't trigger on every event with cause EventTriggerCause
    // so we should be safe with these two checks
    def buildCauseInfo = currentBuild.getBuildCauses("com.cloudbees.jenkins.plugins.pipeline.events.EventTriggerCause")
    if (buildCauseInfo && buildCauseInfo[0])  {      
        def artifactId = buildCauseInfo[0].event.ArtifactEvent.Artifacts[0].artifactId
        return artifactId
    }
    return "N/A"
}


pipeline {
    agent {
        kubernetes {
            yaml """
kind: Pod
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
    triggers {
        eventTrigger jmespathQuery('ArtifactEvent.Artifacts[?artifactId == \'maven-demo-lib\'] && ArtifactEvent.Artifacts[?groupId == \'com.github.joostvdg.demo\']')
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
        stage('OnlyIfEvent1') {
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
        stage('if maven-demo-lib2') {
            when { 
                allOf { 
                    // due to possible npe, we have to outsource this to a function
                    equals expected: 'maven-demo-lib', actual: getTriggerCauseEvent();
                    triggeredBy 'EventTriggerCause'
                }
            }
            steps {
                echo 'It is maven-demo-lib'
            }
        }
        stage('if maven-demo-app') {
            when { 
                allOf { 
                    // due to possible npe, we have to outsource this to a function
                    equals expected: 'maven-demo-app', actual: getTriggerCauseEvent();
                    triggeredBy 'EventTriggerCause'
                }
            }
            steps {
                echo 'It is maven-demo-app'
            }
        }
    }
}
