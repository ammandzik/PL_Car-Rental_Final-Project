package pl.coderslab.carrental;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import pl.coderslab.carrental.env.EnvLoader;

@SpringBootApplication
public class CarRentalApplication {

    public static void main(String[] args) {

        System.setProperty("DB_USER", EnvLoader.getEnv("DB_USER"));
        System.setProperty("DB_PASS", EnvLoader.getEnv("DB_PASS"));
        System.setProperty("DB_NAME", EnvLoader.getEnv("DB_NAME"));
        System.setProperty("DB_HOST", EnvLoader.getEnv("DB_HOST"));
        System.setProperty("DB_PORT", EnvLoader.getEnv("DB_PORT"));

        SpringApplication.run(CarRentalApplication.class, args);
    }

}
