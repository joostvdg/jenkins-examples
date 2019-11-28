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
        stage('Run maven') {
            steps {
                git 'https://github.com/joostvdg/jx-maven-lib.git'
                container('maven') {
                    sh 'mvn -version'
                    sh 'mvn clean verify'
                }
            }
        }
    }
}