package client;


import common.Profile;

public interface ServerUtilListener {

    void profileReceived(Profile profile);

    void noSuchUserError(String requestedUser);

}
