package hive.mugshot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
@EntityScan( basePackages = {"hive.entity"} )
public class MugshotApplication {
  public static void main(String[] args) {
    SpringApplication.run(MugshotApplication.class, args);
  }
}
