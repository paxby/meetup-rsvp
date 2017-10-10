package paxby.meetup.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.support.HttpRequestWrapper;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Configuration
public class KeyInterceptor implements ClientHttpRequestInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(KeyInterceptor.class);

    @Value("${meetup.api.key}")
    private String apiKey;

    private String[] blah;

    // TODO: Could this be done easier?
    private Set<String> restrictedGet = new HashSet(Arrays.asList(new String[] {
            "http://api.meetup.com/2/member/self",
    }));

    @Override
    public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] bytes, ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {
        return clientHttpRequestExecution.execute(new MyHttpRequestWrapper(httpRequest), bytes);
    }

    private class MyHttpRequestWrapper extends HttpRequestWrapper {

        private MyHttpRequestWrapper(HttpRequest request) {
            super(request);
        }

        private URI uriWithKey(URI uri) throws URISyntaxException {
            // TODO: Could probably be done better
            return new URI(String.format("%s?key=%s", uri.toString(), apiKey));
        }

        @Override
        public URI getURI() {

            URI uri = getRequest().getURI();

            if (restrictedGet.contains(uri.toString()) || getRequest().getMethod().equals(HttpMethod.POST)) {
                try {
                    uri = uriWithKey(uri);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
            LOGGER.debug("Request: {} {}", getRequest().getMethod().name(), uri);
            return uri;
        }
    }
}
