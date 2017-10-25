package client;


public interface LoginListener {

    void loginSucceeded(int id, String name);

    void loginFailed(String name);

}
