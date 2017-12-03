package edu.colostate.cs.cs414.hnefatafl.common.event.login;


import edu.colostate.cs.cs414.hnefatafl.common.Event;
import edu.colostate.cs.cs414.hnefatafl.common.UserID;

public class RegisterSuccessEvent extends Event {

    private final String email;

    private final UserID id;

    /**
     * The event sent from the server to the client when the registration was sucessful
     * This tell the client they are good to go, and can start the login process
     * @param email
     * @param id
     */
    public RegisterSuccessEvent(String email, UserID id) {
        this.email = email;
        this.id = id;
    }

    public UserID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return "Registration succeeded for user: " + getId();
    }

}

