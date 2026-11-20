// Jenkinsfile — CI/CD-конвейер проекта «Автосервис» (Jenkins Pipeline, Groovy DSL).
// Версия для Windows-агента (bat). Для Linux/macOS см. Jenkinsfile.linux (sh).
// Соответствует руководству Jenkins «Build a Java app with Maven» (Build -> Test -> Deliver)
// и дополнен этапами Checkout SCM, Static Analysis (Checkstyle) и Package.
pipeline {

    // Выполнять Pipeline на любом доступном агенте
    agent any

    options {
        // Если тесты завершились нестабильно (UNSTABLE), следующие этапы не выполняются
        skipStagesAfterUnstable()
    }

    stages {

        // Этап 1. Компиляция основного кода (Checkout SCM Jenkins выполняет автоматически)
        stage('Build') {
            steps {
                bat 'mvn compile'
            }
        }

        // Этап 2. Статический анализ: Checkstyle по правилам из checkstyle.xml
        stage('Static Analysis') {
            steps {
                bat 'mvn checkstyle:check'
            }
        }

        // Этап 3. Автоматическое тестирование: JUnit-тесты
        stage('Test') {
            steps {
                bat 'mvn test'
            }
            post {
                always {
                    // Публикация JUnit-отчётов в интерфейсе Jenkins (вкладка Test Result);
                    // выполняется всегда, даже если тесты упали
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }

        // Этап 4. Упаковка в JAR (тесты уже выполнены на этапе Test)
        stage('Package') {
            steps {
                bat 'mvn package -DskipTests'
            }
        }

        // Этап 5 (CD). Доставка: JAR устанавливается в локальный Maven-репозиторий
        // и сохраняется как артефакт сборки Jenkins (раздел Build Artifacts)
        stage('Deliver') {
            steps {
                bat 'mvn -B install -DskipTests'
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }
    }
}
