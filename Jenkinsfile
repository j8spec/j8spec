pipeline {
  agent any
  stages {
    stage('test') {
      steps {
        sh './gradlew test'
        junit 'build/test-results/test/*.xml'
      }
    }
  }
}