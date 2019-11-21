node {
    // define the secrets and the env variables
    // engine version can be defined on secret, job, folder or global.
    // the default is engine version 2 unless otherwise specified globally.
    def secrets = [
        [path: 
            'secret/jx-write-check', 
            engineVersion: 2, 
            secretValues: [
                [envVar: 'testing', vaultKey: 'test']
            ]
        ]
    ]

    // optional configuration, if you do not provide this the next higher configuration
    // (e.g. folder or global) will be used
    def configuration = [
        vaultUrl: 'https://vault.dev.cjxd.kearos.net',
        vaultCredentialId: 'vaulttoken',
        engineVersion: 1
    ]

    // inside this block your credentials will be available as env variables
    withVault([configuration: configuration, vaultSecrets: secrets]) {
        sh 'echo $testing'
    }
}




node {
    withCredentials([[$class: 'VaultTokenCredentialBinding', credentialsId: 'vaulttoken', vaultAddr: 'https://vault.dev.cjxd.kearos.net/']]) {
        // values will be masked
        sh 'echo TOKEN=$VAULT_TOKEN'
        sh 'echo ADDR=$VAULT_ADDR'
    }
}