package paxby.meetup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import paxby.meetup.service.RsvpService;

import java.util.Arrays;

@SpringBootApplication
@Configuration
public class App implements CommandLineRunner {

    private final RsvpService rsvpService;

    @Autowired
    public App(RsvpService rsvpService) {
        this.rsvpService = rsvpService;
    }

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Override
    public void run(String... args) {
        rsvpService.rsvpAnyUpcomingEvents(Arrays.asList(args)); // TODO: Proper command line parsing
    }
}

