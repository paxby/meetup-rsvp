package paxby.meetup.rsvp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import paxby.meetup.rsvp.model.Event;
import paxby.meetup.rsvp.model.Member;
import paxby.meetup.rsvp.model.Rsvp;
import paxby.meetup.rsvp.service.exception.RsvpException;
import paxby.meetup.rsvp.util.UrlHelper;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@SuppressWarnings("WeakerAccess")
public class EventService {

    private final RestTemplate restTemplate;

    private final UrlHelper urlHelper = new UrlHelper();

    @Autowired
    public EventService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
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

    public void rsvp(Event event, Member member) {
        HttpEntity request = new PostRequestHttpEntityBuilder()
                .add("agree_to_refund", "false")
                .add("guests", 0)
                .add("opt_to_pay", false)
                .add("response", "yes")
                .build();

        ResponseEntity<Rsvp> response = restTemplate.postForEntity(urlHelper.rsvpUrl(event), request, Rsvp.class);

        if (response.getBody().getMember().getId() != member.getId()) {
            throw new RsvpException("RSVP post request did not return member list containing self");
        } else if (response.getBody().getResponse() != Rsvp.Response.YES && response.getBody().getResponse() != Rsvp.Response.WAITLIST) {
            throw new RsvpException(String.format("RSVP post returned %s, expected one of [YES, WAITLIST] ", response.getBody().getResponse()));
        }
    }

    private class PostRequestHttpEntityBuilder {
        final MultiValueMap<String, String> map = new LinkedMultiValueMap<>();

        PostRequestHttpEntityBuilder add(String key, Object value) {
            map.add(key, value.toString());
            return this;
        }

        HttpEntity build() {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            return new HttpEntity<>(map, headers);
        }
    }
}
