package common;

import common.game.FinishedMatch;

import java.io.Serializable;
import java.util.ArrayList;


public class Profile implements Serializable {

    private final ArrayList<FinishedMatch> history;
    private final int id;
    private final String name;


    public Profile(ArrayList<FinishedMatch> history, int id, String name) {
        this.history = history;
        this.id = id;
        this.name = name;
    }

    public ArrayList<FinishedMatch> getHistory() {
        return history;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

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
