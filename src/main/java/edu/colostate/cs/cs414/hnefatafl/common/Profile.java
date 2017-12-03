package edu.colostate.cs.cs414.hnefatafl.common;

import edu.colostate.cs.cs414.hnefatafl.common.game.FinishedMatch;

import java.io.Serializable;
import java.util.ArrayList;


public class Profile implements Serializable {

    private final ArrayList<FinishedMatch> history;
    private final UserID id;


    /**
     * Constructs a profile with the given info
     *
     * @param history
     * @param id
     */
    public Profile(ArrayList<FinishedMatch> history, UserID id) {
        this.history = history;
        this.id = id;
    }

    /**
     *
     * @return A list of match history objects
     */
    public ArrayList<FinishedMatch> getHistory() {
        return history;
    }

    /**
     *
     * @return The id of the user whose profile this is
     */
    public UserID getId() {
        return id;
    }

    /**
     * Computes the winrate of this profile based on the match history
     *
     * @return A number between 0.0 and 1.0 that is the percentage of matches won
     */
    public double getWinrate() {
        int wins = 0, losses = 0;

        for (FinishedMatch m : history) {
            if (m.getWinner().equals(id)) {
                wins++;
            } else {
                losses++;
            }
        }

        double result;
        if (wins + losses > 0) {
            result = (double) wins / (double) (wins + losses);
        } else {
            result = 0;
        }

        return result;
    }
}
