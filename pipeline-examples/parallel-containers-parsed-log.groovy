pipeline {
    libraries {
        lib('joostvdg@master')
    }   
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
    image: maven:3-jdk-9-slim
    command:
    - cat
    tty: true
  - name: go
    image: caladreas/go-build-agent
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
                    sh 'mvn -version'
                    sh 'uname -a'
                }
                container('go') {
                    sh 'uname -a'
                    sh 'go version'
                }
            }
        }
        stage('Build Go and Maven') {
            parallel {
                stage('Maven Build') {
                    steps {
                        container('maven') {
                            dir('maven') {
                                sh 'pwd'
                                git 'https://github.com/joostvdg/spring-boot-2-reactive.git'
                                sh 'ls -lath'
                                sh 'mvn clean verify -e -B'
                            }
                        }
                    }
                }
                stage('Go Build') {
                    steps {
                        container('go') {
                            dir ('go') {
                                sh 'pwd'
                                git 'https://github.com/joostvdg/dui-go-client.git'
                                sh 'ls -lath'
                                sh 'go get -v github.com/gorilla/mux/...'
                                sh 'go test --cover ./...'
                                sh 'go build -v -tags netgo -o dui-go'
                            }
                        }
                    }
                }
            }
        }
    }
    post {
        always {
            logParseMavenStrict(true, false)
        }
    }
}