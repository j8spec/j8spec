pipeline {
  agent any
  stages {
    stage('test') {
      steps {
        sh './gradlew clean test'
        junit 'build/test-results/test/*.xml'
      }
    }
  }
}