pipeline {
    agent none
    environment {
        CJOC_URL    = 'http://cjoc/cjoc'
        REGISTRY    = 'index.docker.io'
        REPO        = 'caladreas'
        IMAGE       = 'jnlp-test'
        TAG         = "0.1.${BUILD_NUMBER}"
    }
    stages {
        stage('Image Build') {
            parallel {
                stage('Kaniko') {
                    agent {
                        kubernetes {
                        //cloud 'kubernetes'
                        label 'kaniko-jre-test'
                        yaml """
spec:
  containers:
  - command:
    - "/busybox/cat"
    image: "gcr.io/kaniko-project/executor:debug"
    imagePullPolicy: "Always"
    name: "kaniko"
    tty: true
    volumeMounts:
    - mountPath: "/root"
      name: "jenkins-docker-cfg"
  volumes:
  - name: "jenkins-docker-cfg"
    projected:
      sources:
      - secret:
          items:
          - key: ".dockerconfigjson"
            path: ".docker/config.json"
          name: "docker-credentials"
"""
                        }
                    }
                    stages {
                        stage('Prepare Dockerfile') {
                            steps {
                                writeFile encoding: 'UTF-8', file: 'Dockerfile', text: "FROM jenkins/jnlp-slave"
                            }
                        }
                        stage('Build with Kaniko') {
                            environment { 
                                PATH = "/busybox:/kaniko:$PATH"
                            }
                            steps {
                                sh 'echo image fqn=${REGISTRY}/${REPO}/${IMAGE}:${TAG}'
                                container(name: 'kaniko', shell: '/busybox/sh') {
                                    // 1 - Kaniko only pushes to one tag at a time, does not create latest automatically
                                    // 2 - --cache=true leverages the destintation as cache repository
                                    // 3 - --cache-dir 
                                    // kaniko can leverage two destination targets
                                    sh '''#!/busybox/sh
                                    /kaniko/executor -f `pwd`/Dockerfile -c `pwd` --cleanup --cache=true --destination=${REGISTRY}/${REPO}/${IMAGE}:${TAG} --destination=${REGISTRY}/${REPO}/${IMAGE}:latest
                                    '''
                                }
                            }
                        }
                    }
                }
            }
        }
        stage('Image Test') {
            parallel {
                stage('Agent') {
                    agent {
                        kubernetes {
                            label 'agent-test'
                            containerTemplate {
                                name 'agent'
                                image "${REGISTRY}/${REPO}/${IMAGE}:${TAG}"
                                ttyEnabled true
                                command 'cat'
                            }
                        }
                    }
                    stages {
                        stage('Functional Tests') {
                            steps {
                                container('agent') {
                                    sh 'uname -a'
                                    sh 'env'
                                }
                            }
                        }
                        stage('Performance Tests') {
                            steps {
                                echo 'Hello'
                            }
                        }
                    }
                }
            }
        }
    }
}

