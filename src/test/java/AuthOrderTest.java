import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import model.Order;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.*;
import static utils.ApiConfig.BASE_URI;
import model.User;
import model.UserCreds;
import org.junit.Test;
import service.OrdersClient;
import service.UserClient;
import service.UserGenerator;

@RunWith(Parameterized.class)
public class AuthOrderTest {

    private OrdersClient ordersClient;
    private UserClient userClient;
    private String authToken;
    private User testUser; // Сохраняем пользователя для удаления после теста

    // Валидные ингредиенты
    private static final String[] VALID_INGREDIENTS = {
            "61c0c5a71d1f82001bdaaa6d",
            "61c0c5a71d1f82001bdaaa6f",
            "61c0c5a71d1f82001bdaaa6c"
    };

    // Невалидный ингредиент
    private static final String INVALID_INGREDIENT = "61c0c5a71d1f82001bda1111";

    private final Order order;
    private final int expectedStatusCode;
    private final String expectedMessage;

    public AuthOrderTest(Order order,
                         int expectedStatusCode,
                         String expectedMessage) {
        this.order = order;
        this.expectedStatusCode = expectedStatusCode;
        this.expectedMessage = expectedMessage;
    }

    @Before
    public void setUp() {
        RestAssured.baseURI = BASE_URI;
        ordersClient = new OrdersClient();
        userClient = new UserClient();

        // логинимся
        testUser = UserGenerator.userWithKnownParams();

        UserCreds userCreds = new UserCreds();
        userCreds.setEmail(testUser.getEmail());
        userCreds.setPassword(testUser.getPassword());

        Response loginResponse = userClient.login(userCreds);
        assertEquals("Логин пользователя провалился", 200, loginResponse.statusCode());

        String rawToken = loginResponse.jsonPath().getString("accessToken");
        assertNotNull("Токен авторизации не получен", rawToken);

        //  обработка префикса Bearer
        if (rawToken.startsWith("Bearer ")) {
            authToken = rawToken.replace("Bearer ", "");
        } else {
            authToken = rawToken;
        }
    }

    @Parameterized.Parameters(name = "{3}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                // С валидными ингредиентами
                { new Order(VALID_INGREDIENTS), 200, null },
                // Без ингредиентов
                { new Order(new String[0]), 400, "Ingredient ids must be provided" },
                // С неверным хешем ингредиентов
                { new Order(new String[]{INVALID_INGREDIENT}), 500, null }
        });
    }

    @Test
    @DisplayName("Заказ авторизированного пользователя")
    @Description("Авторизированный пользователь может сделать заказ")
    public void testCreateOrderWithDifferentParameters() {
        Response response = ordersClient.createOrder(order, authToken);

        assertEquals("Статус ответа не соответствует ожидаемому", expectedStatusCode, response.statusCode());

        if (expectedMessage != null) {
            assertTrue("Сообщение об ошибке не соответствует ожидаемому: " +
                            response.body().asString(),
                    response.body().asString().contains(expectedMessage));
        }
    }
}
