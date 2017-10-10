package paxby.meetup.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import paxby.meetup.model.Event;
import paxby.meetup.model.Group;
import paxby.meetup.model.Member;
import paxby.meetup.model.Rsvp;
import paxby.meetup.util.UrlHelper;

import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

public class EventServiceTest {

    private static final String SOME_GROUP = "some_group";
    private static final String SOME_EVENT = "some_event";
    private static final String SOME_MEMBER = "some_member";

    private final UrlHelper urlHelper = new UrlHelper();

    private ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json().build();

    private EventService eventService;

    private List<Event> mockEvents;

    private MockRestServiceServer mockServer;

    @Before
    public void setUp() throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        mockServer = MockRestServiceServer.createServer(restTemplate);
        eventService = new EventService(restTemplate, urlHelper);
        mockEvents = Collections.singletonList(new Event(12345, SOME_EVENT, new Group(SOME_GROUP)));
    }

    @Test
    public void getEvents() throws Exception {
        mockServer.expect(requestTo(urlHelper.eventsUrl(SOME_GROUP)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(objectMapper.writeValueAsString(mockEvents), MediaType.APPLICATION_JSON));

        List<Event> events = eventService.getEvents(SOME_GROUP);

        IntStream.of(Integer.max(mockEvents.size(), events.size()) - 1).forEach(
                n -> assertEquals(mockEvents.get(n).getId(), events.get(n).getId())
        );
    }

    @Test
    public void rsvp() throws JsonProcessingException {

        Event event = new Event(12345, SOME_EVENT, new Group(SOME_GROUP));
        Member member = new Member(1, SOME_MEMBER);

        Rsvp expectedRsvp = new Rsvp(member, Rsvp.Response.YES);

        mockServer.expect(requestTo(urlHelper.rsvpUrl(event)))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(content().string("agree_to_refund=false&guests=0&opt_to_pay=false&response=yes"))
                .andRespond(withStatus(HttpStatus.CREATED).body(objectMapper.writeValueAsString(expectedRsvp))
                        .contentType(MediaType.APPLICATION_JSON_UTF8));

        // TODO: Is the order of form params guaranteed?

        eventService.rsvp(event, member);
    }
}