package paxby.meetup.rsvp.service;

import org.junit.Before;
import org.junit.Test;
import paxby.meetup.rsvp.config.AppConfig;
import paxby.meetup.rsvp.model.Event;
import paxby.meetup.rsvp.model.Group;
import paxby.meetup.rsvp.model.Member;
import paxby.meetup.rsvp.model.Rsvp;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AppServiceTest {

    private final EventService eventService = mock(EventService.class);

    private final AppConfig config = new AppConfig();

    private final AppService appService = new AppService(eventService, config);

    private final Event SOME_EVENT = new Event(1, "some_event", new Group("some_group"));
    private final Member SOME_MEMBER = new Member();

    @Before
    public void setUp() {
        when(eventService.getRsvps(SOME_EVENT)).thenReturn(Collections.emptyList());
        when(eventService.getRsvp(any(), any())).thenReturn(Optional.empty());
    }

    @Test
    public void processEventShouldRsvpIfAndOnlyIfNotAlreadyDone() throws Exception {

        for (Rsvp.Response response : Rsvp.Response.values()) {
            when(eventService.getRsvp(any(), any())).thenReturn(Optional.of(new Rsvp(SOME_MEMBER, response)));
            appService.processEvent(SOME_EVENT, SOME_MEMBER);
        }
        verify(eventService, never()).rsvp(SOME_EVENT, SOME_MEMBER);

        when(eventService.getRsvp(any(), any())).thenReturn(Optional.empty());
        appService.processEvent(SOME_EVENT, SOME_MEMBER);
        verify(eventService).rsvp(SOME_EVENT, SOME_MEMBER);
    }

    @Test
    public void processEventShouldRsvpIfNotInWaitListMode() throws Exception {
        SOME_EVENT.setRsvpLimit(10);

        SOME_EVENT.setYesRsvpCount(9);
        appService.processEvent(SOME_EVENT, SOME_MEMBER);
        verify(eventService).rsvp(SOME_EVENT, SOME_MEMBER);
    }

    @Test
    public void processEventShouldNotRsvpIfNotPutOnWaitListInWaitListMode() throws Exception {
        config.setJoinWaitListOnly(true);
        SOME_EVENT.setRsvpLimit(10);

        SOME_EVENT.setYesRsvpCount(9);
        appService.processEvent(SOME_EVENT, SOME_MEMBER);
        verify(eventService, never()).rsvp(SOME_EVENT, SOME_MEMBER);

        SOME_EVENT.setYesRsvpCount(10);
        appService.processEvent(SOME_EVENT, SOME_MEMBER);
        verify(eventService).rsvp(SOME_EVENT, SOME_MEMBER);

        SOME_EVENT.setYesRsvpCount(11);
        appService.processEvent(SOME_EVENT, SOME_MEMBER);
        verify(eventService, times(2)).rsvp(SOME_EVENT, SOME_MEMBER);
    }

    @Test
    public void processEventShouldNotRsvpIfNoRsvpLimitSetInWaitListMode() throws Exception {
        config.setJoinWaitListOnly(true);
        SOME_EVENT.setRsvpLimit(0);

        SOME_EVENT.setYesRsvpCount(0);
        appService.processEvent(SOME_EVENT, SOME_MEMBER);

        SOME_EVENT.setYesRsvpCount(1);
        appService.processEvent(SOME_EVENT, SOME_MEMBER);

        verify(eventService, never()).rsvp(SOME_EVENT, SOME_MEMBER);
    }
}