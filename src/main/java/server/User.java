package server;


public class User {

    /**
     * The connection currently associated with the user. Null if the user is not logged in.
     */
    private ConnectionToClient client = null;

    /**
     * Users unique name
     */
    private final String name;

    /**
     * Users password for login
     */
    private String password;
    //TODO: Make password hashed and salted

    private int id;
    private String email;
    private boolean unregistered = false;


    /**
     * Constructs a user object to represent a user in the system with a given set of credentials.
     *
     * @param name Unique name of the user
     * @param password Password for this account
     */
    public User(int id, String email, String name, String password) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.password = password;
    }

    public User(int id, String email, String name, String password, boolean unregistered) {
        this(id, email, name, password);
        this.unregistered = unregistered;
    }

    /**
     * Tests credentials against this user.
     *
     * @param pass The password being tested against this user.
     * @return True if the password matches this user; false otherwise.
     */
    public boolean comparePassword(String pass) {
        //TODO: Compare hashed and salted password instead of plaintext
        return this.password.equals(pass);
    }

    /**
     *
     * @return This user's unique name
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @return The client connection for this user if present. Null if the user is not logged in.
     */
    public ConnectionToClient getClient() {
        return client;
    }

    /**
     * Sets the client connection.
     *
     * @param client Client connection
     */
    public void setClient(ConnectionToClient client) {
        this.client = client;
    }

    /**
     *
     * @return True if this user is currently logged in; false otherwise.
     */
    public boolean isLoggedIn() {
        return getClient() != null;
    }

    @Override
    public String toString() {
        if (isLoggedIn()) {
            return "[" + getId() + "-" + getName() + "-" + getClient() + "]";
        } else {
            return "[" + getId() + "-" + getName() + "-Offline]";
        }
    }

    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public boolean isUnregistered() {
        return unregistered;
    }

    public void unregister() {
        unregistered = true;
    }

    public String getPassword() {
        return password;
    }
}
