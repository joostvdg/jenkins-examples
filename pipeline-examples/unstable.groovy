pipeline {
    agent {
        kubernetes {
        label 'mypod'
        yaml """
apiVersion: v1
kind: Pod
spec:
  containers:
  - name: maven
    image: maven:3-jdk-11-slim
    command: ['cat']
    tty: true
    volumeMounts:
      - name: maven-cache
        mountPath: /root/.m2/repository
  volumes:
    - name: maven-cache
      hostPath:
        path: /tmp
        type: Directory
"""
        }
    }
    stages {
        stage('SCM') {
            steps {
                git 'https://github.com/joostvdg/jx-maven-lib.git'
            }
        }
        stage('Build & Test') {
            steps {
                container('maven') {
                    sh 'mvn clean verify --show-version -e'
                }
            }
        }
        stage('Publish') {
            steps {
                warnError('Failed Maven Publish') {
                    container('maven'){
                        sh 'mvn publish'
                    }
                }
            }
        }
    }
}