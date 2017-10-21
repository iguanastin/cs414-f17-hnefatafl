import Game.BoardTest;
import Game.MatchTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import server.ServerTest;
import server.UserTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        UserTest.class,
        ServerTest.class,
        BoardTest.class,
        MatchTest.class
})


public class TestSuite {
}
