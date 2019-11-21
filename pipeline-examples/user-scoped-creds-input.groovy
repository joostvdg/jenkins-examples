// Declarative //
pipeline {
  agent any
  environment {
    TEST = ''
    CREDS = ''
  }
  stages {
    stage('Get Details') {
      input {
          message "Please enter the test details"
          ok "Looks good, proceed"
          parameters {
              string(name: 'Test', defaultValue: 'test', description: 'Please specify a test name')
              credentials(
                  credentialType: 'com.cloudbees.plugins.credentials.impl.UsernamePasswordCredentialsImpl', 
                  defaultValue: '', 
                  description: 'Give me your creds', 
                  name: 'Creds', 
                  required: true
              )
          }
      }
      steps {
          println "Test=${Test}"
          println "Creds=${Creds}"
          script {
              TEST  = "${Test}"
              CREDS = "${Creds}"
          }
      }
    }
    stage('Build') {
      steps {
        println "Hello!"
      }
    }
    stage('Deploy') {
      environment {
        DEPLOY_KEY = usernameColonPassword('Creds')
      }
      steps {
        sh 'echo "DEPLOY_KEY=${DEPLOY_KEY}"'
      }
    }
    stage('Deploy #2') {
      environment {
        CREDS = credentials('Creds')
      }
      steps {
        sh 'echo "CREDS=${CREDS}"'
      }
    }
  }
}
