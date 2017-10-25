package client;


import Game.Match;

public interface MatchListener {

    void matchUpdated(Match match);

    void matchStarted(Match match);

}
