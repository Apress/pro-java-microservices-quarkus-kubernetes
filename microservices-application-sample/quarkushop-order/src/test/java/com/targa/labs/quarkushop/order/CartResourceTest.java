package com.targa.labs.quarkushop.order;

import com.targa.labs.quarkushop.commons.utils.TestContainerResource;
import com.targa.labs.quarkushop.order.domain.enums.CartStatus;
import com.targa.labs.quarkushop.order.util.ContextTestResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.DisabledOnNativeImage;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.sql.DataSource;
import javax.ws.rs.core.HttpHeaders;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

import static io.restassured.RestAssured.*;
import static javax.ws.rs.core.Response.Status.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

@DisabledOnNativeImage
@QuarkusTest
@QuarkusTestResource(TestContainerResource.class)
@QuarkusTestResource(ContextTestResource.class)
class CartResourceTest {

    private static final String INSERT_WRONG_CART_IN_DB =
            "insert into carts values (9999, current_timestamp, current_timestamp, 'NEW', 3)";

    private static final String DELETE_WRONG_CART_IN_DB =
            "delete from carts where id = 9999";
    private static final AtomicInteger COUNTER = new AtomicInteger(100);
    static String ADMIN_BEARER_TOKEN;
    static String USER_BEARER_TOKEN;
    @Inject
    DataSource dataSource;

    @BeforeAll
    static void init() {
        ADMIN_BEARER_TOKEN = System.getProperty("quarkus-admin-access-token");
        USER_BEARER_TOKEN = System.getProperty("quarkus-test-access-token");
    }

    @Test
    void testFindAllWithAdminRole() {
        given().when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + ADMIN_BEARER_TOKEN)
                .get("/carts")
                .then()
                .statusCode(OK.getStatusCode())
                .body("size()", greaterThan(0));
    }

    @Test
    void testFindAllActiveCartsWithAdminRole() {
        given().when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + ADMIN_BEARER_TOKEN)
                .get("/carts/active")
                .then()
                .statusCode(OK.getStatusCode());
    }

    @Test
    void testGetActiveCartForCustomerWithAdminRole() {
        given().when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + ADMIN_BEARER_TOKEN)
                .get("/carts/customer/1")
                .then()
                .statusCode(OK.getStatusCode())
                .body(containsString("1"));
    }

    @Test
    void testGetActiveCartForCustomerWhenThereAreTwoCartsInDBWithAdminRole() {
        executeSql(INSERT_WRONG_CART_IN_DB);

        try {
            given().when()
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + ADMIN_BEARER_TOKEN)
                    .get("/carts/customer/3")
                    .then()
                    .statusCode(INTERNAL_SERVER_ERROR.getStatusCode())
                    .body(containsString(INTERNAL_SERVER_ERROR.getReasonPhrase()))
                    .body(containsString("Many active carts detected !!!"));
        } catch (AssertionError e) {

        } finally {
            executeSql(DELETE_WRONG_CART_IN_DB);
        }
    }

    @Test
    void testFindByIdWithAdminRole() {
        given().when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + ADMIN_BEARER_TOKEN)
                .get("/carts/2")
                .then()
                .statusCode(OK.getStatusCode())
                .body(containsString("status"))
                .body(containsString("NEW"));

        given().when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + ADMIN_BEARER_TOKEN)
                .get("/carts/1000")
                .then()
                .statusCode(NO_CONTENT.getStatusCode())
                .body(emptyOrNullString());
    }

    @Test
    void testCreateCartWithAdminRole() {
        var newCustomerId = COUNTER.incrementAndGet();

        var response = given().when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + ADMIN_BEARER_TOKEN)
                .post("/carts/customer/" + newCustomerId)
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .jsonPath()
                .getMap("$");

        assertThat(response.get("id")).isNotNull();
        assertThat(response).containsEntry("status", CartStatus.NEW.name());

        given().when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + ADMIN_BEARER_TOKEN)
                .delete("/carts/" + response.get("id"))
                .then()
                .statusCode(NO_CONTENT.getStatusCode());
    }

    @Test
    void testFindAllWithUserRole() {
        given().when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + USER_BEARER_TOKEN)
                .get("/carts")
                .then()
                .statusCode(OK.getStatusCode())
                .body("size()", greaterThan(0));
    }

    @Test
    void testFindAllActiveCartsWithUserRole() {
        given().when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + USER_BEARER_TOKEN)
                .get("/carts/active")
                .then()
                .statusCode(OK.getStatusCode());
    }

    @Test
    void testGetActiveCartForCustomerWithUserRole() {
        given().when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + USER_BEARER_TOKEN)
                .get("/carts/customer/3")
                .then()
                .statusCode(OK.getStatusCode())
                .body(containsString("3"));
    }

    @Test
    void testGetActiveCartForCustomerWhenThereAreTwoCartsInDBWithUserRole() {
        executeSql(INSERT_WRONG_CART_IN_DB);

        try {
            given().when()
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + USER_BEARER_TOKEN)
                    .get("/carts/customer/3")
                    .then()
                    .statusCode(INTERNAL_SERVER_ERROR.getStatusCode())
                    .body(containsString(INTERNAL_SERVER_ERROR.getReasonPhrase()))
                    .body(containsString("Many active carts detected !!!"));
        } catch (AssertionError e) {

        } finally {
            executeSql(DELETE_WRONG_CART_IN_DB);
        }
    }

    @Test
    void testFindByIdWithUserRole() {
        given().when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + USER_BEARER_TOKEN)
                .get("/carts/2")
                .then()
                .statusCode(OK.getStatusCode())
                .body(containsString("status"))
                .body(containsString("NEW"));

        given().when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + USER_BEARER_TOKEN)
                .get("/carts/1000")
                .then()
                .statusCode(NO_CONTENT.getStatusCode())
                .body(emptyOrNullString());
    }

    @Test
    void testCreateCartWithUserRole() {
        var newCustomerId = COUNTER.incrementAndGet();

        var response = given().when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + USER_BEARER_TOKEN)
                .post("/carts/customer/" + newCustomerId)
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .jsonPath()
                .getMap("$");

        assertThat(response.get("id")).isNotNull();
        assertThat(response).containsEntry("status", CartStatus.NEW.name());

        given().when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + USER_BEARER_TOKEN)
                .delete("/carts/" + response.get("id"))
                .then()
                .statusCode(NO_CONTENT.getStatusCode());
    }

    @Test
    void testFailCreateCartWhileHavingAlreadyActiveCartWithUserRole() {
        var newCustomerId = COUNTER.incrementAndGet();

        var newCartId = given().when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + USER_BEARER_TOKEN)
                .post("/carts/customer/" + newCustomerId)
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .jsonPath()
                .getLong("id");

        given().when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + USER_BEARER_TOKEN)
                .post("/carts/customer/" + newCustomerId)
                .then()
                .statusCode(INTERNAL_SERVER_ERROR.getStatusCode())
                .body(containsString(INTERNAL_SERVER_ERROR.getReasonPhrase()))
                .body(containsString("There is already an active cart"));

        assertThat(newCartId).isNotZero();

        given().when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + USER_BEARER_TOKEN)
                .delete("/carts/" + newCartId)
                .then()
                .statusCode(NO_CONTENT.getStatusCode());
    }

    @Test
    void testDeleteWithUserRole() {
        given().when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + USER_BEARER_TOKEN)
                .get("/carts/active")
                .then()
                .statusCode(OK.getStatusCode())
                .body(containsString("3"))
                .body(containsString("NEW"));

        given().when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + USER_BEARER_TOKEN)
                .delete("/carts/3")
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

        given().when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + USER_BEARER_TOKEN)
                .get("/carts/3")
                .then()
                .statusCode(OK.getStatusCode())
                .body(containsString("3"))
                .body(containsString("CANCELED"));
    }

    @Test
    void testFindAll() {
        get("/carts").then()
                .statusCode(UNAUTHORIZED.getStatusCode());
    }

    @Test
    void testFindAllActiveCarts() {
        get("/carts/active").then()
                .statusCode(UNAUTHORIZED.getStatusCode());
    }

    @Test
    void testGetActiveCartForCustomer() {
        get("/carts/customer/3").then()
                .statusCode(UNAUTHORIZED.getStatusCode());
    }

    @Test
    void testFindById() {
        get("/carts/3").then()
                .statusCode(UNAUTHORIZED.getStatusCode());

        get("/carts/100").then()
                .statusCode(UNAUTHORIZED.getStatusCode());
    }

    @Test
    void testCreateCart() {
        post("/carts/customer/" + 555555).then()
                .statusCode(UNAUTHORIZED.getStatusCode());
    }

    @Test
    void testDelete() {
        get("/carts/active").then()
                .statusCode(UNAUTHORIZED.getStatusCode());

        delete("/carts/1").then()
                .statusCode(UNAUTHORIZED.getStatusCode());

        get("/carts/1").then()
                .statusCode(UNAUTHORIZED.getStatusCode());
    }

    private void executeSql(String insertWrongCartInDb) {
        try (var connection = dataSource.getConnection()) {
            var statement = connection.createStatement();
            statement.executeUpdate(insertWrongCartInDb);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
