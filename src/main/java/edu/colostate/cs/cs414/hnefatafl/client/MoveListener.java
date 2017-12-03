package edu.colostate.cs.cs414.hnefatafl.client;


import edu.colostate.cs.cs414.hnefatafl.common.game.Match;

public interface MoveListener {

    void playerRequestedMove(Match match, int fromRow, int fromCol, int toRow, int toCol);

}
