package common;

import java.io.Serializable;


public class Invitation implements Serializable {

    private final int senderID, targetID;


    public Invitation(int senderID, int targetID) {
        this.senderID = senderID;
        this.targetID = targetID;
    }

    public int getSenderID() {
        return senderID;
    }

    public int getTargetID() {
        return targetID;
    }

}
