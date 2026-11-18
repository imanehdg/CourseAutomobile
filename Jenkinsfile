pipeline {
    agent any

    triggers {
        pollSCM('H/5 * * * *')
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        stage('Build') {
            steps {
                sh 'mkdir -p out'
                sh 'javac -d out src/*.java test/TestsCourse.java'
            }
        }
        stage('Test') {
            steps {
                sh 'java -cp out TestsCourse'
            }
        }
    }

    post {
        success { echo 'Pipeline reussi' }
        failure { echo 'Pipeline en echec' }
    }
}