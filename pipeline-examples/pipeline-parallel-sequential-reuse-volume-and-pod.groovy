pipeline {
    agent none
    stages {
        stage('Fluffy Build') {
            parallel {
                stage('Build Java 8') {
                    agent {
                        kubernetes {
                            label 'pl_mavenjdk8_build'
                            containerTemplate {
                                name 'maven'
                                image 'maven:3.3.9-jdk-8-alpine'
                                ttyEnabled true
                                command 'cat'
                                activeDeadlineSeconds 300
                                idleMinutes 5
                                inheritFrom 'default-java'
                                instanceCap 1
                                podRetention never()
                            }
                        }
                    }
                    steps {
                        container('maven') {
                            sh 'mvn -v'
                        }
                    }
                }
                stage('Build Java 11') {
                    agent {
                        kubernetes {
                            idleMinutes 5
                            activeDeadlineSeconds 300
                            label 'pl_mavenjdk11_build'
                            yaml """
spec:
  containers:
  - name: mavenjdk11
    image: maven:3-jdk-11-slim
    command: ['cat']
    tty: true
    volumeMounts:
      - name: build-cache
        mountPath: /tmp/cache
  volumes:
    - name: build-cache
      persistentVolumeClaim:
        claimName: azure-managed-disk
"""
                        }
                    }
                    steps {
                        git 'https://github.com/joostvdg/jx-maven-lib.git'
                        container('mavenjdk11') {
                            sh 'mvn -version'
                            sh 'mvn clean verify'
                            sh 'cp -R target/ /tmp/cache/'
                            sh 'ls -lath /tmp/cache/'
                        }
                    }
                }
            }
        }
        stage('Fluffy Test') {
            parallel {
                stage('JDK 8') {
                    agent {
                        kubernetes {
                            label 'pl_mavenjdk8_test'
                            containerTemplate {
                                name 'maven'
                                image 'maven:3.3.9-jdk-8-alpine'
                                ttyEnabled true
                                command 'cat'
                                activeDeadlineSeconds 300
                                idleMinutes 5
                                inheritFrom 'default-java'
                                instanceCap 1
                                podRetention never()
                            }
                        }
                    }
                    stages {
                        stage('Functional Tests') {
                            steps {
                                echo 'Hello'
                            }
                        }
                        stage('API Contract Tests') {
                            steps {
                                echo 'Hello'
                            }
                        }
                        stage('Performance Tests') {
                            when {
                                branch 'master'
                            }
                            steps {
                                echo 'Hello'
                            }
                        }
                    }
                }
                stage('JDK 11') {
                    agent {
                        kubernetes {
                            idleMinutes 5
                            activeDeadlineSeconds 300
                            label 'pl_mavenjdk11_build'
                            yaml """
spec:
  containers:
  - name: mavenjdk11
    image: maven:3-jdk-11-slim
    command: ['cat']
    tty: true
    volumeMounts:
      - name: build-cache
        mountPath: /tmp/cache
  volumes:
    - name: build-cache
      persistentVolumeClaim:
        claimName: azure-managed-disk
"""
                        }
                    }
                    stages {
                        stage('Functional Tests') {
                            steps {
                                container('mavenjdk11') {
                                    sh 'ls -lath /tmp/cache/target'
                                    sh 'cp -R /tmp/cache/target .'
                                    echo 'Hello'
                                }
                                sh 'ls -lath'
                                sh 'ls -lath target/'
                            }
                        }
                        stage('API Contract Tests') {
                            steps {
                                echo 'Hello'
                            }
                        }
                        stage('Performance Tests') {
                            when {
                                branch 'master'
                            }
                            steps {
                                echo 'Hello'
                            }
                        }
                    }
                }
            }
        }
        stage('Fluffy Deploy') {
            agent {
                kubernetes {
                    label 'pl_declarative_deployment'
                    containerTemplate {
                        name 'pl_deployment'
                        image 'cloudbees/docker-java-with-docker-client'
                        ttyEnabled true
                        command 'cat'
                    }
                }
            }
            when {
                branch 'master'
                beforeAgent true
            }
            steps {
                echo "hello"
            }
        }
    }
}





