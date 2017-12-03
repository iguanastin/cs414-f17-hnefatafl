package edu.colostate.cs.cs414.hnefatafl.common.event.match;

import edu.colostate.cs.cs414.hnefatafl.common.Event;
import edu.colostate.cs.cs414.hnefatafl.common.game.Match;


public class QuitMatchEvent extends Event {

    private final Match match;


    public QuitMatchEvent(Match match) {
        this.match = match;
    }

    public Match getMatch() {
        return match;
    }

    @Override
    public String toString() {
        return "User quitting match " + match;
    }

}
