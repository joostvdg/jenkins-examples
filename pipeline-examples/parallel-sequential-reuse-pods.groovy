def jdk11Yaml = """
spec:
  containers:
  - name: mavenjdk11
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

def jdk13Yaml = """
spec:
  containers:
  - name: mavenjdk13
    image: maven:3-jdk-13
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

pipeline {
    agent none
    stages {
        stage('Build') {
            parallel {
                stage('Java 11') {
                    agent {
                        kubernetes {
                            idleMinutes 5
                            activeDeadlineSeconds 300
                            label "pl_mavenjdk11-${BUILD_NUMBER}"
                            yaml jdk11Yaml
                        }
                    }
                    steps {
                        container('mavenjdk11') {
                            sh 'mvn -v'
                        }
                    }
                }
                stage('Java 13') {
                    agent {
                        kubernetes {
                            idleMinutes 5
                            activeDeadlineSeconds 300
                            label "pl_mavenjdk13-${BUILD_NUMBER}"
                            yaml jdk13Yaml
                        }
                    }
                    steps {
                        git 'https://github.com/joostvdg/jx-maven-lib.git'
                        container('mavenjdk13') {
                            sh 'mvn -version'
                            sh 'mvn clean verify'
                        }
                    }
                }
            }
        }
        stage('Test') {
            parallel {
                stage('Java 11') {
                    agent {
                        kubernetes {
                            idleMinutes 5
                            activeDeadlineSeconds 300
                            label "pl_mavenjdk11-${BUILD_NUMBER}"
                            yaml jdk11Yaml
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
                stage('Java 13') {
                    agent {
                        kubernetes {
                            idleMinutes 5
                            activeDeadlineSeconds 300
                            label "pl_mavenjdk13-${BUILD_NUMBER}"
                            yaml jdk13Yaml
                        }
                    } 
                    stages {
                        stage('Functional Tests') {
                            steps {
                                container('mavenjdk13') {
                                    echo 'Hello'
                                }
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
        stage('Deploy') {
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





