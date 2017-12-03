package edu.colostate.cs.cs414.hnefatafl.client;


import edu.colostate.cs.cs414.hnefatafl.common.Profile;

public interface ServerUtilListener {

    void profileReceived(Profile profile);

    void noSuchUserError(String requestedUser);

}
