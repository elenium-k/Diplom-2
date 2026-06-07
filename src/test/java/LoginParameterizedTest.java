
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import java.util.Arrays;
import java.util.Collection;
import static org.hamcrest.core.IsEqual.equalTo;
import static utils.ApiConfig.BASE_URI;
import model.User;
import model.UserCreds;
import org.junit.Test;
import service.UserClient;
import service.UserGenerator;


@RunWith(Parameterized.class)
public class LoginParameterizedTest {

    private UserClient userClient;
    private User testUser;
    private int expectedStatusCode;
    private boolean expectedSuccess;
    private String expectedMessage;

    public LoginParameterizedTest(User testUser, int expectedStatusCode,
                                  boolean expectedSuccess, String expectedMessage) {
        this.testUser = testUser;
        this.expectedStatusCode = expectedStatusCode;
        this.expectedSuccess = expectedSuccess;
        this.expectedMessage = expectedMessage;
    }

    @Before
    public void setUp() {
        RestAssured.baseURI = BASE_URI;
        userClient = new UserClient();
        if (testUser.getEmail() != null && testUser.getPassword() != null) {
            userClient.register(testUser);
        }
    }

    @Parameterized.Parameters(name = "{4}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                // Успешная авторизация существующего пользователя
                {
                        UserGenerator.userWithKnownParams(),
                        200,
                        true,
                        null
                },
                // Авторизация с неверным паролем
                {
                        new User()
                                .withEmail("bubenchik3@yandex.ru")
                                .withPassword("wrongpassword")
                                .withName("Bubenchik"),
                        401,
                        false,
                        "email or password are incorrect"
                },
                // Авторизация с несуществующим email
                {
                        new User()
                                .withEmail("wrong.email" + System.currentTimeMillis() + "@example.com")
                                .withPassword("1111122222")
                                .withName("wrong.name"),
                        401,
                        false,
                        "email or password are incorrect"
                },
                // Авторизация без email
                {
                        UserGenerator.withNullLogin(),
                        401,
                        false,
                        "email or password are incorrect"
                },
                // Авторизация без пароля
                {
                        UserGenerator.withNullPassword(),
                        401,
                        false,
                        "email or password are incorrect"
                }
        });
    }

    @Test
    @DisplayName("Логин пользователя")
    public void testUserLogin() {
        UserCreds userCreds = UserCreds.getCredsFromCourier(testUser);

        userClient.login(userCreds)
                .then()
                .statusCode(expectedStatusCode)
                .body("success", equalTo(expectedSuccess));

        if (expectedMessage != null) {
            userClient.login(userCreds)
                    .then()
                    .body("message", equalTo(expectedMessage));
        }
    }

    @After
    public void tearDown() {
        if (testUser != null && testUser.getEmail() != null) {
            try {
                userClient.deleteUserByEmail(testUser.getEmail());
            } catch (Exception e) {
                System.err.println("Ошибка при удалении пользователя: " + e.getMessage());
            }
        }
    }
}