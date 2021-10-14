pipeline {
    agent none
    stages {
        stage('Images') {
            parallel {
                stage('Building Image 1') {
                    agent {
                        kubernetes {
                            label 'pl_declarative_full_example_build_image_1'
                            containerTemplate {
                                name 'maven'
                                image 'maven:3.3.9-jdk-8-alpine'
                                ttyEnabled true
                                command 'cat'
                            }
                        }
                    }
                    steps {
                        echo 'building image'
                        sh 'env'
                        sh 'sleep 60'
                    }
                }
                stage('Building Image 2 ') {
                    agent {
                        kubernetes {
                            label 'pl_declarative_full_example_build_image_2'
                            containerTemplate {
                                name 'maven'
                                image 'nginx:1.15.1'
                                ttyEnabled true
                                command 'cat'
                            }
                        }
                    }
                    steps {
                        echo 'building image 2'
                        sh 'sleep 50'
                        echo 'another message'
                    }
                }
            }
        }
        stage('Compilation') {
            parallel {
                stage('Compiling 1') {
                    steps {
                        echo 'compiling 1'
                    }
                }
                stage('Compiling 2') {
                    steps {
                        echo 'Compiling 2'
                    }
                }
            }
        }
        stage('Package') {
            parallel {
                stage('Package 1') {
                    steps {
                        echo 'Package 1'
                    }
                }
                stage('Package 2') {
                    steps {
                        echo 'Package 2'
                    }
                }
                stage('Package 3') {
                    steps {
                        echo 'Package 3'
                    }
                }
            }
        }
        stage('Forth Stage') {
            steps {
                echo 'step1'
                echo 'step2'
            }
        }
    }
}
