package paxby.meetup.rsvp.service;

import org.junit.Test;
import paxby.meetup.rsvp.model.Event;
import paxby.meetup.rsvp.model.Group;
import paxby.meetup.rsvp.model.Member;
import paxby.meetup.rsvp.model.Rsvp;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AppServiceTest {

    private final EventService eventService = mock(EventService.class);

    private final AppService appService = new AppService(eventService);

    private final Event SOME_EVENT = new Event(1, "some_event", new Group("some_group"));
    private final Member SOME_MEMBER = new Member();

    @Test
    public void rsvpToEventShouldRsvpIfAndOnlyIfNotAlreadyDone() throws Exception {
        when(eventService.getRsvps(SOME_EVENT)).thenReturn(Collections.emptyList());

        for (Rsvp.Response response : Rsvp.Response.values()) {
            when(eventService.getRsvp(any(), any())).thenReturn(Optional.of(new Rsvp(SOME_MEMBER, response)));
            appService.rsvpToEvent(SOME_EVENT, SOME_MEMBER);
        }
        verify(eventService, never()).rsvp(SOME_EVENT, SOME_MEMBER);

        when(eventService.getRsvp(any(), any())).thenReturn(Optional.empty());
        appService.rsvpToEvent(SOME_EVENT, SOME_MEMBER);
        verify(eventService).rsvp(SOME_EVENT, SOME_MEMBER);
    }
}