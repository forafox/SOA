plugins {
    java
    id("org.springframework.boot") version "3.5.5"
    id("io.spring.dependency-management") version "1.1.7"
    war
}

group = "com.jellyone"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.7.0")

    compileOnly("jakarta.servlet:jakarta.servlet-api:6.0.0")

    annotationProcessor("org.projectlombok:lombok")
    compileOnly("org.projectlombok:lombok:1.18.28")


    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("io.rest-assured:rest-assured:5.4.0")
}


tasks.test {
    useJUnitPlatform()
}

tasks.bootJar {
    enabled = false
}

tasks.war {
    archiveFileName.set("backend-oscars-0.0.1-SNAPSHOT.war")
}

// Задача для сборки фронтенда и интеграции с Spring Boot
tasks.register<Exec>("buildFrontend") {
    group = "build"
    description = "Собирает фронтенд и интегрирует его с Spring Boot"
    
    workingDir = file("../frontend")
    commandLine("npm", "run", "build")
    
    doLast {
        // Копируем собранный фронтенд в static директорию
        copy {
            from("../frontend/out")
            into("src/main/resources/static")
        }
        println("✅ Фронтенд успешно интегрирован с Spring Boot!")
    }
}

// Задача для полной сборки (фронтенд + бэкенд)
tasks.register("buildFull") {
    group = "build"
    description = "Полная сборка проекта (фронтенд + бэкенд)"
    dependsOn("buildFrontend", "war")
}

