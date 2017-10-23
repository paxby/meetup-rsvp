package paxby.meetup.rsvp.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

@SuppressWarnings("unused")
public class Event {

    private long id;

    private Group group;

    private String name;

    private Status status;

    private Instant time;

    @JsonProperty("yes_rsvp_count")
    private long yesRsvpCount;

    @JsonProperty("rsvp_limit")
    private long rsvpLimit;

    public Event() {
    }

    public Event(long id, String name, Group group) {
        this.id = id;
        this.name = name;
        this.group = group;
    }

    public long getYesRsvpCount() {
        return yesRsvpCount;
    }

    public void setYesRsvpCount(long yesRsvpCount) {
        this.yesRsvpCount = yesRsvpCount;
    }

    public long getRsvpLimit() {
        return rsvpLimit;
    }

    public void setRsvpLimit(long rsvpLimit) {
        this.rsvpLimit = rsvpLimit;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTime(Instant time) {
        this.time = time;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String toString() {
        return String.format("'%s' on %s", name, time);
    }

    public enum Status {
        @JsonProperty("upcoming")
        UPCOMING,
        @JsonProperty("past")
        PAST,
        @JsonProperty("proposed")
        PROPOSED,
        @JsonProperty("suggested")
        SUGGESTED,
        @JsonProperty("cancelled")
        CANCELLED,
        @JsonProperty("draft")
        DRAFT
    }
}
