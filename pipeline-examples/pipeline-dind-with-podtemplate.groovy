pipeline {
    options {
        disableConcurrentBuilds()
    }
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
  - name: docker-cmds 
    image: docker:1.12.6 
    command: ['docker', 'run', '-p', '80:80', 'httpd:latest'] 
    env: 
    - name: DOCKER_HOST 
      value: tcp://localhost:2375 
  - name: dind-daemon 
    image: docker:1.12.6-dind 
    securityContext: 
      privileged: true 
    volumeMounts: 
      - name: docker-graph-storage 
        mountPath: /var/lib/docker 
  volumes: 
    - name: docker-graph-storage 
      emptyDir: {}
"""
        }
    }
    stages {
        stage('Run maven') {
            steps {
                git 'https://github.com/joostvdg/jx-maven-lib.git'
                container('docker-cmds') {
                    sh "docker run -v ${WORKSPACE}:/usr/src/mymaven -w /usr/src/mymaven maven:3-jdk-11-slim mvn clean verify"
                }
            }
        }
    }
}

