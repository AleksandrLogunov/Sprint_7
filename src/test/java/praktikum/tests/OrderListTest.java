package praktikum.tests;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

public class OrderListTest {

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
    }

    @Test
    @DisplayName("Проверка получения списка заказов")
    void testGetOrderList() {
        getOrderList()
                .then()
                .statusCode(200)
                .body("orders", notNullValue());
    }

    @Step("Получение списка заказов")
    private io.restassured.response.Response getOrderList() {
        return given()
                .when()
                .get("/api/v1/orders");
    }
}
