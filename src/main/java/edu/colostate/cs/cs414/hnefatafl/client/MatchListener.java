package edu.colostate.cs.cs414.hnefatafl.client;


import edu.colostate.cs.cs414.hnefatafl.common.game.Match;

public interface MatchListener {

    void matchUpdated(Match match);

    void matchStarted(Match match);

    void matchFinished(Match match);

}
