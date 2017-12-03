package common.event.login;


import common.Event;
import common.UserID;

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
