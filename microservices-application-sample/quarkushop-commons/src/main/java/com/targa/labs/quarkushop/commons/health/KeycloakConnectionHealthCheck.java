package com.targa.labs.quarkushop.commons.health;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Provider;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Slf4j
@Liveness
@ApplicationScoped
public class KeycloakConnectionHealthCheck implements HealthCheck {

    @ConfigProperty(name = "mp.jwt.verify.publickey.location", defaultValue = "false")
    Provider<String> keycloakUrl;

    @Override
    public HealthCheckResponse call() {

        var responseBuilder = HealthCheckResponse.named("Keycloak connection health check");

        try {
            keycloakConnectionVerification();
            responseBuilder.up();
        } catch (IllegalStateException e) {
            // cannot access keycloak
            responseBuilder.down().withData("error", e.getMessage());
        }

        return responseBuilder.build();
    }

    private void keycloakConnectionVerification() {
        var httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(3000))
                .build();

        var request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(keycloakUrl.get()))
                .build();

        HttpResponse<String> response = null;

        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            log.error("IOException", e);
        } catch (InterruptedException e) {
            log.error("InterruptedException", e);
            Thread.currentThread().interrupt();
        }

        if (response == null || response.statusCode() != 200) {
            throw new IllegalStateException("Cannot contact Keycloak");
        }
    }
}
