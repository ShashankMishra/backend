package com.qrust;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
class LinqrIntegrationTest {
    @BeforeAll
    static void setup() {
        RestAssured.basePath = "/";
    }

    @Test
    void testPublicQrEndpoint_noAuthRequired() {
        // This should work regardless of auth.enabled
        given()
            .when().get("/qr/some-public-token")
            .then()
            .statusCode(anyOf(is(200), is(404))); // 200 if found, 404 if not
    }

    @Test
    void testProfileEndpoint_noAuthRequiredWhenDisabled() {
        // With auth.enabled=false, this should not require a JWT
        given()
            .contentType(ContentType.JSON)
            .when().get("/profile")
            .then()
            .statusCode(anyOf(is(200), is(204), is(404)));
    }
}

