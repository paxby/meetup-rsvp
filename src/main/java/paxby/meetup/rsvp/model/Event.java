package paxby.meetup.rsvp.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Duration;
import java.time.Instant;

@SuppressWarnings({"unused", "WeakerAccess"})
public class Event {

    private String id;

    private Group group;

    private String name;

    private Status status;

    private Instant time;

    @JsonProperty("rsvp_open_offset")
    private Duration rsvpOpenOffset;

    @JsonProperty("rsvp_close_offset")
    private Duration rsvpCloseOffset;

    @JsonProperty("yes_rsvp_count")
    private long yesRsvpCount;

    @JsonProperty("rsvp_limit")
    private long rsvpLimit;

    public Event() {
    }

    public boolean rsvpsAreOpen(Instant atTime) {

        if (rsvpOpenOffset != null && atTime.isBefore(time.minus(rsvpOpenOffset))) {
            return false;
        } else if (rsvpCloseOffset != null && atTime.isAfter(time.minus(rsvpCloseOffset))) {
            return false;
        }
        return true;
    }

    public Event(String id, String name, Group group) {
        this.id = id;
        this.name = name;
        this.group = group;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Instant getTime() {
        return time;
    }

    public void setTime(Instant time) {
        this.time = time;
    }

    public Duration getRsvpOpenOffset() {
        return rsvpOpenOffset;
    }

    public void setRsvpOpenOffset(Duration rsvpOpenOffset) {
        this.rsvpOpenOffset = rsvpOpenOffset;
    }

    public Duration getRsvpCloseOffset() {
        return rsvpCloseOffset;
    }

    public void setRsvpCloseOffset(Duration rsvpCloseOffset) {
        this.rsvpCloseOffset = rsvpCloseOffset;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
