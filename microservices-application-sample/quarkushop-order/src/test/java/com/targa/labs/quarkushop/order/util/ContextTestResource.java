package com.targa.labs.quarkushop.order.util;

import com.targa.labs.quarkushop.commons.security.TokenService;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.junit.ClassRule;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class ContextTestResource implements QuarkusTestResourceLifecycleManager {

    @ClassRule
    public static DockerComposeContainer ECOSYSTEM = new DockerComposeContainer(
            new File("src/main/docker/context-test.yml"))
            .withExposedService("quarkushop-product_1",
                    8080,
                    Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(30)))
            .withExposedService("keycloak_1",
                    9080,
                    Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(30)));


    @Override
    public Map<String, String> start() {
        ECOSYSTEM.start();

        String jwtIssuerUrl = String.format("http://%s:%s/auth/realms/quarkus-realm",
                ECOSYSTEM.getServiceHost("keycloak_1", 9080),
                ECOSYSTEM.getServicePort("keycloak_1", 9080)
        );

        TokenService tokenService = new TokenService();
        Map<String, String> config = new HashMap<>();

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

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        config.put("mp.jwt.verify.publickey.location", jwtIssuerUrl + "/protocol/openid-connect/certs");
        config.put("mp.jwt.verify.issuer", jwtIssuerUrl);

        String productServiceUrl = String.format("http://%s:%s/api",
                ECOSYSTEM.getServiceHost("quarkushop-product_1", 8080),
                ECOSYSTEM.getServicePort("quarkushop-product_1", 8080)
        );

        config.put("product-service.url", productServiceUrl);

        return config;
    }

    @Override
    public void stop() {
        ECOSYSTEM.stop();
    }
}