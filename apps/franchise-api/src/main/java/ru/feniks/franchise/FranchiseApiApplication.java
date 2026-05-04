package ru.feniks.franchise;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FranchiseApiApplication {

  static {
    Dotenv dotenv =
        Dotenv.configure().ignoreIfMalformed().ignoreIfMissing().load();
    dotenv
        .entries()
        .forEach(
            e -> {
              if (System.getenv(e.getKey()) == null) {
                String v = e.getValue() == null ? "" : e.getValue().strip();
                System.setProperty(e.getKey(), v);
              }
            });
  }

  public static void main(String[] args) {
    SpringApplication.run(FranchiseApiApplication.class, args);
  }
}
