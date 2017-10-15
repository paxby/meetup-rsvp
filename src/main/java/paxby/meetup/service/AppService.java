package paxby.meetup.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import paxby.meetup.model.Event;
import paxby.meetup.model.Member;
import paxby.meetup.model.Rsvp;

import java.util.List;
import java.util.Optional;

@Service
public class AppService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppService.class);

    private final EventService eventService;

    @Autowired
    public AppService(EventService eventService) {
        this.eventService = eventService;
    }

    public void rsvpToEvent(Event event, Member member) {
        List<Rsvp> rsvps = eventService.getRsvps(event);
        Optional<Rsvp> selfRsvp = eventService.getRsvp(rsvps, member);

        LOGGER.info("Event: {} (rsvp: {})", event, selfRsvp.map(r -> r.getResponse().name()).orElse("NONE"));

        if (!selfRsvp.isPresent()) {
            LOGGER.info("Sending rsvp for {} event {}", event.getGroup().getName(), event);
            eventService.rsvp(event, member);
        }
    }

    public void run(List<String> groupNames) {
        Member member = eventService.getMember();

        for (String groupName : groupNames) {
            List<Event> events = eventService.getUpcomingEvents(groupName);
            LOGGER.info("Group {} has {} upcoming events", groupName, events.size());

            for (Event event : events) {
                rsvpToEvent(event, member);
            }
        }
    }
}
