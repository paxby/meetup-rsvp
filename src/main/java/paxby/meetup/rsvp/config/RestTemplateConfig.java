package paxby.meetup.rsvp.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Configuration
public class RestTemplateConfig {

    private final ClientHttpRequestInterceptor keyInterceptor;

    @Autowired
    public RestTemplateConfig(KeyAuthenticationInterceptor interceptor) {
        this.keyInterceptor = interceptor;
    }

    @Bean
    RestTemplate restTemplateBean() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().forEach(x -> {
                    if (x instanceof MappingJackson2HttpMessageConverter) {
                        ((MappingJackson2HttpMessageConverter) x).getObjectMapper()
                                .disable(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS);
                    }
                }
        );
        restTemplate.setInterceptors(Collections.singletonList(keyInterceptor));
        return restTemplate;
    }
}
