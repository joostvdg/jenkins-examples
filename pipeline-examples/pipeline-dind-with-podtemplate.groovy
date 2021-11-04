// It seems the Docker 20.10 images are different
// and the DIND setup below doesn't work with those

def agentPodYAML = """
spec:
  containers:
  - name: docker-client
    image: docker:19.03.15
    command: ['sleep', '99d']
    env:
      - name: DOCKER_HOST
        value: tcp://localhost:2375
  - name: docker-daemon
    image: docker:19.03.15-dind
    env:
      - name: DOCKER_TLS_CERTDIR
        value: ""
    securityContext:
      privileged: true
"""

pipeline {
    options {
        disableConcurrentBuilds()
    }
    agent {
        kubernetes {
            yaml agentPodYAML
        }
    }
    stages {
        stage('Write Mini Dockerfile') {
            steps {
                writeFile file: 'Dockerfile', text: 'FROM scratch'
            }
        }
        stage('Docker Build') {
            steps {
                container('docker-client') {
                    sh 'docker version && DOCKER_BUILDKIT=1 docker build --progress plain -t testing .'
                }
            }
        }
    }
}
