package paxby.meetup.model;

import com.fasterxml.jackson.annotation.JsonProperty;

@SuppressWarnings("unused")
public class Rsvp {

    private Member member;

    private Response response;

    public Rsvp() {
    }

    public Rsvp(Member member, Response response) {
        this.member = member;
        this.response = response;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public enum Response {
        @JsonProperty("yes")
        YES,
        @JsonProperty("no")
        NO,
        @JsonProperty("waitlist")
        WAITLIST
    }
}