package common;


import client.Client;

public class LoginRequestEvent extends Event {

    private final String username, password;
    private Client client;

    public LoginRequestEvent(Client client, String username, String password) {
        this.client = client;
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Client getClient() {
        return client;
    }

    @Override
    public String toString() {
        return "Login requested for username: " + getUsername();
    }

}
