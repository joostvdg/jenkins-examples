@Library('joostvdg') _

try {
    parallel linux: {
        parallel linux32: {
            node('linux') {
                echo 'linux32'
                echo 'warning ... asdasd asd asdidjfpoisujf'
                sleep 30
            }
        }, linux64: {
            node('linux') {
                echo 'linux64'
                sleep 25
            }
        },
        failFast: false
    }, windows: {
        parallel windows32: {
            node('master') {
                echo 'master'
                sleep 35
            }
        }, windows64: {
            node('master') {
                echo 'master'
                sleep 20
            }
        },
        failFast: false
    },
    failFast: false
} finally {
    node {
        logParseMavenStrict(true, false)
    }
}
