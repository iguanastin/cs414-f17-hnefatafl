package client;


import common.game.Match;

public interface MoveListener {

    void playerRequestedMove(Match match, int fromRow, int fromCol, int toRow, int toCol);

}
