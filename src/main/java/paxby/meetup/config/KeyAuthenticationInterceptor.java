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
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Configuration
public class KeyAuthenticationInterceptor implements ClientHttpRequestInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(KeyAuthenticationInterceptor.class);

    @Value("${meetup.api.key}")
    private String apiKey;

    private Set<String> authenticatedUris = new HashSet<>(Arrays.asList(new String[]{
            "http://api.meetup.com/2/member/self"
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
            return UriComponentsBuilder.fromUri(uri).queryParam("key", apiKey).build().toUri();
        }

        private URI maskKey(URI uri) {
            UriComponentsBuilder builder = UriComponentsBuilder.fromUri(uri);

            if (builder.build().getQueryParams().containsKey("key")) {
                builder.replaceQueryParam("key", "KEY");
            }
            return builder.build().toUri();
        }

        @Override
        public URI getURI() {
            URI uri = getRequest().getURI();
            HttpMethod method = getRequest().getMethod();

            if (!method.equals(HttpMethod.GET) || authenticatedUris.contains(uri.toString())) {
                try {
                    uri = uriWithKey(uri);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }

            LOGGER.debug("Request: {} {}", getRequest().getMethod().name(), maskKey(uri));
            return uri;
        }
    }
}
