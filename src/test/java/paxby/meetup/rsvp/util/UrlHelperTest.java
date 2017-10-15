package paxby.meetup.rsvp.util;

import org.junit.Before;
import org.junit.Test;
import paxby.meetup.rsvp.model.Event;
import paxby.meetup.rsvp.model.Group;

import static org.junit.Assert.assertEquals;


public class UrlHelperTest {

    private UrlHelper urlHelper;

    @Before
    public void setUp() {
        this.urlHelper = new UrlHelper();
    }

    @Test
    public void eventsUrl() throws Exception {
        assertEquals("http://api.meetup.com/someMeetup/events", urlHelper.eventsUrl("someMeetup"));
    }

    @Test
    public void eventUrl() throws Exception {
        Event event = new Event(1, "some_event", new Group("some_group"));
        assertEquals("http://api.meetup.com/some_group/events/1", urlHelper.eventUrl(event));
    }

    @Test
    public void rsvpUrl() throws Exception {
        Event event = new Event(1, "some_event", new Group("some_group"));
        assertEquals("http://api.meetup.com/some_group/events/1/rsvps", urlHelper.rsvpUrl(event));
    }
}