package server;

import common.UserID;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class UserTest {

    @Test
    public void name() {
        User user = new User(1, "email","name", "pass");
        assertEquals("name", user.getId().getName());
    }

    @Test
    public void password() {
        User user = new User(1, "email","name", "paSS");
        assertTrue(user.comparePassword("paSS"));
        assertFalse(user.comparePassword("pass"));
        assertFalse(user.comparePassword("PASS"));
        assertFalse(user.comparePassword("1234"));
    }

    @Test
    public void loggedIn() {
        User user = new User(1, "email","name", "pass");
        assertFalse(user.isLoggedIn());
    }

    @Test
    public void id() {
        User user = new User(1, "email","name", "pass");
        assertEquals(new UserID(1, "name"), user.getId());
    }

    @Test
    public void email() {
        User user = new User(1, "email","name", "pass");
        assertEquals("email", user.getEmail());
    }

    @Test
    public void registered() {
        User user = new User(1, "email","name", "pass");
        assertFalse(user.isUnregistered());
        user.unregister();
        assertTrue(user.isUnregistered());
    }

}
