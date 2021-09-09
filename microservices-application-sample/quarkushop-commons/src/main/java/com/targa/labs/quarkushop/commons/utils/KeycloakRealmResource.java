package com.targa.labs.quarkushop.commons.utils;

import com.targa.labs.quarkushop.commons.security.TokenService;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.ClassRule;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class KeycloakRealmResource implements QuarkusTestResourceLifecycleManager {
    private static final String KEYCLOAK_1 = "keycloak_1";

    @ClassRule
    public static final DockerComposeContainer KEYCLOAK = new DockerComposeContainer(
            new File("src/main/docker/keycloak-test.yml"))
            .withExposedService(KEYCLOAK_1,
                    9080,
                    Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(30)));

    @Override
    public Map<String, String> start() {
        KEYCLOAK.start();

        var jwtIssuerUrl = String.format("http://%s:%s/auth/realms/quarkus-realm",
                KEYCLOAK.getServiceHost(KEYCLOAK_1, 9080),
                KEYCLOAK.getServicePort(KEYCLOAK_1, 9080)
        );

        var tokenService = new TokenService();
        var config = new HashMap<String, String>();

        try {

            String adminAccessToken = tokenService.getAccessToken(jwtIssuerUrl,
                    "admin",
                    "test",
                    "quarkus-client",
                    "mysecret"
            );

            String testAccessToken = tokenService.getAccessToken(jwtIssuerUrl,
                    "test",
                    "test",
                    "quarkus-client",
                    "mysecret"
            );

            config.put("quarkus-admin-access-token", adminAccessToken);
            config.put("quarkus-test-access-token", testAccessToken);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Cannot get the access_token");
        }

        config.put("mp.jwt.verify.publickey.location", jwtIssuerUrl + "/protocol/openid-connect/certs");
        config.put("mp.jwt.verify.issuer", jwtIssuerUrl);

        return config;
    }

    @Override
    public void stop() {
        KEYCLOAK.stop();
    }
}
