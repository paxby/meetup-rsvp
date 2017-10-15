package paxby.meetup.rsvp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import paxby.meetup.rsvp.model.Event;
import paxby.meetup.rsvp.model.Group;
import paxby.meetup.rsvp.model.Member;
import paxby.meetup.rsvp.model.Rsvp;
import paxby.meetup.rsvp.service.exception.RsvpException;
import paxby.meetup.rsvp.util.UrlHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

public class EventServiceTest {

    private static final Group SOME_GROUP = new Group("some_group");
    private static final Event SOME_EVENT = new Event(1, "some_event", SOME_GROUP);
    private static final Member SOME_MEMBER = new Member(1, "some_member");

    private final UrlHelper urlHelper = new UrlHelper();

    private EventService eventService;

    private MockRestServiceServer mockServer;

    @Before
    public void setUp() throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        mockServer = MockRestServiceServer.createServer(restTemplate);
        eventService = new EventService(restTemplate);
    }

    @Test
    public void getUpcomingEventsShouldReturnOnlyUpcomingEvents() throws Exception {

        Event pastEvent = new Event(1, "upcoming", SOME_GROUP);
        pastEvent.setStatus(Event.Status.PAST);

        Event upcomingEvent = new Event(2, "past", SOME_GROUP);
        upcomingEvent.setStatus(Event.Status.UPCOMING);

        List<Event> mockEvents = new ArrayList<>();
        mockEvents.add(pastEvent);
        mockEvents.add(upcomingEvent);

        mockServer.expect(requestTo(urlHelper.eventsUrl(SOME_GROUP.getUrlName())))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(json(mockEvents), MediaType.APPLICATION_JSON));

        List<Event> events = eventService.getUpcomingEvents(SOME_GROUP.getUrlName());
        mockServer.verify();

        assertEquals(1, events.size());
        assertEquals(2, events.get(0).getId());
    }

    @Test
    public void getRsvpsShouldCallApiCorrectly() throws JsonProcessingException {
        mockServer.expect(requestTo(urlHelper.rsvpUrl(SOME_EVENT)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess()
                        .body(json(Collections.singletonList(new Rsvp(SOME_MEMBER, Rsvp.Response.YES))))
                        .contentType(MediaType.APPLICATION_JSON));

        eventService.getRsvps(SOME_EVENT);
        mockServer.verify();
    }

    @Test
    public void rsvpShouldSendCorrectContent() throws JsonProcessingException {
        mockServer.expect(requestTo(urlHelper.rsvpUrl(SOME_EVENT)))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(content().string(containsString("agree_to_refund=false")))  // TODO: parse the form data?
                .andExpect(content().string(containsString("opt_to_pay=false")))
                .andExpect(content().string(containsString("agree_to_refund=false")))
                .andExpect(content().string(containsString("response=yes")))
                .andRespond(withStatus(HttpStatus.CREATED)
                        .body(json(new Rsvp(SOME_MEMBER, Rsvp.Response.YES)))
                        .contentType(MediaType.APPLICATION_JSON_UTF8));

        eventService.rsvp(SOME_EVENT, SOME_MEMBER);
        mockServer.verify();
    }

    @Test(expected = RsvpException.class)
    public void rsvpShouldThrowErrorIfMemberNotInResponse() throws JsonProcessingException {
        mockServer.expect(requestTo(urlHelper.rsvpUrl(SOME_EVENT)))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(json(new Rsvp(new Member(2, "other"), Rsvp.Response.YES))));

        eventService.rsvp(SOME_EVENT, SOME_MEMBER);
        mockServer.verify();
    }

    @Test(expected = RsvpException.class)
    public void rsvpShouldThrowErrorIfMemberResponseIsNotYesOrWaitlist() throws JsonProcessingException {
        mockServer.expect(requestTo(urlHelper.rsvpUrl(SOME_EVENT)))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(json(new Rsvp(SOME_MEMBER, Rsvp.Response.NO))));

        eventService.rsvp(SOME_EVENT, SOME_MEMBER);
        mockServer.verify();
    }

    private String json(Object object) throws JsonProcessingException {
        return Jackson2ObjectMapperBuilder.json().build().writeValueAsString(object);
    }
}