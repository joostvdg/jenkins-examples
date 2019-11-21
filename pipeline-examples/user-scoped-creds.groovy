// Declarative //
pipeline {
  agent any
  // build parameters can insert user credentials for the entire pipeline
  parameters {
    credentials(credentialType: 'com.cloudbees.plugins.credentials.impl.UsernamePasswordCredentialsImpl',
                defaultValue: '',
                description: 'Production deployment key',
                // the credentials name used here must match the parameter passed to userColonPassword in the 'Deploy' stage below
                name: 'deployKey',
                required: true)
  }
  stages {
    stage('Build') {
      steps {
        println "Hello!"
      }
    }
    stage('Deploy') {
      environment {
        // the usernameColorPassword must be passed the credential name in the Pipeline parameters above
        DEPLOY_KEY = usernameColonPassword('deployKey')
      }
      steps {
        sh 'echo "DEPLOY_KEY=${DEPLOY_KEY}"'
      }
    }
  }
}
// Script //
// build parameters can insert user credentials for the entire pipeline
properties([
  parameters([
    credentials(credentialType: 'com.cloudbees.plugins.credentials.impl.UsernamePasswordCredentialsImpl',
                defaultValue: 'barbossa-test',
                description: 'Production deployment key',
                // the credentials name used here must match the credentialsId referenced in the 'Deploy' stage below
                name: 'deployKey',
                required: true)
  ])
])

node {
  stage('Build') {
    println "Hello!"
  }
  stage('Deploy') {
    withCredentials([
      // the credentialsId must match the credential name in the Pipeline properties parameters above
      usernameColonPassword(credentialsId: 'deployKey', variable: 'DEPLOY_KEY')
    ]) {
      sh 'echo "DEPLOY_KEY=${DEPLOY_KEY}"'
    }
  }
}