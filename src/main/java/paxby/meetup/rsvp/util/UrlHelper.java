package paxby.meetup.rsvp.util;

import paxby.meetup.rsvp.model.Event;

@SuppressWarnings("WeakerAccess")
public class UrlHelper {

    private static final String URL_PREFIX = "http://api.meetup.com";

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
        return String.format("%s/members/self", URL_PREFIX);
    }
}
