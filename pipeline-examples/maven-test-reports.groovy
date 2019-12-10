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
        stage('Build') {
            steps {
                container('maven') {
                    sh 'mvn compile --errors --show-version --strict-checksums'
                }
            }
        }
        stage('Test') {
            steps {
                container('maven') {
                    sh 'mvn test jacoco:report --errors --show-version --strict-checksums'
                }
            }
            post {
                success {
                    junit '**/surefire-reports/*.xml'
                    publishCoverage adapters: [jacocoAdapter('target/site/jacoco/jacoco.xml')], sourceFileResolver: sourceFiles('NEVER_STORE')
                    jacoco()
                }
            }
        }
        stage('Javadoc'){
            steps {
                container('maven') {
                    sh 'mvn javadoc:aggregate'
                }
            }
            post {
                success {
                    step([$class: 'JavadocArchiver', javadocDir: 'target/site/apidocs', keepAll: true])
                }
            }
        }
    }
}