package model;

public class Order {

    private String[] ingredients;
    private String token;

    public Order() {}

    public Order(String[] ingredients) {
        this.ingredients = ingredients;
    }

    public Order(String[] ingredients, String token) {
        this.ingredients = ingredients;
        this.token = token;
    }

    public String[] getIngredients() {
        return ingredients;
    }

    public void setIngredients(String[] ingredients) {
        this.ingredients = ingredients;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}