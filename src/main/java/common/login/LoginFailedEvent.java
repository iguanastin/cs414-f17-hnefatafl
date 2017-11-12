package common.login;


import common.Event;

public class LoginFailedEvent extends Event {

    private final String username;


    public LoginFailedEvent(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public String toString() {
        return "Login request denied for user: " + getUsername();
    }

}
