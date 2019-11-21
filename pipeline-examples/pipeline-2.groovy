pipeline {
    options {
        buildDiscarder logRotator(artifactDaysToKeepStr: '5', artifactNumToKeepStr: '5', daysToKeepStr: '5', numToKeepStr: '5')
        durabilityHint 'PERFORMANCE_OPTIMIZED'
        timeout(5)
    }
    libraries {
        lib('joostvdg@master')
    }
    agent { label 'maven-jdk-8'}

    stages {
        stage('Test Something') {
            steps {
                script {
                    def url = 'http://pse-joostvdg-controller-725893236.eu-west-1.elb.amazonaws.com.elb.cloudbees.net/teams-flag'
                    def responseCode = sh (returnStdout: true, script: "curl -sL -w \"%{http_code}\" ${url} -o /dev/null --max-time 15").trim()
                    echo "[INFO] reponse ${responseCode}"
                    try {
                        url = 'http://sonar-sonarqube:9000/api/system/health'
                        responseCode = sh (returnStdout: true, script: "curl -sL -w \"%{http_code}\" ${url} -o /dev/null --max-time 15").trim()
                    } catch(err) {
                        echo "[WARN] caught ${err}"
                        responseCode = '520' // CloudFlare's 520 - Unknown Error: https://en.wikipedia.org/wiki/List_of_HTTP_status_codes
                    } finally {
                        echo "[INFO] reponse ${responseCode}"
                    }

                }
            }
        }
        stage('Test - Success') {
            when {
                expression { 
                    '403' == toolHealthCheck('http://pse-joostvdg-controller-725893236.eu-west-1.elb.amazonaws.com.elb.cloudbees.net/teams-flag') && 
                    '520' == toolHealthCheck('http://sonar-sonarqube:9000/api/system/health')
                }
            }
            steps {
                echo 'Hello'
            }
        }
        stage('Test - Faillure') {
            when {
                expression { '200' ==  toolHealthCheck('http://sonar-sonarqube:9000/api/system/health') }
            }
            steps {
                echo 'Hello'
            }
        }
    }
}