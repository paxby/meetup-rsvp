package paxby.meetup.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import paxby.meetup.model.Event;
import paxby.meetup.model.Member;
import paxby.meetup.model.Rsvp;
import paxby.meetup.util.UrlHelper;

import java.util.Arrays;
import java.util.List;

@Service
public class EventService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventService.class);

    private final RestTemplate restTemplate;

    private final UrlHelper urlHelper;

    @Autowired
    public EventService(RestTemplate restTemplate, UrlHelper urlHelper) {
        this.restTemplate = restTemplate;
        this.urlHelper = urlHelper;
    }

    public List<Event> getEvents(String name) {
        return Arrays.asList(restTemplate.getForObject(urlHelper.eventsUrl(name), Event[].class));
    }

    public List<Rsvp> getRsvps(Event event) {
        return Arrays.asList(restTemplate.getForObject(urlHelper.rsvpUrl(event), Rsvp[].class));
    }

    public Member getMember() {
        return restTemplate.getForObject(urlHelper.memberUrl(), Member.class);
    }

    public boolean hasRsvpd(Event event, Member member) {
        return getRsvps(event).stream()
                .map(r -> r.getMember().getId())
                .anyMatch(x -> x.equals(member.getId()));
    }

    private HttpEntity<MultiValueMap<String, Object>> postRequestEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // TODO: Create builder ?
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("agree_to_refund", "false");
        map.add("guests", "0");
        map.add("opt_to_pay", "false");
        map.add("response", "yes");

        return new HttpEntity<>(map, headers);
    }

    public void rsvp(Event event, Member member) throws JsonProcessingException {

        // TODO: Should check eligibility first

        ResponseEntity<Rsvp> response = restTemplate.postForEntity(urlHelper.rsvpUrl(event), postRequestEntity(), Rsvp.class);

        if (response.getStatusCode() != HttpStatus.CREATED
                || response.getBody().getMember().getId() != member.getId()
                || (response.getBody().getResponse() != Rsvp.Response.YES && response.getBody().getResponse() != Rsvp.Response.WAITLIST)) {
            throw new RuntimeException("RSVP failed"); // TODO: Add exception
        }
    }

    private static class RsvpPost {

        @JsonProperty("agree_to_refund")
        private boolean agreeToRefund = false;

        private Integer guests = 0;

        @JsonProperty("opt_to_pay")
        private boolean optToPay = false;

        @JsonProperty("pro_email_share_optin")
        private boolean proEmailShareOptin = false;

        private Rsvp.Response response = Rsvp.Response.YES;

        public boolean isAgreeToRefund() {
            return agreeToRefund;
        }

        public Integer getGuests() {
            return guests;
        }

        public boolean isOptToPay() {
            return optToPay;
        }

        public boolean isProEmailShareOptin() {
            return proEmailShareOptin;
        }

        public Rsvp.Response getResponse() {
            return response;
        }
    }
}
