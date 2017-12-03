package edu.colostate.cs.cs414.hnefatafl.common.event.match;


import edu.colostate.cs.cs414.hnefatafl.common.Event;
import edu.colostate.cs.cs414.hnefatafl.common.game.Match;

public class MatchFinishEvent extends Event {

    private final Match match;


    public MatchFinishEvent(Match match) {
        this.match = match;
    }

    public Match getMatch() {
        return match;
    }

    @Override
    public String toString() {
        return "Match finished between " + match.getAttacker() + " and " + match.getDefender();
    }

}
