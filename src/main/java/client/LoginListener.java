package client;


public interface LoginListener {

    void loginSucceeded(int id, String name);

    void loginFailed(String name);

    void registerSucceeded(String email, String name, String password);

    void registerFailed(String email, String name, String error);
}
