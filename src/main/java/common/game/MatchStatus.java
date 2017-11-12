package common.game;

import java.io.Serializable;

public enum MatchStatus implements Serializable {
    ATTACKER_TURN, DEFENDER_TURN, ATTACKER_WIN, DEFENDER_WIN, DRAW_GAME
}
