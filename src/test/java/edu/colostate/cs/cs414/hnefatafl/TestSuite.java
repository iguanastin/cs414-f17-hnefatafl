package edu.colostate.cs.cs414.hnefatafl;

import edu.colostate.cs.cs414.hnefatafl.game.BoardTest;
import edu.colostate.cs.cs414.hnefatafl.game.MatchTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import edu.colostate.cs.cs414.hnefatafl.server.ServerTest;
import edu.colostate.cs.cs414.hnefatafl.server.UserTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        UserTest.class,
        ServerTest.class,
        BoardTest.class,
        MatchTest.class
})


public class TestSuite {
}
