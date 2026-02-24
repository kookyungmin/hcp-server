plugins {
  id("java")
  id("org.springframework.boot") version "3.5.8" apply false
  id("io.spring.dependency-management") version "1.1.7" apply false
}

allprojects {
  group = "net.happykoo.hcp"
  description = "Happy Cloud Platform Server"

  repositories {
    mavenCentral()
  }
}

subprojects {
  apply(plugin = "java")
  apply(plugin = "org.springframework.boot")
  apply(plugin = "io.spring.dependency-management")

  java {
    toolchain {
      languageVersion.set(JavaLanguageVersion.of(21))
    }
  }

  dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
//        implementation("org.springframework.kafka:spring-kafka")

    //Swagger
    implementation("org.springdoc:springdoc-openapi-starter-common:2.2.0")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0")

    //Lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    testRuntimeOnly("com.h2database:h2")
    testCompileOnly("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
  }

  tasks.getByName<Test>("test") {
    useJUnitPlatform()
  }
}


