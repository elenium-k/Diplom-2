
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import service.UserClient;
import service.UserGenerator;

import java.util.Arrays;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static utils.ApiConfig.BASE_URI;

@RunWith(Parameterized.class)
public class RegistrationParameterizedTest {

    private UserClient userClient;
    private static List<String> createdUserEmails = new ArrayList<>();

    // Тестовые данные
    private final User testUser;
    private final int expectedStatusCode;
    private final boolean expectedSuccess;
    private final String expectedMessage;


    public RegistrationParameterizedTest(User testUser,
                                         int expectedStatusCode,
                                         boolean expectedSuccess,
                                         String expectedMessage) {
        this.testUser = testUser;
        this.expectedStatusCode = expectedStatusCode;
        this.expectedSuccess = expectedSuccess;
        this.expectedMessage = expectedMessage;
    }

    @Before
    public void setUp() {
        io.restassured.RestAssured.baseURI = BASE_URI;
        userClient = new UserClient();
    }

    @Parameterized.Parameters(name = "{4}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                // Сценарий 1: Создание уникального пользователя (с временным email)
                {
                        UserGenerator.testUser(),
                        200,
                        true,
                        null
                },
                // Сценарий 2: Попытка создания уже существующего пользователя (фиксированный email)
                {
                        UserGenerator.userWithKnownParams(),
                        403,
                        false,
                        "User already exists"
                },
                // Сценарий 3: Создание без email
                {
                        UserGenerator.withNullLogin(),
                        403,
                        false,
                        "Email, password and name are required fields"
                },
                // Сценарий 4: Создание без пароля
                {
                        UserGenerator.withNullPassword(),
                        403,
                        false,
                        "Email, password and name are required fields"
                },
                // Сценарий 5: Создание без имени
                {
                        UserGenerator.withNullName(),
                        403,
                        false,
                        "Email, password and name are required fields"
                }
        });
    }


    @Test
    @DisplayName("Регистрация с разными параметрами")
    public void testUserRegistrationWithDifferentParameters() {
        Response response = userClient.register(testUser);

        assertEquals(expectedStatusCode, response.statusCode());
        assertEquals(expectedSuccess, response.jsonPath().getBoolean("success"));

        if (expectedMessage != null) {
            assertEquals(expectedMessage, response.jsonPath().getString("message"));
        }

        // Если пользователь успешно создан, сохраняем email для последующего удаления
        if (expectedStatusCode == 201) {
            createdUserEmails.add(testUser.getEmail());
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
