package common.event.login;


import client.Client;
import common.Event;

public class RegisterRequestEvent extends Event {

    private final String email, username, password;

    /**
     * This is the event the client sends to the server when they are initiating a registration.
     * This starts the servers work of checking the fields and storing the data.
     * @param email
     * @param username
     * @param password
     */
    public RegisterRequestEvent(String email, String username, String password) {
        this.email = email;
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return "Login requested for username: " + getUsername();
    }

}
