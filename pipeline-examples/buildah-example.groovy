pipeline {
    agent {
        kubernetes {
            cloud 'local'
            yaml """
apiVersion: v1
kind: Pod
metadata:
  labels:
    some-label: some-label-value
spec:
  containers:
  - name: buildah
    image: buildah/buildah
    command:
    - cat
    tty: true
    securityContext:
      privileged: true
"""
        }
    }
    stages {
        stage('Test Buildah') {
            environment {
                DOCKER_CREDS = credentials('dockerhub')
                DOCKER_TAG   = 'docker.io/caladreas/buildah-test:0.0.2'
            }
            steps {
                println 'hello'
                container('buildah') {
                    sh 'export BUILDAH_FORMAT=docker'
                    writeFile encoding: 'UTF-8', file: 'registries.conf', text: '''# This is a system-wide configuration file used to
# keep track of registries for various container backends.
# It adheres to TOML format and does not support recursive
# lists of registries.

# The default location for this configuration file is /etc/containers/registries.conf.

# The only valid categories are: \'registries.search\', \'registries.insecure\',
# and \'registries.block\'.

[registries.search]
registries = [\'docker.io\']

# If you need to access insecure registries, add the registry\'s fully-qualified name.
# An insecure registry is one that does not have a valid SSL certificate or only does HTTP.
[registries.insecure]
registries = []


# If you need to block pull access from a registry, uncomment the section below
# and add the registries fully-qualified name.
#
# Docker only
[registries.block]
registries = []
'''
                    sh 'cp registries.conf /etc/containers/registries.conf'
                    writeFile file: 'Dockerfile', text: '''FROM docker.io/alpine
RUN ls
RUN apk add --no-cache tini
# Tini is now available at /sbin/tini
ENTRYPOINT ["/sbin/tini", "--"]'''
                    sh 'buildah build-using-dockerfile --format docker --file Dockerfile --creds ${DOCKER_CREDS} --tag ${DOCKER_TAG} --debug .'
                    sh 'buildah push --creds ${DOCKER_CREDS}  docker://${DOCKER_TAG}'
                }
            }
        }
    }
}