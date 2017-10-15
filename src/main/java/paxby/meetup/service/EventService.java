package paxby.meetup.service;

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
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EventService {

    private final RestTemplate restTemplate;

    private final UrlHelper urlHelper;

    @Autowired
    public EventService(RestTemplate restTemplate, UrlHelper urlHelper) {
        this.restTemplate = restTemplate;
        this.urlHelper = urlHelper;
    }

    public List<Event> getUpcomingEvents(String name) {
        return Arrays.stream(restTemplate.getForObject(urlHelper.eventsUrl(name), Event[].class))
                // API should only return upcoming events, but in case this changes
                .filter(event -> event.getStatus() == Event.Status.UPCOMING)
                .collect(Collectors.toList());
    }

    public List<Rsvp> getRsvps(Event event) {
        return Arrays.asList(restTemplate.getForObject(urlHelper.rsvpUrl(event), Rsvp[].class));
    }

    public Member getMember() {
        return restTemplate.getForObject(urlHelper.memberUrl(), Member.class);
    }

    public Optional<Rsvp> getRsvp(List<Rsvp> rsvps, Member member) {
        return rsvps.stream()
                .filter(rsvp -> rsvp.getMember().getId() == member.getId())
                .findFirst();
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

    public void rsvp(Event event, Member member) {
        ResponseEntity<Rsvp> response = restTemplate.postForEntity(urlHelper.rsvpUrl(event), postRequestEntity(), Rsvp.class);

        if (response.getStatusCode() != HttpStatus.CREATED) {
            throw new RsvpException(String.format("RSVP post request returned status %s (%s)", response.getStatusCodeValue(), response.getStatusCode().name()));
        } else if (response.getBody().getMember().getId() != member.getId()) {
            throw new RsvpException("RSVP post request did not return member list containing self");
        } else if (response.getBody().getResponse() != Rsvp.Response.YES && response.getBody().getResponse() != Rsvp.Response.WAITLIST) {
            throw new RsvpException(String.format("RSVP post returned %s, expected one of [YES, WAITLIST] ", response.getBody().getResponse()));
        }
    }

    private class RsvpException extends RuntimeException {
        private RsvpException(String message) {
            super(message);
        }
    }
}
