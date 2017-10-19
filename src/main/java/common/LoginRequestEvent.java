package common;


public class LoginRequestEvent extends Event {

    private final String username, password;

    public LoginRequestEvent(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return "Login requested for username: " + getUsername();
    }

}
