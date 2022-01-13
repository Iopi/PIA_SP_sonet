package cz.zcu.fav.pia.sonet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan
public class SonetApp {

    public static void main(String[] args) {
        SpringApplication.run(SonetApp.class, args);
    }

}
