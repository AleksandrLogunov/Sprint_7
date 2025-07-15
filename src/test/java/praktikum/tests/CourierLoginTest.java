package praktikum.tests;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import praktikum.model.Courier;
import praktikum.model.CourierLogin;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class CourierLoginTest {

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
    @DisplayName("Успешная авторизация курьера")
    void courierCanLogin() {
        createCourier(testCourier).then().statusCode(201);

        loginCourierResponse(testCourier.login, testCourier.password)
                .then()
                .statusCode(200)
                .body("id", notNullValue());
    }

    @Test
    @DisplayName("Ошибка авторизации с неверным паролем")
    void loginWithWrongPasswordFails() {
        createCourier(testCourier).then().statusCode(201);

        loginCourierResponse(testCourier.login, "wrongpass")
                .then()
                .statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Ошибка авторизации без логина")
    void loginWithoutLoginFails() {
        CourierLogin loginData = new CourierLogin(null, "1234");
        given()
                .contentType("application/json")
                .body(loginData)
                .when()
                .post("/api/v1/courier/login")
                .then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Ошибка авторизации без пароля")
    void loginWithoutPasswordFails() {
        CourierLogin loginData = new CourierLogin("login" + System.currentTimeMillis(), null);
        given()
                .contentType("application/json")
                .body(loginData)
                .when()
                .post("/api/v1/courier/login")
                .then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Авторизация несуществующего курьера")
    void loginNonExistentCourier() {
        given()
                .contentType("application/json")
                .body(new CourierLogin("nonexistentUser", "password"))
                .when()
                .post("/api/v1/courier/login")
                .then()
                .statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"));
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