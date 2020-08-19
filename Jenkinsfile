pipeline {
    agent any
    tools {
        gradle 'Gradle 6'
        jdk 'Java 8'
    }
    stages {
        stage ('Build') {
            steps {
                sh 'gradle clean assembleRelease'
            }
        }
        stage ('Sign') {
            steps {
                signAndroidApks (
                    apksToSign: '**/*-unsigned.apk',
                    keyStoreId: 'AndroidKeys',
                    keyAlias: 'key0',
                    archiveSignedApks: true,
                    archiveUnsignedApks: true
                )
            }
        }        
    }

    post {
        always {
            deleteDir()
        }
    }
}
