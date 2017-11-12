package common.game;


import java.io.Serializable;

public class FinishedMatch implements Serializable {

    public static final int ATTACKER_WIN = 0;
    public static final int DEFENDER_WIN = 1;
    public static final int ATTACKER_QUIT = 2;
    public static final int DEFENDER_QUIT = 3;

    private final int id, winner, p1, p2, endState;


    public FinishedMatch(int id, int p1, int p2, int winner, int endState) {
        this.id = id;
        this.p1 = p1;
        this.p2 = p2;
        this.winner = winner;
        this.endState = endState;
    }

    public int getId() {
        return id;
    }

    public int getAttacker() {
        return p1;
    }

    public int getDefender() {
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
