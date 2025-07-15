package praktikum.tests;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import praktikum.model.Courier;
import praktikum.model.CourierLogin;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class CourierCreateTest {

    private Courier testCourier;

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
        testCourier = Courier.random();
    }

    @AfterEach
    public void tearDown() {
        if (testCourier != null) {
            Integer id = loginCourier(testCourier.login, testCourier.password);
            if (id != null) {
                deleteCourier(id);
            }
        }
    }

    @Test
    @DisplayName("Успешное создание курьера")
    void createCourierSuccess() {
        createCourier(testCourier)
                .then()
                .statusCode(201)
                .body("ok", equalTo(true));
    }

    @Test
    @DisplayName("Ошибка при создании двух одинаковых курьеров")
    void createDuplicateCourierFails() {
        createCourier(testCourier)
                .then()
                .statusCode(201);

        createCourier(testCourier)
                .then()
                .statusCode(409)
                .body("message", equalTo("Этот логин уже используется. Попробуйте другой."));
    }

    @Test
    @DisplayName("Создание курьера без логина")
    void createCourierWithoutLogin() {
        Courier courier = new Courier(null, "1234", "Петя");
        createCourier(courier)
                .then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Создание курьера без пароля")
    void createCourierWithoutPassword() {
        Courier courier = new Courier("login" + System.currentTimeMillis(), null, "Евпатий");
        createCourier(courier)
                .then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }


    @Step("Создание курьера")
    public Response createCourier(Courier courier) {
        return given()
                .contentType("application/json")
                .body(courier)
                .when()
                .post("/api/v1/courier");
    }

    @Step("Авторизация курьера")
    public Response loginCourierResponse(String login, String password) {
        CourierLogin loginData = new CourierLogin(login, password);
        return given()
                .contentType("application/json")
                .body(loginData)
                .when()
                .post("/api/v1/courier/login");
    }

    @Step("Получение ID курьера при авторизации")
    public Integer loginCourier(String login, String password) {
        Response response = loginCourierResponse(login, password);
        if (response.statusCode() == 200) {
            return response.then().extract().path("id");
        }
        return null;
    }

    @Step("Удаление курьера по ID")
    public void deleteCourier(int id) {
        given()
                .when()
                .delete("/api/v1/courier/" + id)
                .then()
                .statusCode(200);
    }
}