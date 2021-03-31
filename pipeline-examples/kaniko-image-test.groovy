// https://github.com/GoogleContainerTools/kaniko/issues/835
pipeline {
    agent none
    environment {
        REPO        = 'caladreas'
        IMAGE       = 'jnlp-test'
        TAG         = "0.30.${BUILD_NUMBER}"
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
kind: Pod
metadata:
  name: kaniko
spec:
  containers:
  - name: kaniko
    image: gcr.io/kaniko-project/executor:debug
    imagePullPolicy: Always
    command:
    - /busybox/cat
    tty: true
    volumeMounts:
      - name: jenkins-docker-cfg
        mountPath: /kaniko/.docker
    env:
      - name: DOCKER_CONFIG
        value: /kaniko/.docker
  volumes:
  - name: jenkins-docker-cfg
    projected:
      sources:
      - secret:
          name: docker-credentials
          items:
            - key: .dockerconfigjson
              path: config.json
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
                            steps {
                                sh 'echo image fqn=${REPO}/${IMAGE}:${TAG}'
                                container(name: 'kaniko', shell: '/busybox/sh') {
                                    withEnv(['PATH+EXTRA=/busybox']) {
                                        sh '''#!/busybox/sh
                                        /kaniko/executor -f `pwd`/Dockerfile -c `pwd` --cleanup --cache=true --destination ${REPO}/${IMAGE}:${TAG} --destination ${REPO}/${IMAGE}:latest
                                        '''
                                    }
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
                                image "${REPO}/${IMAGE}:${TAG}"
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
