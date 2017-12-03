package edu.colostate.cs.cs414.hnefatafl.client;


import edu.colostate.cs.cs414.hnefatafl.common.UserID;

public interface LoginListener {

    void loginSucceeded(UserID id);

    void loginFailed(String name);
}
