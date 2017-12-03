package common.game;


import common.UserID;

import java.io.Serializable;

public class FinishedMatch implements Serializable {

    public static final int ATTACKER_WIN = 0;
    public static final int DEFENDER_WIN = 1;
    public static final int ATTACKER_QUIT = 2;
    public static final int DEFENDER_QUIT = 3;

    private final int id, winner, endState;
    private final UserID p1, p2;


    public FinishedMatch(int id, UserID p1, UserID p2, int winner, int endState) {
        this.id = id;
        this.p1 = p1;
        this.p2 = p2;
        this.winner = winner;
        this.endState = endState;
    }

    public int getId() {
        return id;
    }

    public UserID getAttacker() {
        return p1;
    }

    public UserID getDefender() {
        return p2;
    }

    public int getWinner() {
        return winner;
    }

    public int getEndState() {
        return endState;
    }

    @Override
    public String toString() {
        String result = getAttacker() + " vs " + getDefender() + " - Winner: " + getWinner();

        if (getEndState() == ATTACKER_QUIT || getEndState() == DEFENDER_QUIT) result += " because enemy quit";

        return result;
    }

}
