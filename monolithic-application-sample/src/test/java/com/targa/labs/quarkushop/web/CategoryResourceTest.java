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
import static javax.ws.rs.core.Response.Status.FORBIDDEN;
import static javax.ws.rs.core.Response.Status.NO_CONTENT;
import static javax.ws.rs.core.Response.Status.OK;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
@QuarkusTestResource(TestContainerResource.class)
@QuarkusTestResource(KeycloakRealmResource.class)
class CategoryResourceTest {

    static String ADMIN_BEARER_TOKEN;
    static String USER_BEARER_TOKEN;

    @BeforeAll
    static void init() {
        ADMIN_BEARER_TOKEN = System.getProperty("quarkus-admin-access-token");
        USER_BEARER_TOKEN = System.getProperty("quarkus-test-access-token");
    }

    @Test
    void testFindAll() {
        get("/categories").then()
                .statusCode(OK.getStatusCode())
                .body("size()", is(2))
                .body(containsString("Phones & Smartphones"))
                .body(containsString("Mobile"))
                .body(containsString("Computers and Laptops"))
                .body(containsString("PC"));
    }

    @Test
    void testFindById() {
        get("/categories/1").then()
                .statusCode(OK.getStatusCode())
                .body(containsString("Phones & Smartphones"))
                .body(containsString("Mobile"));
    }

    @Test
    void testFindProductsByCategoryId() {
        get("/categories/1/products").then()
                .statusCode(OK.getStatusCode())
                .body(containsString("categoryId"))
                .body(containsString("description"))
                .body(containsString("id"))
                .body(containsString("name"))
                .body(containsString("price"))
                .body(containsString("reviews"))
                .body(containsString("salesCounter"))
                .body(containsString("status"));
    }

    @Test
    void testCreate() {
        var requestParams = new HashMap<>();
        requestParams.put("name", "Cars");
        requestParams.put("description", "New and used cars");

        given().header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .body(requestParams)
                .post("/categories")
                .then()
                .statusCode(UNAUTHORIZED.getStatusCode());
    }

    @Test
    void testDelete() {
        var requestParams = new HashMap<>();
        requestParams.put("name", "Home");
        requestParams.put("description", "New and old homes");

        given().header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .body(requestParams)
                .post("/categories")
                .then()
                .statusCode(UNAUTHORIZED.getStatusCode());
    }

    @Test
    void testFindAllWithAdminRole() {
        given().when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + ADMIN_BEARER_TOKEN)
                .get("/categories")
                .then()
                .statusCode(OK.getStatusCode())
                .body("size()", is(2))
                .body(containsString("Phones & Smartphones"))
                .body(containsString("Mobile"))
                .body(containsString("Computers and Laptops"))
                .body(containsString("PC"));
    }

    @Test
    void testFindByIdWithAdminRole() {
        given().when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + ADMIN_BEARER_TOKEN)
                .get("/categories/1")
                .then()
                .statusCode(OK.getStatusCode())
                .body(containsString("Phones & Smartphones"))
                .body(containsString("Mobile"));
    }

    @Test
    void testFindProductsByCategoryIdWithAdminRole() {
        given().when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + ADMIN_BEARER_TOKEN)
                .get("/categories/1/products")
                .then()
                .statusCode(OK.getStatusCode())
                .body(containsString("categoryId"))
                .body(containsString("description"))
                .body(containsString("id"))
                .body(containsString("name"))
                .body(containsString("price"))
                .body(containsString("reviews"))
                .body(containsString("salesCounter"))
                .body(containsString("status"));
    }

    @Test
    void testCreateWithAdminRole() {
        var requestParams = new HashMap<>();
        requestParams.put("name", "Cars");
        requestParams.put("description", "New and used cars");

        var response = given()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + ADMIN_BEARER_TOKEN)
                .body(requestParams)
                .post("/categories")
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .jsonPath()
                .getMap("$");

        assertNotNull(response.get("id"));

        var newProductID = (Integer) response.get("id");

        given().when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + ADMIN_BEARER_TOKEN)
                .get("/categories/" + newProductID)
                .then()
                .statusCode(OK.getStatusCode())
                .body(containsString("Cars"))
                .body(containsString("New and used cars"));

        given().when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + ADMIN_BEARER_TOKEN)
                .delete("/categories/" + newProductID)
                .then()
                .statusCode(NO_CONTENT.getStatusCode());
    }

    @Test
    void testDeleteWithAdminRole() {
        var requestParams = new HashMap<>();
        requestParams.put("name", "Home");
        requestParams.put("description", "New and old homes");

        var response = given()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + ADMIN_BEARER_TOKEN)
                .body(requestParams)
                .post("/categories")
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .jsonPath()
                .getMap("$");

        assertNotNull(response.get("id"));

        var newProductID = (Integer) response.get("id");

        given().when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + ADMIN_BEARER_TOKEN)
                .get("/categories/" + newProductID)
                .then()
                .statusCode(OK.getStatusCode())
                .body(containsString("Home"))
                .body(containsString("New and old homes"));

        given().when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + ADMIN_BEARER_TOKEN)
                .delete("/categories/" + newProductID)
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

        given().when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + ADMIN_BEARER_TOKEN)
                .get("/categories/" + newProductID)
                .then()
                .statusCode(NO_CONTENT.getStatusCode());
    }

    @Test
    void testFindAllWithUserRole() {
        given().when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + USER_BEARER_TOKEN)
                .get("/categories")
                .then()
                .statusCode(OK.getStatusCode())
                .body("size()", is(2))
                .body(containsString("Phones & Smartphones"))
                .body(containsString("Mobile"))
                .body(containsString("Computers and Laptops"))
                .body(containsString("PC"));
    }

    @Test
    void testFindByIdWithUserRole() {
        given().when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + USER_BEARER_TOKEN)
                .get("/categories/1")
                .then()
                .statusCode(OK.getStatusCode())
                .body(containsString("Phones & Smartphones"))
                .body(containsString("Mobile"));
    }

    @Test
    void testFindProductsByCategoryIdWithUserRole() {
        given().when()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + USER_BEARER_TOKEN)
                .get("/categories/1/products")
                .then()
                .statusCode(OK.getStatusCode())
                .body(containsString("categoryId"))
                .body(containsString("description"))
                .body(containsString("id"))
                .body(containsString("name"))
                .body(containsString("price"))
                .body(containsString("reviews"))
                .body(containsString("salesCounter"))
                .body(containsString("status"));
    }

    @Test
    void testCreateWithUserRole() {
        var requestParams = new HashMap<>();
        requestParams.put("name", "Cars");
        requestParams.put("description", "New and used cars");

        given()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + USER_BEARER_TOKEN)
                .body(requestParams)
                .post("/categories")
                .then()
                .statusCode(FORBIDDEN.getStatusCode());
    }

    @Test
    void testDeleteWithUserRole() {
        var requestParams = new HashMap<>();
        requestParams.put("name", "Home");
        requestParams.put("description", "New and old homes");

        given()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + USER_BEARER_TOKEN)
                .body(requestParams)
                .post("/categories")
                .then()
                .statusCode(FORBIDDEN.getStatusCode());
    }
}
