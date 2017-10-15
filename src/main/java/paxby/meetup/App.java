package paxby.meetup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import paxby.meetup.model.Event;
import paxby.meetup.model.Member;
import paxby.meetup.model.Rsvp;
import paxby.meetup.service.EventService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@SpringBootApplication
@Configuration
public class App implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    private final EventService eventService;

    @Value("${meetup.groups}")
    private String groupNames;

    @Autowired
    public App(EventService eventService) {
        this.eventService = eventService;
    }

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    public static void lambdaRun() {
        main(new String[0]);
    }

    @Override
    public void run(String... args) {
        List<String> groupNames = Arrays.asList(this.groupNames.split(","));
        rsvpAnyUpcomingEvents(groupNames);
    }

    private void rsvpAnyUpcomingEvents(List<String> groupNames) {
        Member self = eventService.getMember();

        for (String groupName : groupNames) {
            List<Event> events = eventService.getUpcomingEvents(groupName);
            LOGGER.info("Group {} has {} upcoming events", groupName, events.size());

            for (Event event : events) {
                List<Rsvp> rsvps = eventService.getRsvps(event);
                Optional<Rsvp> selfRsvp = eventService.getRsvp(rsvps, self);
                LOGGER.info("Event: {} (rsvp: {})", event, selfRsvp.map(r -> r.getResponse().name()) .orElse("NONE"));

                if (!selfRsvp.isPresent()) {
                    LOGGER.info("Sending rsvp for {} event {}", groupName, event);
                    eventService.rsvp(event, self);
                }
            }
        }
    }
}

