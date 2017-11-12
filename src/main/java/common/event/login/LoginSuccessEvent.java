package common.event.login;


import common.Event;

public class LoginSuccessEvent extends Event {

    private final String username;

    private final int id;


    public LoginSuccessEvent(String username, int id) {
        this.username = username;
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Login succeeded for user: " + getId() + " - " + getUsername();
    }

}
