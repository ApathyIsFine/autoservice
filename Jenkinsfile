// Jenkinsfile — описание CI-конвейера (Jenkins Pipeline, Groovy DSL).
// Версия для Windows-агента (используется bat). Для Linux/macOS см. Jenkinsfile.linux (sh).
pipeline {

    // На каком агенте выполнять Pipeline: любой доступный
    agent any

    stages {

        // Этап 1. Компиляция основного кода
        stage('Build') {
            steps {
                bat 'mvn compile'
            }
        }

        // Этап 2. Запуск JUnit-тестов (при падении теста Pipeline остановится здесь)
        stage('Test') {
            steps {
                bat 'mvn test'
            }
        }

        // Этап 3. Упаковка в JAR (тесты уже были выполнены на этапе Test)
        stage('Package') {
            steps {
                bat 'mvn package -DskipTests'
            }
        }

        // Индивидуальное задание (вариант 4): показать результат сборки в каталоге target
        stage('Inspect Target') {
            steps {
                bat 'dir target'
            }
        }
    }
}
