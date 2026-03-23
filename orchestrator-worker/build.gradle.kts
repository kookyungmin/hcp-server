plugins {
  id("java")
  id("org.springframework.boot") version "3.5.8"
  id("io.spring.dependency-management") version "1.1.7"
}

group = "net.happykoo.hcp"
version = "0.0.1"
description = "Happy Cloud Platform Orchestrator Worker"

java {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(21))
  }
}

repositories {
  mavenCentral()
}

dependencies {
  implementation(project(":common-web"))
  implementation(project(":common-util"))

  //Spring framework
  implementation("org.springframework.boot:spring-boot-starter-web")
  implementation("org.springframework.boot:spring-boot-starter-websocket")
  implementation("org.springframework.boot:spring-boot-starter-actuator")
  implementation("org.springframework.boot:spring-boot-starter-validation")
  implementation("org.springframework.boot:spring-boot-starter-data-jpa")
  implementation("org.springframework.boot:spring-boot-starter-security")
  implementation("org.springframework.kafka:spring-kafka")
  implementation("io.fabric8:kubernetes-client:6.9.0")
  implementation("io.kubernetes:client-java:25.0.0")
  implementation("org.redisson:redisson-spring-boot-starter:3.23.5")

  //Swagger
  implementation("org.springdoc:springdoc-openapi-starter-common:2.2.0")
  implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0")

  //MySql
  runtimeOnly("com.mysql:mysql-connector-j")

  //Lombok
  compileOnly("org.projectlombok:lombok")
  annotationProcessor("org.projectlombok:lombok")

  implementation("org.apache.commons:commons-lang3:3.20.0")

  implementation("com.google.code.gson:gson:2.13.2")

  testRuntimeOnly("com.h2database:h2")
  testCompileOnly("org.projectlombok:lombok")
  testAnnotationProcessor("org.projectlombok:lombok")
  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.getByName<Test>("test") {
  useJUnitPlatform()
}
