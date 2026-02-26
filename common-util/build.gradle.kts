plugins {
  id("java")
}

group = "net.happykoo.hcp"
version = "0.0.1"
description = "Happy Cloud Platform Common Util Module"

java {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(21))
  }
}

repositories {
  mavenCentral()
}

dependencies {
  //JWT 관련
  implementation("io.jsonwebtoken:jjwt-api:0.12.6")
  implementation("io.jsonwebtoken:jjwt-impl:0.12.6")
  runtimeOnly("io.jsonwebtoken:jjwt-gson:0.12.6")

  //Lombok
  compileOnly("org.projectlombok:lombok:1.18.38")
  annotationProcessor("org.projectlombok:lombok:1.18.38")

  implementation("org.apache.commons:commons-lang3:3.20.0")

  testCompileOnly("org.projectlombok:lombok:1.18.38")
  testAnnotationProcessor("org.projectlombok:lombok:1.18.38")
  testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.12.2")
}

tasks.getByName<Test>("test") {
  useJUnitPlatform()
}
