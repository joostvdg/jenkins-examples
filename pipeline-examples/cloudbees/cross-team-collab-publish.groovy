pipeline {
    options {
        buildDiscarder logRotator(artifactDaysToKeepStr: '5', artifactNumToKeepStr: '5', daysToKeepStr: '5', numToKeepStr: '5')
        durabilityHint 'PERFORMANCE_OPTIMIZED'
        timeout(5)
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
        stage('Publish version') {
            steps {
                publishEvent jsonEvent('''{
                    "ArtifactEvent": {
                        "Artifacts": [{
                            "artifactId": "maven-demo-lib",
                            "groupId": "com.github.joostvdg.demo",
                            "version": "1.0.0"
                        }]
                    }
                }''')
            }
        }
    }
}
