package service;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import model.User;
import model.UserCreds;

import static io.restassured.RestAssured.given;

public class UserClient {
    private static final String REGISTER_ENDPOINT = "/api/auth/register";
    private static final String LOGIN_ENDPOINT = "/api/auth/login";

    @Step("Регистрация пользователя")
    public Response register(User user) {
        return given().header("Content-type", "Application/json")
                .body(user)
                .when()
                .post(REGISTER_ENDPOINT);
    }

    @Step("Логин пользователя")
    public Response login(UserCreds userCreds) {
        return given().header("Content-type", "Application/json")
                .body(userCreds)
                .when()
                .post(LOGIN_ENDPOINT);
    }

    @Step("Удаление пользователя по email")
    public Response deleteUserByEmail(String email) {
        return given()
                .header("Content-Type", "application/json")
                .queryParam("email", email)
                .when()
                .delete("/api/auth/user");
    }

}
