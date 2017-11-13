package client;

public interface RegisterListener {
    /**
     * The event passed back to the client when the registration was successful
     * @param email
     * @param name
     * @param password
     */
    void registerSucceeded(String email, String name, String password);

    /**
     * The event passed back to the client when the registration failed.
     * @param email
     * @param name
     * @param error
     */
    void registerFailed(String email, String name, String error);
}
