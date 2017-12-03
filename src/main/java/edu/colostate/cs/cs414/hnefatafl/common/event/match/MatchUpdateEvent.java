package edu.colostate.cs.cs414.hnefatafl.common.event.match;


import edu.colostate.cs.cs414.hnefatafl.common.Event;
import edu.colostate.cs.cs414.hnefatafl.common.game.Match;

public class MatchUpdateEvent extends Event {

    private final Match match;


    public MatchUpdateEvent(Match match) {
        this.match = match;
    }

    public Match getMatch() {
        return match;
    }

    @Override
    public String toString() {
        return "Match state updated for " + match.getAttacker() + "vs" + match.getDefender();
    }

}
