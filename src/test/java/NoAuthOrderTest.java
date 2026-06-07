
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
import static org.junit.Assert.assertEquals;
import static utils.ApiConfig.BASE_URI;
import org.junit.Test;
import service.OrdersClient;



@RunWith(Parameterized.class)
@DisplayName("Тесты создания заказа без авторизации (ожидается редирект на /login)")
public class NoAuthOrderTest {

    private OrdersClient ordersClient;

    // Валидные ингредиенты
    private static final String[] VALID_INGREDIENTS = {
            "61c0c5a71d1f82001bdaaa6d",
            "61c0c5a71d1f82001bdaaa6f",
            "61c0c5a71d1f82001bdaaa6d"
    };

    // Невалидный ингредиент
    private static final String INVALID_INGREDIENT = "61c0c5a71d1f82001bda1111";

    private final Order order;
    private final int expectedStatusCode;

    public NoAuthOrderTest(Order order, int expectedStatusCode) {
        this.order = order;
        this.expectedStatusCode = expectedStatusCode;
    }

    @Before
    public void setUp() {
        RestAssured.baseURI = BASE_URI;
        ordersClient = new OrdersClient();
    }

    @Parameterized.Parameters(name = "{3}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                // с валидными ингредиентами
                { new Order(VALID_INGREDIENTS), 302 },
                // без ингредиентов
                { new Order(new String[0]), 302 },
                // с неверным хешем ингредиентов
                { new Order(new String[]{INVALID_INGREDIENT}), 302 }
        });
    }

    @Test
    @DisplayName("Заказа без авторизации — редирект на логин")
    @Description("Неавторизированный пользователь перенаправляется на страницу авторизации")
    public void testCreateOrderWithoutAuth() {
        Response response = ordersClient.createOrder(order);

        assertEquals(expectedStatusCode, response.statusCode());
        }
    }