package common.game;


import common.UserID;

import java.io.Serializable;

public class FinishedMatch implements Serializable {

    public static final int ATTACKER_WIN = 0;
    public static final int DEFENDER_WIN = 1;
    public static final int ATTACKER_QUIT = 2;
    public static final int DEFENDER_QUIT = 3;

    private final int id, endState;
    private final UserID p1, p2, winner;


    public FinishedMatch(int id, UserID p1, UserID p2, UserID winner, int endState) {
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

    public UserID getWinner() {
        return winner;
    }

    public int getEndState() {
        return endState;
    }

    @Override
    public String toString() {
        String result = getAttacker().getName() + " vs " + getDefender().getName() + " - Winner: " + getWinner().getName();

        if (getEndState() == ATTACKER_QUIT) {
            result += " because " + getAttacker().getName() + " quit";
        } else if (getEndState() == DEFENDER_QUIT) {
            result += " because " + getDefender().getName() + " quit";
        }

        return result;
    }

}
