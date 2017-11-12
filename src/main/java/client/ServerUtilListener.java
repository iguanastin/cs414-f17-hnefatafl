package client;


import common.profile.Profile;

public interface ServerUtilListener {

    void profileReceived(Profile profile);

    void noSuchUserError(String requestedUser);

}
