pipeline {
  agent any
  stages {
    stage('package') {
      steps {
        sh './gradlew test'
      }
    }
  }
}