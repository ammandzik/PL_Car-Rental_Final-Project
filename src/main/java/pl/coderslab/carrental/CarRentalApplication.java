package pl.coderslab.carrental;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import pl.coderslab.carrental.env.EnvLoader;

@SpringBootApplication
@EnableCaching
public class CarRentalApplication {

    @Autowired
    private CacheManager cacheManager;

    public static void main(String[] args) {

        System.setProperty("MYSQL_USER", EnvLoader.getEnv("MYSQL_USER"));
        System.setProperty("MYSQL_PASSWORD", EnvLoader.getEnv("MYSQL_PASSWORD"));
        System.setProperty("MYSQL_DATABASE", EnvLoader.getEnv("MYSQL_DATABASE"));
        System.setProperty("DB_HOST", EnvLoader.getEnv("DB_HOST"));
        System.setProperty("DB_PORT", EnvLoader.getEnv("DB_PORT"));
        System.setProperty("SPRING_REDIS_HOST", EnvLoader.getEnv("SPRING_REDIS_HOST"));
        System.setProperty("SPRING_REDIS_PORT", EnvLoader.getEnv("SPRING_REDIS_PORT"));
        System.setProperty("SPRING_DATASOURCE_URL", EnvLoader.getEnv("SPRING_DATASOURCE_URL"));

        SpringApplication.run(CarRentalApplication.class, args);
    }
    @PostConstruct
    public void checkCacheManager() {
        System.out.println(">>> CacheManager używany przez Spring: " + cacheManager.getClass().getName());
    }

}
