package common;


import common.game.Match;

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
