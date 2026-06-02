package service;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import model.Order;
import static io.restassured.RestAssured.given;

public class OrdersClient {

    private static final String ORDER_ENDPOINT = "/api/orders";


    @Step("Создание заказа без авторизации")
    public Response createOrder(Order order) {
        return given()
                .body(order)
                .post(ORDER_ENDPOINT);
    }

    @Step("Создание заказа с авторизацией")
    public Response createOrder(Order order, String authToken) {
        return given()
                .header("Authorization", authToken)
                .body(order)
                .post(ORDER_ENDPOINT);
    }
}