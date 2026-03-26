plugins {
  id("java")
  id("org.springframework.boot") version "3.5.8"
  id("io.spring.dependency-management") version "1.1.7"
}

group = "net.happykoo.hcp"
version = "0.0.1"
description = "Happy Cloud Platform API Gateway"

java {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(21))
  }
}

repositories {
  mavenCentral()
}

dependencies {
  implementation(project(":common-util"))
  implementation(platform("org.springframework.cloud:spring-cloud-dependencies:2025.0.1"))
  implementation("org.springframework.cloud:spring-cloud-starter-gateway") // WebFlux Gateway
  implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive")
  implementation("org.springframework.boot:spring-boot-starter-actuator")

  //Prometheus
  implementation("io.micrometer:micrometer-registry-prometheus")

  //logstash
  implementation("net.logstash.logback:logstash-logback-encoder:7.4")

  implementation("io.micrometer:micrometer-tracing-bridge-otel")
  implementation("io.opentelemetry:opentelemetry-exporter-otlp")

  //Lombok
  compileOnly("org.projectlombok:lombok")
  annotationProcessor("org.projectlombok:lombok")

  implementation("org.apache.commons:commons-lang3:3.20.0")

  testImplementation("org.springframework.boot:spring-boot-starter-test")

}

tasks.getByName<Test>("test") {
  useJUnitPlatform()
}
