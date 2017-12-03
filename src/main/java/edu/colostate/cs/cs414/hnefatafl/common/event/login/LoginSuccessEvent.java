package edu.colostate.cs.cs414.hnefatafl.common.event.login;


import edu.colostate.cs.cs414.hnefatafl.common.Event;
import edu.colostate.cs.cs414.hnefatafl.common.UserID;

public class LoginSuccessEvent extends Event {

    private final UserID id;


    public LoginSuccessEvent(UserID id) {
        this.id = id;
    }

    public UserID getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Login succeeded for user: " + getId();
    }

}
