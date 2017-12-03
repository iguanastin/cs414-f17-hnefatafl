package edu.colostate.cs.cs414.hnefatafl.common.event.login;


import edu.colostate.cs.cs414.hnefatafl.common.Event;

public class RegisterFailedEvent extends Event {

    private final String email, username, error;

    /**
     * The constuctor for the registration succeed event.  Passes this to the client where it deisplay the correct message
     * @param email
     * @param username
     * @param error
     */
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

    /**
     * Override the toString to print the following message for this event.
     * @return
     */
    @Override
    public String toString() {
        return "Login request denied for user: " + getUsername();
    }

}
