package paxby.meetup.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public class Event {

    private long created;

    private long id;

    private Group group;

    private String name;

    private String status;

    private Instant time;

    @JsonProperty("join_mode")
    private String joinMode;

    public Event() {
    }

    public Event(long id, String name, Group group) {
        this.id = id;
        this.name = name;
        this.group = group;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public void setCreated(long created) {
        this.created = created;
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

    public void setStatus(String status) {
        this.status = status;
    }

    public void setTime(Instant time) {
        this.time = time;
    }

    public void setJoin_mode(String join_mode) {
        this.joinMode = join_mode;
    }

    public String toString() {
        return String.format("'%s' on %s: %s", name, time, status);
    }

}
