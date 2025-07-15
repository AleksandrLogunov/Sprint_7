package praktikum.tests;

import praktikum.model.Order;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import java.util.List;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

public class OrderCreateTest {

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
    }

    static Stream<List<String>> colorProvider() {
        return Stream.of(
                List.of("BLACK"),
                List.of("GREY"),
                List.of("BLACK", "GREY"),
                List.of() // без цвета
        );
    }

    @ParameterizedTest(name = "Создание заказа с цветом: {0}")
    @MethodSource("colorProvider")
    @DisplayName("Создание заказа с разными вариантами цвета")
    void testCreateOrder(List<String> colors) {
        Order order = Order.defaultOrder(colors);
        createOrder(order)
                .then()
                .statusCode(201)
                .body("track", notNullValue());
    }

    @Step("Создание заказа")
    private io.restassured.response.Response createOrder(Order order) {
        return given()
                .contentType("application/json")
                .body(order)
                .when()
                .post("/api/v1/orders");
    }
}