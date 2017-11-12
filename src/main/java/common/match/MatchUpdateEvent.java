package common.match;


import common.Event;
import common.game.Match;

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
