pipeline {
    agent {
        kubernetes {
            label 'agent-with-resourcest'
            yaml '''
apiVersion: v1
kind: Pod
spec:
  containers:
  - name: "jnlp"
    resources:
      requests:
        cpu: 100m 
        memory: 200Mi 
      limits:
        cpu: 200m 
        memory: 400Mi     
  - name: maven
    image: maven:3-jdk-11-slim
    resources:
      requests:
        cpu: 100m 
        memory: 200Mi 
      limits:
        cpu: 200m 
        memory: 400Mi     
    command:
    - cat
    tty: true
    volumeMounts:
      - name: maven-cache
        mountPath: /root/.m2/repository
  volumes:
    - name: maven-cache
      hostPath:
        path: /tmp
        type: Directory
  securityContext:
    runAsUser: 1000
    fsGroup: 1000
'''
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
