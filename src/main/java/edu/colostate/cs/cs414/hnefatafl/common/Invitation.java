package edu.colostate.cs.cs414.hnefatafl.common;

import java.io.Serializable;


public class Invitation implements Serializable {

    private final UserID sender, target;


    /**
     * Constructs an invitation from a specified user to another
     *
     * @param sender
     * @param target
     */
    public Invitation(UserID sender, UserID target) {
        this.sender = sender;
        this.target = target;
    }

    /**
     *
     * @return ID of the user who sent this invitation
     */
    public UserID getSender() {
        return sender;
    }

    /**
     *
     * @return ID of the user who is to receive this invitation
     */
    public UserID getTarget() {
        return target;
    }

}
