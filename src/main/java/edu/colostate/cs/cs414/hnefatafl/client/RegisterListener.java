package edu.colostate.cs.cs414.hnefatafl.client;

import edu.colostate.cs.cs414.hnefatafl.common.UserID;

public interface RegisterListener {
    /**
     * The event passed back to the client when the registration was successful
     * @param email
     * @param id
     */
    void registerSucceeded(String email, UserID id);

    /**
     * The event passed back to the client when the registration failed.
     * @param email
     * @param name
     * @param error
     */
    void registerFailed(String email, String name, String error);
}
