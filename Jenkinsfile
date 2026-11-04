// Jenkinsfile — описание CI-конвейера (Jenkins Pipeline, Groovy DSL).
// Версия для Windows-агента (используется bat). Для Linux/macOS см. Jenkinsfile.linux (sh).
// Перед этапами Jenkins автоматически выполняет Checkout SCM (Pipeline script from SCM).
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

        // Этап 2 (ЛР №5). Статический анализ: Checkstyle по правилам из checkstyle.xml.
        // При нарушении правил команда завершается ошибкой, Test и Package не выполняются.
        stage('Static Analysis') {
            steps {
                bat 'mvn checkstyle:check'
            }
        }

        // Этап 3. Запуск JUnit-тестов (при падении теста Pipeline остановится здесь)
        stage('Test') {
            steps {
                bat 'mvn test'
            }
        }

        // Этап 4. Упаковка в JAR (тесты уже были выполнены на этапе Test)
        stage('Package') {
            steps {
                bat 'mvn package -DskipTests'
            }
        }
    }
}
