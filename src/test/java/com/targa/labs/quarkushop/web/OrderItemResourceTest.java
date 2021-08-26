package com.targa.labs.quarkushop.web;

import com.targa.labs.quarkushop.utils.KeycloakRealmResource;
import com.targa.labs.quarkushop.utils.TestContainerResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static javax.ws.rs.core.Response.Status.NO_CONTENT;
import static javax.ws.rs.core.Response.Status.OK;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;
import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
@QuarkusTestResource(TestContainerResource.class)
@QuarkusTestResource(KeycloakRealmResource.class)
class OrderItemResourceTest {

    static String ADMIN_BEARER_TOKEN;
    static String USER_BEARER_TOKEN;

    @BeforeAll
    static void init() {
        ADMIN_BEARER_TOKEN = System.getProperty("quarkus-admin-access-token");
        USER_BEARER_TOKEN = System.getProperty("quarkus-test-access-token");
    }

    @Test
    void testFindByOrderId() {
        get("/order-items/order/1").then()
                .statusCode(UNAUTHORIZED.getStatusCode());
    }

    @Test
    void testFindById() {
        get("/order-items/1").then()
                .statusCode(UNAUTHORIZED.getStatusCode());
    }

    @Test
    void testCreate() {
        get("/orders/3").then()
                .statusCode(UNAUTHORIZED.getStatusCode());
    }

    @Test
    void testDelete() {
        get("/orders/1").then()
                .statusCode(UNAUTHORIZED.getStatusCode());
    }

    @Test
    void testFindByOrderIdWithAdminRole() {
        given().when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + ADMIN_BEARER_TOKEN)
                .get("/order-items/order/1")
                .then()
                .statusCode(OK.getStatusCode());
    }

    @Test
    void testFindByIdWithAdminRole() {
        var requestParams = new HashMap<>();
        requestParams.put("firstName", "Saul");
        requestParams.put("lastName", "Berenson");
        requestParams.put("email", "call.saul@mail.com");

        var newCustomerId = given()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + ADMIN_BEARER_TOKEN)
                .body(requestParams)
                .post("/customers")
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .jsonPath()
                .getInt("id");

        var newCartId = given().when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + ADMIN_BEARER_TOKEN)
                .post("/carts/customer/" + newCustomerId)
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .jsonPath()
                .getInt("id");

        requestParams.clear();

        var cart = new HashMap<>();
        cart.put("id", newCartId);

        requestParams.put("cart", cart);

        var address = new HashMap<>();
        address.put("address1", "413 Circle Drive");
        address.put("city", "Washington, DC");
        address.put("country", "US");
        address.put("postcode", "20004");

        requestParams.put("shipmentAddress", address);

        var orderResponse = given()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + ADMIN_BEARER_TOKEN)
                .body(requestParams)
                .post("/orders")
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .body()
                .jsonPath()
                .getMap("$");

        var newOrderId = (Integer) orderResponse.get("id");

        var orderItemRequestParams = new HashMap<>();
        orderItemRequestParams.put("quantity", 1);
        orderItemRequestParams.put("productId", 3);
        orderItemRequestParams.put("orderId", newOrderId);

        var orderItemResponse = given().header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + ADMIN_BEARER_TOKEN)
                .body(orderItemRequestParams)
                .post("/order-items/")
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .body()
                .jsonPath()
                .getMap("$");

        var orderItemId = (Integer) orderItemResponse.get("id");

        given().when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + ADMIN_BEARER_TOKEN)
                .get("/order-items/" + orderItemId)
                .then()
                .statusCode(OK.getStatusCode());

        given().when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + ADMIN_BEARER_TOKEN)
                .delete("/orders/" + newOrderId)
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

        given().when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + ADMIN_BEARER_TOKEN)
                .delete("/carts/" + newCartId)
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

        given().when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + ADMIN_BEARER_TOKEN)
                .delete("/customers/" + newCustomerId)
                .then()
                .statusCode(NO_CONTENT.getStatusCode());
    }

    @Test
    void testCreateWithAdminRole() {
        var totalPrice = given().when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + ADMIN_BEARER_TOKEN)
                .get("/orders/4")
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .jsonPath()
                .getDouble("totalPrice");

        var requestParams = new HashMap<>();
        requestParams.put("quantity", 1);
        requestParams.put("productId", 3);
        requestParams.put("orderId", 4);

        assertThat(totalPrice).isZero();

        given().header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + ADMIN_BEARER_TOKEN)
                .body(requestParams)
                .post("/order-items/")
                .then()
                .statusCode(OK.getStatusCode());

        var newTotalPrice = given().when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + ADMIN_BEARER_TOKEN)
                .get("/orders/4")
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .jsonPath()
                .getDouble("totalPrice");

        assertThat(newTotalPrice).isEqualTo(totalPrice + 1999);
    }

    @Test
    void testDeleteWithAdminRole() {
        var requestParams = new HashMap<>();
        requestParams.put("firstName", "Saul");
        requestParams.put("lastName", "Berenson");
        requestParams.put("email", "call.saul@mail.com");

        var newCustomerId = given()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + ADMIN_BEARER_TOKEN)
                .body(requestParams)
                .post("/customers")
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .jsonPath()
                .getInt("id");

        var newCartId = given().when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + ADMIN_BEARER_TOKEN)
                .post("/carts/customer/" + newCustomerId)
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .jsonPath()
                .getInt("id");

        requestParams.clear();

        var cart = new HashMap<>();
        cart.put("id", newCartId);

        requestParams.put("cart", cart);

        var address = new HashMap<>();
        address.put("address1", "413 Circle Drive");
        address.put("city", "Washington, DC");
        address.put("country", "US");
        address.put("postcode", "20004");

        requestParams.put("shipmentAddress", address);

        var orderResponse = given()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + ADMIN_BEARER_TOKEN)
                .body(requestParams)
                .post("/orders")
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .body()
                .jsonPath()
                .getMap("$");

        var newOrderId = (Integer) orderResponse.get("id");

        var orderItemRequestParams = new HashMap<>();
        orderItemRequestParams.put("quantity", 1);
        orderItemRequestParams.put("productId", 3);
        orderItemRequestParams.put("orderId", newOrderId);

        var orderItemResponse = given().header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + ADMIN_BEARER_TOKEN)
                .body(orderItemRequestParams)
                .post("/order-items/")
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .body()
                .jsonPath()
                .getMap("$");

        var orderItemId = (Integer) orderItemResponse.get("id");

        given().when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + ADMIN_BEARER_TOKEN)
                .get("/order-items/" + orderItemId)
                .then()
                .statusCode(OK.getStatusCode());

        given().when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + ADMIN_BEARER_TOKEN)
                .delete("/order-items/" + orderItemId)
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

        given().when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + ADMIN_BEARER_TOKEN)
                .delete("/orders/" + newOrderId)
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

        given().when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + ADMIN_BEARER_TOKEN)
                .delete("/carts/" + newCartId)
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

        given().when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + ADMIN_BEARER_TOKEN)
                .delete("/customers/" + newCustomerId)
                .then()
                .statusCode(NO_CONTENT.getStatusCode());
    }

    @Test
    void testFindByOrderIdWithUserRole() {
        given().when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + USER_BEARER_TOKEN)
                .get("/order-items/order/1")
                .then()
                .statusCode(OK.getStatusCode());
    }

    @Test
    void testFindByIdWithUserRole() {
        var requestParams = new HashMap<>();
        requestParams.put("firstName", "Saul");
        requestParams.put("lastName", "Berenson");
        requestParams.put("email", "call.saul@mail.com");

        var newCustomerId = given()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + ADMIN_BEARER_TOKEN)
                .body(requestParams)
                .post("/customers")
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .jsonPath()
                .getInt("id");

        var newCartId = given().when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + ADMIN_BEARER_TOKEN)
                .post("/carts/customer/" + newCustomerId)
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .jsonPath()
                .getInt("id");

        requestParams.clear();

        var cart = new HashMap<>();
        cart.put("id", newCartId);

        requestParams.put("cart", cart);

        var address = new HashMap<>();
        address.put("address1", "413 Circle Drive");
        address.put("city", "Washington, DC");
        address.put("country", "US");
        address.put("postcode", "20004");

        requestParams.put("shipmentAddress", address);

        var orderResponse = given()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + ADMIN_BEARER_TOKEN)
                .body(requestParams)
                .post("/orders")
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .body()
                .jsonPath()
                .getMap("$");

        var newOrderId = (Integer) orderResponse.get("id");

        var orderItemRequestParams = new HashMap<>();
        orderItemRequestParams.put("quantity", 1);
        orderItemRequestParams.put("productId", 3);
        orderItemRequestParams.put("orderId", newOrderId);

        var orderItemResponse = given().header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + ADMIN_BEARER_TOKEN)
                .body(orderItemRequestParams)
                .post("/order-items/")
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .body()
                .jsonPath()
                .getMap("$");

        var orderItemId = (Integer) orderItemResponse.get("id");

        given().when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + USER_BEARER_TOKEN)
                .get("/order-items/" + orderItemId)
                .then()
                .statusCode(OK.getStatusCode());

        given().when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + ADMIN_BEARER_TOKEN)
                .delete("/orders/" + newOrderId)
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

        given().when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + ADMIN_BEARER_TOKEN)
                .delete("/carts/" + newCartId)
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

        given().when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + ADMIN_BEARER_TOKEN)
                .delete("/customers/" + newCustomerId)
                .then()
                .statusCode(NO_CONTENT.getStatusCode());
    }

    @Test
    void testCreateWithUserRole() {
        var totalPrice = given().when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + USER_BEARER_TOKEN)
                .get("/orders/3")
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .jsonPath()
                .getDouble("totalPrice");

        var requestParams = new HashMap<>();
        requestParams.put("quantity", 1);
        requestParams.put("productId", 3);
        requestParams.put("orderId", 3);

        assertThat(totalPrice).isZero();

        given().header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + USER_BEARER_TOKEN)
                .body(requestParams)
                .post("/order-items/")
                .then()
                .statusCode(OK.getStatusCode());

        totalPrice = given().when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + USER_BEARER_TOKEN)
                .get("/orders/3")
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .jsonPath()
                .getDouble("totalPrice");

        assertThat(totalPrice).isEqualTo(1999);
    }

    @Test
    void testDeleteWithUserRole() {
        var requestParams = new HashMap<>();
        requestParams.put("firstName", "Saul");
        requestParams.put("lastName", "Berenson");
        requestParams.put("email", "call.saul@mail.com");

        var newCustomerId = given()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + ADMIN_BEARER_TOKEN)
                .body(requestParams)
                .post("/customers")
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .jsonPath()
                .getInt("id");

        var newCartId = given().when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + ADMIN_BEARER_TOKEN)
                .post("/carts/customer/" + newCustomerId)
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .jsonPath()
                .getInt("id");

        requestParams.clear();

        var cart = new HashMap<>();
        cart.put("id", newCartId);

        requestParams.put("cart", cart);

        var address = new HashMap<>();
        address.put("address1", "413 Circle Drive");
        address.put("city", "Washington, DC");
        address.put("country", "US");
        address.put("postcode", "20004");

        requestParams.put("shipmentAddress", address);

        var orderResponse = given()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + ADMIN_BEARER_TOKEN)
                .body(requestParams)
                .post("/orders")
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .body()
                .jsonPath()
                .getMap("$");

        var newOrderId = (Integer) orderResponse.get("id");

        var orderItemRequestParams = new HashMap<>();
        orderItemRequestParams.put("quantity", 1);
        orderItemRequestParams.put("productId", 3);
        orderItemRequestParams.put("orderId", newOrderId);

        var orderItemResponse = given().header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + ADMIN_BEARER_TOKEN)
                .body(orderItemRequestParams)
                .post("/order-items/")
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .body()
                .jsonPath()
                .getMap("$");

        var orderItemId = (Integer) orderItemResponse.get("id");

        given().when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + ADMIN_BEARER_TOKEN)
                .get("/order-items/" + orderItemId)
                .then()
                .statusCode(OK.getStatusCode());

        given().when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + USER_BEARER_TOKEN)
                .delete("/order-items/" + orderItemId)
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

        given().when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + ADMIN_BEARER_TOKEN)
                .delete("/orders/" + newOrderId)
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

        given().when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + ADMIN_BEARER_TOKEN)
                .delete("/carts/" + newCartId)
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

        given().when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + ADMIN_BEARER_TOKEN)
                .delete("/customers/" + newCustomerId)
                .then()
                .statusCode(NO_CONTENT.getStatusCode());
    }
}
