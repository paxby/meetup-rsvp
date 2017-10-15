package paxby.meetup.rsvp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import paxby.meetup.rsvp.service.AppService;

import java.util.Arrays;

@SpringBootApplication
@Configuration
public class App implements CommandLineRunner {

    private final AppService appService;

    @Autowired
    public App(AppService appService) {
        this.appService = appService;
    }

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Override
    public void run(String... args) {
        appService.run(Arrays.asList(args));
    }
}

