package common;

import java.io.Serializable;


public class Invitation implements Serializable {

    private final int senderID, targetID;


    /**
     * Constructs an invitation from a specified user to another
     *
     * @param senderID
     * @param targetID
     */
    public Invitation(int senderID, int targetID) {
        this.senderID = senderID;
        this.targetID = targetID;
    }

    /**
     *
     * @return ID of the user who sent this invitation
     */
    public int getSenderID() {
        return senderID;
    }

    /**
     *
     * @return ID of the user who is to receive this invitation
     */
    public int getTargetID() {
        return targetID;
    }

}
