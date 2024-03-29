package edu.colostate.cs.cs414.hnefatafl.server;


import edu.colostate.cs.cs414.hnefatafl.common.UserID;

import java.io.IOException;

public class User {

    /**
     * The connection currently associated with the user. Null if the user is not logged in.
     */
    private ConnectionToClient client = null;

    /**
     * Users password for login
     */
    private String password;
    //TODO: Make password hashed and salted

    /**
     * Unique ID associated with this user
     */
    private UserID id;

    /**
     * Email for this user
     */
    private String email;

    /**
     * Whether or not this user account has been unregistered
     */
    private boolean unregistered = false;


    /**
     * Constructs a user object to represent a user in the system with a given set of credentials.
     *
     * @param name Unique name of the user
     * @param password Password for this account
     */
    public User(int id, String email, String name, String password) {
        this.id = new UserID(id, name);
        this.email = email;
        this.password = password;
    }

    /**
     * Instantiates a User with the given information
     *
     * @param id           Unique ID
     * @param email        User's email
     * @param name         Name of user
     * @param password     User password
     * @param unregistered Whether or not this user account is unregistered
     */
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
     * Attempts to send an object to the client
     *
     * @param o Object to be sent to the client. Must be serializable
     * @return True if successful, false if stream is not active
     */
    public boolean send(Object o) {
        if (isLoggedIn()) {
            try {
                getClient().sendToClient(o);
                return true;
            } catch (IOException e) {
                return false;
            }
        }

        return false;
    }

    /**
     * Attempts to reset the output stream. Necessary when sending the same object multiple times in different states
     *
     * @return True if successful, false if stream not active
     */
    public boolean resetOutputStream() {
        if (isLoggedIn()) {
            try {
                getClient().forceResetAfterSend();
                return true;
            } catch (IOException e) {
                return false;
            }
        }
        return false;
    }

    /**
     *
     * @return True if this user is currently logged in; false otherwise.
     */
    public boolean isLoggedIn() {
        return getClient() != null;
    }

    /**
     *
     * @return Simple string representation of this object
     */
    @Override
    public String toString() {
        if (isLoggedIn()) {
            return "[" + getId() + "-" + getClient() + "]";
        } else {
            return "[" + getId() + "-Offline]";
        }
    }

    /**
     *
     * @return This user's unique ID
     */
    public UserID getId() {
        return id;
    }

    /**
     *
     * @return This user's email
     */
    public String getEmail() {
        return email;
    }

    /**
     *
     * @return True if this user account has been unregistered and is no longer active
     */
    public boolean isUnregistered() {
        return unregistered;
    }

    /**
     * Unregisters this user account, making it unable to be used, permanently.
     */
    public void unregister() {
        unregistered = true;
    }

    /**
     *
     * @return The salted/hashed password for this user
     */
    public String getPassword() {
        return password;
    }

    /**
     * Tests equality of this user and another user via their unique IDs
     *
     * @param obj Object to compare to
     * @return True if obj is a User object with the same ID as this user
     */
    @Override
    public boolean equals(Object obj) {
        return obj instanceof User && id.equals(((User) obj).getId());
    }
    @Override
    public int hashCode() {
    	return getId().hashCode();
    }
}
