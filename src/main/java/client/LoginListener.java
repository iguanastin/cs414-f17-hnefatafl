package client;


import common.UserID;

public interface LoginListener {

    void loginSucceeded(UserID id);

    void loginFailed(String name);
}
