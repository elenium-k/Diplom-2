package model;

public class UserCreds {
    private String email;
    private String password;


    public UserCreds(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public UserCreds() {}

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public static UserCreds getCredsFromCourier (User user) {
        return new UserCreds(user.getEmail(), user.getPassword());
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
