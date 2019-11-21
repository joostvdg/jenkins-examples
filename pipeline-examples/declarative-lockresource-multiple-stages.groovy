pipeline {
    agent none
    stages {
        stage('Some Stage') {
            steps {
                println 'Hello'
            }
        }
        stage('Fluffy Test') {
            parallel {
                stage('PR') {
                    options {
                        lock(quantity: 1, resource: 'abc')
                    }
                    agent {
                        kubernetes {
                            label 'pl_mavenjdk8_test'
                            containerTemplate {
                                name 'maven'
                                image 'maven:3.3.9-jdk-8-alpine'
                                ttyEnabled true
                                command 'cat'
                            }
                        }
                    }
                    stages {
                        stage('Functional Tests') {
                            steps {
                                echo 'Hello'
                            }
                        }
                        stage('Performance Tests') {
                            steps {
                                echo 'Hello'
                            }
                        }
                    }
                }
                stage('Master') {
                    when {
                        branch 'master'
                        beforeAgent true
                    }
                    agent {
                        kubernetes {
                            label 'pl_mavenjdk11_test'
                            containerTemplate {
                                name 'mavenjdk11'
                                image 'maven:3-jdk-11-slim'
                                ttyEnabled true
                                command 'cat'
                            }
                        }
                    }
                    stages {
                        stage('Functional Tests') {
                            steps {
                                echo 'Hello'
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
        stage('Some Other Stage') {
            steps {
                println 'Hello'
            }
        }
    }
}


