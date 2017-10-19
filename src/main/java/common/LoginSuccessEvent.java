package common;


public class LoginSuccessEvent extends Event {

    private final String username;


    public LoginSuccessEvent(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public String toString() {
        return "Login succeeded for user: " + getUsername();
    }

}
