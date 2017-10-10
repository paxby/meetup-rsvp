package paxby.meetup.util;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import paxby.meetup.model.Event;

@Component
public class UrlHelper {

    public static final String URL_PREFIX = "http://api.meetup.com";

    public String eventsUrl(String groupName) {
        return String.format("%s/%s/events", URL_PREFIX, groupName);
    }

    public String eventUrl(Event event) {
        return String.format("%s/%s", eventsUrl(event.getGroup().getUrlName()), event.getId());
    }

    public String rsvpUrl(Event event) {
        return String.format("%s/rsvps", eventUrl(event));
    }

    public String memberUrl() {
        return String.format("%s/2/member/self", URL_PREFIX);
    }
}
