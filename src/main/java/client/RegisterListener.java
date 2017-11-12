package client;

public interface RegisterListener {
    void registerSucceeded(String email, String name, String password);

    void registerFailed(String email, String name, String error);
}
