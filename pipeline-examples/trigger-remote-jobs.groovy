pipeline {
    agent none
    stages{
        stage('Call Remote Unprotected') {
            steps {
                triggerRemoteJob mode: 
                    [$class: 'TrackProgressAwaitResult', 
                        scheduledTimeout: [timeoutStr: ''], 
                        startedTimeout: [timeoutStr: ''], 
                        timeout: [timeoutStr: '1d'], 
                        whenFailure: [$class: 'StopAsFailure'], 
                        whenScheduledTimeout: [$class: 'ContinueAsIs'], 
                        whenStartedTimeout: [$class: 'ContinueAsIs'], 
                        whenTimeout: [$class: 'ContinueAsFailure'], 
                        whenUnstable: [$class: 'ContinueAsUnstable']
                    ], remotePathUrl: 'jenkins://f30973861f2d542a36e918399f0c0e4c/unprotected/unprotected'
            }
        }
        stage('Call Remote Protected') {
            steps {
                triggerRemoteJob mode: [$class: 'TrackProgressAwaitResult', 
                    scheduledTimeout: [timeoutStr: '30s'], 
                    startedTimeout: [timeoutStr: '2m'], 
                    timeout: [timeoutStr: '5m'], 
                    whenFailure: [$class: 'StopAsFailure'], 
                    whenScheduledTimeout: [$class: 'ContinueAsIs'], 
                    whenStartedTimeout: [$class: 'ContinueAsIs'], 
                    whenTimeout: [$class: 'ContinueAsFailure'], 
                    whenUnstable: [$class: 'ContinueAsUnstable']
                ], remotePathUrl: 'jenkins://f30973861f2d542a36e918399f0c0e4c/protected/protected-job'
            }
        }
        stage('Call Remote Untrusted') {
            steps {
                triggerRemoteJob mode: [$class: 'TrackProgressAwaitResult', 
                    scheduledTimeout: [timeoutStr: '30s'], 
                    startedTimeout: [timeoutStr: '2m'], 
                    timeout: [timeoutStr: '5m'], 
                    whenFailure: [$class: 'StopAsFailure'], 
                    whenScheduledTimeout: [$class: 'ContinueAsIs'], 
                    whenStartedTimeout: [$class: 'ContinueAsIs'], 
                    whenTimeout: [$class: 'ContinueAsFailure'], 
                    whenUnstable: [$class: 'ContinueAsUnstable']
                ],  remotePathUrl: 'jenkins://40d968760dd2feb8110782243736fe5c/pipeline-test-1'
            }
        }
    }
}