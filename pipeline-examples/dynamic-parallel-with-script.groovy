pipeline {
    agent any
    stages {
        stage('Running tests') {
            steps {
                sh 'touch TEST-1.xml TEST-2.xml TEST-3.xml TEST-3.xml TEST-5.xml TEST-6.xml'
                script {
                    def tests = [:]
                    for (f in findFiles(glob: '**/TEST-*.xml')) {
                        // Create temp variable, otherwise the name will be the last value of the for loop
                        def name = f
                        tests["${name}"] = {
                            sh 'echo ${name}'
                            sh 'sleep 30'
                        }
                    }
                    parallel tests
                }
            }
        }       
    }
}