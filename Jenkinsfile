pipeline {
    agent any
    tools {
        gradle 'Gradle 6'
        jdk 'Java 8'
    }
    stages {
        stage ('Build') {
            steps {
                sh './gradle clean assembleRelease'
            }
            post {
                success {
                    archiveArtifacts artifacts: 'app/build/outputs/apk/**/*.apk', fingerprint: true
                }
            }
        }
    }

    post {
        always {
            deleteDir()
        }
    }
}
