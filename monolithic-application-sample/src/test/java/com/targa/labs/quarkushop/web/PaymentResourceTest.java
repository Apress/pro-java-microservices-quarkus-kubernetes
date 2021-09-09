package com.targa.labs.quarkushop.web;

import com.targa.labs.quarkushop.domain.enums.PaymentStatus;
import com.targa.labs.quarkushop.utils.KeycloakRealmResource;
import com.targa.labs.quarkushop.utils.TestContainerResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;

import static io.restassured.RestAssured.delete;
import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static javax.ws.rs.core.Response.Status.ACCEPTED;
import static javax.ws.rs.core.Response.Status.FORBIDDEN;
import static javax.ws.rs.core.Response.Status.NO_CONTENT;
import static javax.ws.rs.core.Response.Status.OK;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
@QuarkusTestResource(TestContainerResource.class)
@QuarkusTestResource(KeycloakRealmResource.class)
class PaymentResourceTest {

    static String ADMIN_BEARER_TOKEN;
    static String USER_BEARER_TOKEN;

    @BeforeAll
    static void init() {
        ADMIN_BEARER_TOKEN = System.getProperty("quarkus-admin-access-token");
        USER_BEARER_TOKEN = System.getProperty("quarkus-test-access-token");
    }

    @Test
    void testFindAll() {
        get("/payments").then()
                .statusCode(UNAUTHORIZED.getStatusCode());
    }

    @Test
    void testFindById() {
        get("/payments/2").then()
                .statusCode(UNAUTHORIZED.getStatusCode());
    }

    @Test
    void testCreate() {
        var requestParams = new HashMap<>();

        requestParams.put("orderId", 4);
        requestParams.put("paypalPaymentId", "anotherPaymentId");
        requestParams.put("status", PaymentStatus.PENDING);

        given().header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .body(requestParams)
                .post("/payments")
                .then()
                .statusCode(UNAUTHORIZED.getStatusCode());
    }

    @Test
    void testDelete() {
        delete("/payments/1").then()
                .statusCode(UNAUTHORIZED.getStatusCode());
    }

    @Test
    void testFindByRangeMax() {
        get("/payments/price/800").then()
                .statusCode(UNAUTHORIZED.getStatusCode());
    }

    @Test
    void testFindAllWithAdminRole() {
        var payments = given().when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + ADMIN_BEARER_TOKEN)
                .get("/payments")
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .jsonPath()
                .getList("$");

        assertNotNull(payments);
    }

    @Test
    void testFindByIdWithAdminRole() {
        var response = given().when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + ADMIN_BEARER_TOKEN)
                .get("/payments/4")
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .jsonPath()
                .getMap("$");

        assertEquals(4, response.get("id"));
        assertEquals(ACCEPTED.name(), response.get("status"));
        assertEquals("paymentId", response.get("paypalPaymentId"));
        assertEquals(5, response.get("orderId"));
    }

    @Test
    void testCreateWithAdminRole() {
        var requestParams = new HashMap<>();

        requestParams.put("orderId", 4);
        requestParams.put("paypalPaymentId", "anotherPaymentId");
        requestParams.put("status", PaymentStatus.PENDING);

        var response = given()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + ADMIN_BEARER_TOKEN)
                .body(requestParams)
                .post("/payments")
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .jsonPath()
                .getMap("$");

        var createdPaymentId = (Integer) response.get("id");
        assertThat(createdPaymentId).isNotZero();
        assertThat(response).containsEntry("orderId", 4)
                .containsEntry("paypalPaymentId", "anotherPaymentId")
                .containsEntry("status", PaymentStatus.PENDING.name());

        given().when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + ADMIN_BEARER_TOKEN)
                .delete("/payments/" + createdPaymentId)
                .then()
                .statusCode(NO_CONTENT.getStatusCode());
    }

    @Test
    void testDeleteWithAdminRole() {
        var requestParams = new HashMap<>();

        requestParams.put("orderId", 4);
        requestParams.put("paypalPaymentId", "anotherPaymentId");
        requestParams.put("status", PaymentStatus.PENDING);

        var createdPaymentId = given()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + ADMIN_BEARER_TOKEN)
                .body(requestParams)
                .post("/payments")
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .jsonPath()
                .getLong("id");

        given().when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + ADMIN_BEARER_TOKEN)
                .delete("/payments/" + createdPaymentId)
                .then()
                .statusCode(NO_CONTENT.getStatusCode());
    }

    @Test
    void testFindByRangeMaxWithAdminRole() {
        given().when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + ADMIN_BEARER_TOKEN)
                .get("/payments/price/800")
                .then()
                .statusCode(OK.getStatusCode())
                .body("size()", is(greaterThanOrEqualTo(0)));
    }

    @Test
    void testFindAllWithUserRole() {
        given().when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + USER_BEARER_TOKEN)
                .get("/payments")
                .then()
                .statusCode(FORBIDDEN.getStatusCode());
    }

    @Test
    void testFindByIdWithUserRole() {
        var response = given().when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + USER_BEARER_TOKEN)
                .get("/payments/4")
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .jsonPath()
                .getMap("$");

        assertEquals(4, response.get("id"));
        assertEquals(ACCEPTED.name(), response.get("status"));
        assertEquals("paymentId", response.get("paypalPaymentId"));
        assertEquals(5, response.get("orderId"));
    }

    @Test
    void testCreateWithUserRole() {
        var requestParams = new HashMap<>();

        requestParams.put("orderId", 5);
        requestParams.put("paypalPaymentId", "anotherPaymentId");
        requestParams.put("status", PaymentStatus.PENDING);

        var response = given()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + USER_BEARER_TOKEN)
                .body(requestParams)
                .post("/payments")
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .jsonPath()
                .getMap("$");

        var createdPaymentId = (Integer) response.get("id");
        assertThat(createdPaymentId).isNotZero();
        assertThat(response).containsEntry("orderId", 5)
                .containsEntry("paypalPaymentId", "anotherPaymentId")
                .containsEntry("status", PaymentStatus.PENDING.name());

        given().when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + ADMIN_BEARER_TOKEN)
                .delete("/payments/" + createdPaymentId)
                .then()
                .statusCode(NO_CONTENT.getStatusCode());
    }

    @Test
    void testDeleteWithUserRole() {
        var requestParams = new HashMap<>();

        requestParams.put("orderId", 5);
        requestParams.put("paypalPaymentId", "anotherPaymentId");
        requestParams.put("status", PaymentStatus.PENDING);

        var createdPaymentId = given()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + USER_BEARER_TOKEN)
                .body(requestParams)
                .post("/payments")
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .jsonPath()
                .getLong("id");

        given().when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + USER_BEARER_TOKEN)
                .delete("/payments/" + createdPaymentId)
                .then()
                .statusCode(FORBIDDEN.getStatusCode());

        given().when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + ADMIN_BEARER_TOKEN)
                .delete("/payments/" + createdPaymentId)
                .then()
                .statusCode(NO_CONTENT.getStatusCode());
    }

    @Test
    void testFindByRangeMaxWithUserRole() {
        given().when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + USER_BEARER_TOKEN)
                .get("/payments/price/800")
                .then()
                .statusCode(OK.getStatusCode())
                .body("size()", is(greaterThanOrEqualTo(0)));
    }
}
