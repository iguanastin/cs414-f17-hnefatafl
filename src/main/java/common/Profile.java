package common;

import common.game.FinishedMatch;

import java.io.Serializable;
import java.util.ArrayList;


public class Profile implements Serializable {

    private final ArrayList<FinishedMatch> history;
    private final int id;
    private final String name;


    /**
     * Constructs a profile with the given info
     *
     * @param history
     * @param id
     * @param name
     */
    public Profile(ArrayList<FinishedMatch> history, int id, String name) {
        this.history = history;
        this.id = id;
        this.name = name;
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
     * @return The name of hte user whose profile this is
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @return The id of the user whose profile this is
     */
    public int getId() {
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
            if (m.getWinner() == id) {
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
