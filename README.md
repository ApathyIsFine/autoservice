# Autoservice (ЛР №1–4, вариант 4)

Maven-проект «Автосервис» + Jenkins Pipeline (Jenkinsfile) + Git.

```
mvn clean | compile | test | package
java -jar target/autoservice-1.0-SNAPSHOT.jar
```

В Git хранятся только исходный код, `pom.xml`, `Jenkinsfile` и `.gitignore`;
каталог `target/` создаётся Maven при сборке и в репозиторий не попадает.

Jenkins: Pipeline script from SCM (Git), триггер Poll SCM.
Этапы: Build (`mvn compile`) → Test (`mvn test`) → Package (`mvn package -DskipTests`) → Inspect Target.
