package net.happykoo.hcp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ComputeServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(ComputeServiceApplication.class, args);
  }
}
