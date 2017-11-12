package common.event.login;


import common.Event;

public class RegisterFailedEvent extends Event {

    private final String email, username, error;


    public RegisterFailedEvent(String email, String username, String error) {
        this.username = username;
        this.email = email;
        this.error = error;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getError() {
        return error;
    }

    @Override
    public String toString() {
        return "Login request denied for user: " + getUsername();
    }

}
