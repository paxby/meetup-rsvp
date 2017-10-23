package paxby.meetup.rsvp.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import paxby.meetup.rsvp.config.AppConfig;
import paxby.meetup.rsvp.model.Event;
import paxby.meetup.rsvp.model.Member;
import paxby.meetup.rsvp.model.Rsvp;

import java.util.List;
import java.util.Optional;

@Service
@SuppressWarnings("WeakerAccess")
public class AppService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppService.class);

    private final EventService eventService;

    private final AppConfig config;

    @Autowired
    public AppService(EventService eventService, AppConfig appConfig) {
        this.eventService = eventService;
        this.config = appConfig;
    }

    public void run(List<String> groupNames) {
        Member member = eventService.getMember();

        for (String groupName : groupNames) {
            List<Event> events = eventService.getUpcomingEvents(groupName);
            LOGGER.info("Group {} has {} upcoming events", groupName, events.size());

            events.forEach(event -> processEvent(event, member));
        }
    }

    public void processEvent(Event event, Member member) {
        List<Rsvp> rsvps = eventService.getRsvps(event);
        Optional<Rsvp> selfRsvp = eventService.getRsvp(rsvps, member);

        LOGGER.info("Event: {} (rsvpLimit: {}, yesCount: {}, rsvp: {})", event, event.getRsvpLimit(),
                event.getYesRsvpCount(), selfRsvp.map(r -> r.getResponse().name()).orElse("NONE"));

        if (!selfRsvp.isPresent()) {
            if (!config.isJoinWaitListOnly() ||
                    (event.getRsvpLimit() > 0 && event.getYesRsvpCount() >= event.getRsvpLimit())) {
                LOGGER.info("Sending rsvp for {} event {}", event.getGroup().getName(), event);
                eventService.rsvp(event, member);
            }
        }
    }
}
