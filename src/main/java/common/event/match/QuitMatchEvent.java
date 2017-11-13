package common.event.match;

import common.Event;
import common.game.Match;


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
