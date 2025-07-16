package praktikum.model;

public class Courier {
    public String login;
    public String password;
    public String firstName;

    public Courier(String login, String password, String firstName) {
        this.login = login;
        this.password = password;
        this.firstName = firstName;
    }
    public static Courier random() {
        String login = "courier" + System.currentTimeMillis();
        return new Courier(login, "1234", "Иван");
    }
}
