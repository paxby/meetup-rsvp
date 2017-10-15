package paxby.meetup.rsvp.model;

import com.fasterxml.jackson.annotation.JsonProperty;

@SuppressWarnings("unused")
public class Group {

    private String name;

    @JsonProperty("urlname")
    private String urlName;

    public Group() {
    }

    public Group(String name) {
        this.name = name;
        this.urlName = name;
    }

    public Group(String name, String urlName) {
        this.name = name;
        this.urlName = urlName;
    }

    public String getUrlName() {
        return urlName;
    }

    public void setUrlName(String urlName) {
        this.urlName = urlName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
