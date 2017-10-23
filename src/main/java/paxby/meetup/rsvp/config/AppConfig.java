package paxby.meetup.rsvp.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class AppConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppConfig.class);

    @Value("${meetup.waitlist-only:false}")
    private boolean joinWaitListOnly;

    @PostConstruct
    public void postConstruct() {
        LOGGER.info("Config (waitlist-only): {}", joinWaitListOnly);
    }

    public boolean isJoinWaitListOnly() {
        return joinWaitListOnly;
    }

    public void setJoinWaitListOnly(boolean joinWaitListOnly) {
        this.joinWaitListOnly = joinWaitListOnly;
    }
}
