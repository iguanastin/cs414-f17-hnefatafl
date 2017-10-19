package server;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class UserTest {

    @Test
    public void name() {
        User user = new User("name", "pass");
        assertEquals("name", user.getName());
    }

    @Test
    public void password() {
        User user = new User("name", "paSS");
        assertTrue(user.comparePassword("paSS"));
        assertFalse(user.comparePassword("pass"));
        assertFalse(user.comparePassword("PASS"));
        assertFalse(user.comparePassword("1234"));
    }

    @Test
    public void loggedIn() {
        User user = new User("name", "pass");
        assertFalse(user.isLoggedIn());
    }

}
