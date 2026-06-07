package service;
import io.qameta.allure.Step;
import model.User;

public class UserGenerator {

    @Step("Создание пользователя с определёнными параметрами")
    public static User testUser() {
        return new User()
                .withEmail("test" + System.currentTimeMillis() + "@example.com")
                .withPassword("1111122222")
                .withName("Lenkapenka");
    }

    @Step("Создание конкретного пользователя")
    public static User userWithKnownParams() {
        return new User()
                .withEmail("bubenchik3@yandex.ru")
                .withPassword("1111122222")
                .withName("Bubenchik");
    }

    @Step("Создание пользователя без пароля")
    public static User withNullPassword() {
        return new User()
                .withEmail("bubenchik3@yandex.ru")
                .withPassword(null)
                .withName("iloveburgers");
    }

    @Step("Создание пользователя без логина")
    public static User withNullLogin() {
        return new User()
                .withEmail(null)
                .withPassword("1111122222")
                .withName("burgersmylove");
    }

    @Step("Создание пользователя без имени")
    public static User withNullName() {
        return new User()
                .withEmail("test" + System.currentTimeMillis() + "@example.com")
                .withPassword("testpassword")
                .withName(null);
    }
}