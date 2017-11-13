package common.event.login;


import common.Event;

public class RegisterSuccessEvent extends Event {

    private final String email, username, password;

    private final int id;

    /**
     * The event sent from the server to the client when the registration was sucessful
     * This tell the client they are good to go, and can start the login process
     * @param email
     * @param username
     * @param password
     * @param id
     */
    public RegisterSuccessEvent(String email, String username, String password, int id) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return "Registration succeeded for user: " + getId() + " - " + getUsername();
    }

}

