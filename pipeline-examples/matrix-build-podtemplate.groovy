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
                stages {
                    stage("Get Agent") {
                        when {
                            allOf {
                                not {
                                    allOf {
                                        environment name: 'JDK_VERSION' , value: '13'
                                        environment name: 'JDK_TYPE'    , value: 'amazoncorretto'
                                    } 
                                }
                                not {
                                    allOf {
                                        environment name: 'JDK_VERSION' , value: '11'
                                        environment name: 'JDK_TYPE'    , value: 'ibmjava'
                                    } 
                                }
                                not {
                                    allOf {
                                        environment name: 'JDK_VERSION' , value: '13'
                                        environment name: 'JDK_TYPE'    , value: 'ibmjava'
                                    } 
                                }
                            }
                            beforeAgent true
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
                            stage("Build") {
                                steps {
                                    sh 'uname -a'
                                    git 'https://github.com/joostvdg/jx-maven-lib.git'
                                    container('maven') {
                                        sh 'mvn -version'
                                        sh 'mvn clean verify'
                                    }
                                }
                            }
                            stage("Compat") {
                                when {
                                    environment name: "JDK_VERSION", value: "7"
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