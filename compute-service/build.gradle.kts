plugins {
  id("java")
  id("org.springframework.boot") version "3.5.8"
  id("io.spring.dependency-management") version "1.1.7"
}

group = "net.happykoo.hcp"
version = "0.0.1"
description = "Happy Cloud Platform Compute Service"

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
  implementation("org.springframework.boot:spring-boot-starter-data-redis")
  implementation("org.springframework.boot:spring-boot-starter-security")
  implementation("org.springframework.kafka:spring-kafka")

  //Prometheus
  implementation("io.micrometer:micrometer-registry-prometheus")

  //logstash
  implementation("net.logstash.logback:logstash-logback-encoder:7.4")

  //Swagger
  implementation("org.springdoc:springdoc-openapi-starter-common:2.2.0")
  implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0")

  //querydsl
  implementation("com.querydsl:querydsl-jpa:5.1.0:jakarta")
  annotationProcessor("com.querydsl:querydsl-apt:5.1.0:jakarta")
  annotationProcessor("jakarta.annotation:jakarta.annotation-api")
  annotationProcessor("jakarta.persistence:jakarta.persistence-api")

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
