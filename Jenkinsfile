pipeline {
    agent any
    tools {
        gradle 'Gradle 6'
        jdk 'Java 8'
    }
    stages {
        stage ('Build') {
            steps {
                sh 'gradle clean assembleRelease --refresh-dependencies'
            }
        }
        stage ('Sign') {
            steps {
                signAndroidApks (
                    apksToSign: '**/*-unsigned.apk',
                    keyStoreId: 'AndroidKeys',
                    keyAlias: 'key0',
                    archiveSignedApks: true
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
