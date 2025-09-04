package pl.coderslab.carrental;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import pl.coderslab.carrental.env.EnvLoader;

@SpringBootApplication
public class CarRentalApplication {

    public static void main(String[] args) {

        System.setProperty("MYSQL_USER", EnvLoader.getEnv("MYSQL_USER"));
        System.setProperty("MYSQL_PASSWORD", EnvLoader.getEnv("MYSQL_PASSWORD"));
        System.setProperty("MYSQL_DATABASE", EnvLoader.getEnv("MYSQL_DATABASE"));
        System.setProperty("SPRING_DATASOURCE_URL", EnvLoader.getEnv("SPRING_DATASOURCE_URL"));
        System.setProperty("SPRING_DATASOURCE_USER", EnvLoader.getEnv("SPRING_DATASOURCE_USER"));
        System.setProperty("SPRING_DATASOURCE_PASSWORD", EnvLoader.getEnv("SPRING_DATASOURCE_PASSWORD"));
        System.setProperty("DB_HOST", EnvLoader.getEnv("DB_HOST"));
        System.setProperty("DB_PORT", EnvLoader.getEnv("DB_PORT"));

        SpringApplication.run(CarRentalApplication.class, args);
    }

}
