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
public class RsvpService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RsvpService.class);

    private final EventService eventService;

    @Autowired
    public RsvpService(EventService eventService) {
        this.eventService = eventService;
    }

    public void rsvpAnyUpcomingEvents(List<String> groupNames) {
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
