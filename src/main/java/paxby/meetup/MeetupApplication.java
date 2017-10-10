package paxby.meetup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

@SpringBootApplication
@Configuration
public class MeetupApplication implements CommandLineRunner {

    public static final Logger LOGGER = LoggerFactory.getLogger(MeetupApplication.class);

    private final EventService eventService;

    @Autowired
    public MeetupApplication(EventService eventService) {
        this.eventService = eventService;
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(MeetupApplication.class, args);
    }

    @Override
    public void run(String... strings) throws Exception {

        List<String> groups = Arrays.asList(
                /*
                "PyData-London-Meetup",
                "London-New-Tech",
                "sqlsupperlondon"
                */
                "Ansible-London"
        );

        Member self = eventService.getMember();

        for (String groupName : groups) {
            List<Event> events = eventService.getEvents(groupName);
            LOGGER.info("Group: " + groupName);
            for (Event event : events) {
                LOGGER.info("Event: " + event);
                List<Rsvp> rsvps = eventService.getRsvps(event);
                //rsvps.forEach(x -> LOGGER.info(x.getMember() + ":" + x.getResponse()));
                LOGGER.info("Has rsvpd: " + eventService.hasRsvpd(event, self));
                eventService.rsvp(event, self);
            }
        }
    }
}

