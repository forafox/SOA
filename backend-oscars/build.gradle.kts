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
    implementation("org.springframework.boot:spring-boot-starter-web") {
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging")
        exclude(group = "org.apache.tomcat.embed", module = "tomcat-embed-websocket")
    }
    implementation("org.springframework.boot:spring-boot-starter-webflux") {
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging")
    }
    implementation("org.springframework.boot:spring-boot-starter-aop") {
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging")
    }
    implementation("org.springframework.boot:spring-boot-starter-actuator") {
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging")
    }

    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.7.0")
    implementation("com.fasterxml:classmate:1.5.1")
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

tasks.bootWar {
    archiveFileName.set("backend-oscars-0.0.1-SNAPSHOT.war")
}

// Frontend build task
tasks.register("buildFrontend") {
    group = "build"
    description = "Build frontend and copy static files"
    
    doLast {
        val frontendDir = file("../frontend")
        val staticDir = file("src/main/resources/static")
        
        if (frontendDir.exists()) {
            println("🚀 Building frontend...")
            
            // Check if node_modules exists, install if not
            if (!file("$frontendDir/node_modules").exists()) {
                println("📥 Installing frontend dependencies...")
                exec {
                    workingDir = frontendDir
                    commandLine("npm", "install")
                }
            }
            
            // Build frontend
            exec {
                workingDir = frontendDir
                commandLine("npm", "run", "build")
                environment("NEXT_PUBLIC_EMBEDDED_MODE", "true")
                environment("NODE_ENV", "production")
            }
            
            // Clean and create static directory
            delete(staticDir)
            staticDir.mkdirs()
            
            // Copy built files
            val frontendOut = file("$frontendDir/out")
            if (frontendOut.exists()) {
                copy {
                    from(frontendOut)
                    into(staticDir)
                }
                println("✅ Frontend files copied to static resources")
            } else {
                throw GradleException("Frontend build output not found at ${frontendOut.absolutePath}")
            }
        } else {
            println("⚠️ Frontend directory not found, skipping frontend build")
        }
    }
}

// Make bootWar depend on frontend build
tasks.bootWar {
    dependsOn("buildFrontend")
}

tasks.war {
    dependsOn("buildFrontend")
    archiveFileName.set("backend-oscars-0.0.1-SNAPSHOT.war")
}
