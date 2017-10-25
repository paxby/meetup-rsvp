package paxby.meetup.rsvp.model;

import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.time.Instant;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class EventTest {

    private final Event event = new Event();

    @Before
    public void setUp() {
        event.setTime(Instant.parse("2017-10-27T15:00:00.00Z"));
    }

    private Instant daysBefore(int n) {
        return event.getTime().minus(Duration.ofDays(n));
    }

    @Test
    public void rsvpsAreOpenShouldWorkWithRespectToOpenTime() {
        assertTrue(event.rsvpsAreOpen(daysBefore(2)));

        event.setRsvpOpenOffset(Duration.ofDays(1));
        assertFalse(event.rsvpsAreOpen(daysBefore(2)));
        assertTrue(event.rsvpsAreOpen(daysBefore(1)));
    }

    @Test
    public void rsvpsAreOpenShouldWorkWithRespectToCloseTime() {
        assertTrue(event.rsvpsAreOpen(daysBefore(2)));

        event.setRsvpCloseOffset(Duration.ofDays(3));
        assertFalse(event.rsvpsAreOpen(daysBefore(2)));
        assertTrue(event.rsvpsAreOpen(daysBefore(3)));
    }

    @Test
    public void rsvpsAreOpenShouldWorkWithRespectToOpenAndCloseTime() {
        assertTrue(event.rsvpsAreOpen(daysBefore(5)));
        assertTrue(event.rsvpsAreOpen(daysBefore(2)));

        event.setRsvpOpenOffset(Duration.ofDays(4));
        event.setRsvpCloseOffset(Duration.ofDays(3));
        assertFalse(event.rsvpsAreOpen(daysBefore(5)));
        assertTrue(event.rsvpsAreOpen(daysBefore(4)));
        assertTrue(event.rsvpsAreOpen(daysBefore(3)));
        assertFalse(event.rsvpsAreOpen(daysBefore(2)));
    }
}