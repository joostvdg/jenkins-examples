// https://github.com/jenkinsci/pipeline-model-definition-plugin/blob/master/pipeline-model-definition/src/test/resources/matrix/matrixPipelineTwoAxis.groovy
pipeline {
    agent none
    stages {
        stage("Test") {
            matrix {
                axes {
                    axis {
                        name 'JDK_VERSION'
                        values '8','11', '13'
                    }
                    axis {
                        name 'JDK_TYPE'
                        values 'ibmjava','amazoncorretto', 'jdk'
                    }
                }
                excludes {
                    exclude {
                        axis {
                            name 'JDK_VERSION'
                            values '13'
                        }
                        axis {
                            name 'JDK_TYPE'
                            notValues 'jdk' // double negative, make sure we only do 13 with jdk
                        }
                    }
                    exclude {
                        axis {
                            name 'JDK_VERSION'
                            values '11'
                        }
                        axis {
                            name 'JDK_TYPE'
                            values "ibmjava"
                        }
                    }
                }
                agent {
                    kubernetes {
                        label "maven-${JDK_TYPE}-${JDK_VERSION}-test"
                        containerTemplate {
                            name 'maven'
                            image "maven:3-${JDK_TYPE}-${JDK_VERSION}"
                            ttyEnabled true
                            command 'cat'
                        }
                    }
                }
                stages {
                    stage("${JDK_TYPE}-${JDK_VERSION}") {
                        stages {
                            stage('Test Image') {
                                steps {
                                    println "Using Image: maven:3-${JDK_TYPE}-${JDK_VERSION}"
                                }
                            }
                            stage("Build") {
                                steps {
                                    sh 'uname -a'
                                    git 'https://github.com/joostvdg/jx-maven-lib.git'
                                    container('maven') {
                                        sh 'mvn clean verify --show-version --strict-checksums -e'
                                    }
                                }
                            }
                            stage("Deploy") {
                                when {
                                    branch 'master'
                                }
                                steps {
                                    echo "WE SHOULD NEVER GET HERE"
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}