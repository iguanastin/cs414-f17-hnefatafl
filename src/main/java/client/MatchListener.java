package client;


import common.game.Match;

public interface MatchListener {

    void matchUpdated(Match match);

    void matchStarted(Match match);

    void matchFinished(Match match);

}
