package com.targa.labs.quarkushop.commons.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import io.quarkus.security.UnauthorizedException;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static java.net.http.HttpRequest.BodyPublishers;

@Slf4j
@RequestScoped
public class TokenService {

    private static final String TOKENS_REQUESTS_TIMER = "tokensRequestsTimer";
    private static final String TOKENS_REQUESTS_COUNTER = "tokensRequestsCounter";

    @Inject
    MeterRegistry registry;

    @ConfigProperty(name = "mp.jwt.verify.issuer", defaultValue = "undefined")
    Provider<String> jwtIssuerUrlProvider;

    @ConfigProperty(name = "keycloak.credentials.client-id", defaultValue = "undefined")
    Provider<String> clientIdProvider;

    @PostConstruct
    public void init() {
        registry.timer(TOKENS_REQUESTS_TIMER, Tags.empty());
        registry.counter(TOKENS_REQUESTS_COUNTER, Tags.empty());
    }

    public String getAccessToken(String userName, String password) {
        var timer = registry.timer(TOKENS_REQUESTS_TIMER);
        return timer.record(() -> {
            var accessToken = "";
            try {
                accessToken = getAccessToken(jwtIssuerUrlProvider.get(),
                        userName, password, clientIdProvider.get(), null);
                registry.counter(TOKENS_REQUESTS_COUNTER).increment();
            } catch (IOException e) {
                log.error(e.getMessage());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Cannot get the access_token");
            }
            return accessToken;
        });
    }

    public String getAccessToken(String jwtIssuerUrl, String userName, String password,
                                 String clientId, String clientSecret) throws IOException, InterruptedException {
        String tokenizer = jwtIssuerUrl + "/protocol/openid-connect/token";

        String requestBody = "username=" + userName + "&password=" + password
                + "&grant_type=password&client_id=" + clientId;

        if (clientSecret != null) {
            requestBody += "&client_secret=" + clientSecret;
        }

        HttpClient client = HttpClient.newBuilder().build();
        var request = HttpRequest.newBuilder()
                .uri(URI.create(tokenizer))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        var accessToken = "";

        if (response.statusCode() == 200) {
            var mapper = new ObjectMapper();
            try {
                accessToken = mapper.readTree(response.body()).get("access_token").textValue();
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        } else {
            log.error("UnauthorizedException");
            throw new UnauthorizedException();
        }

        return accessToken;
    }
}
