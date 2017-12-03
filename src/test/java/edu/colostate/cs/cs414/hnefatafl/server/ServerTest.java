package edu.colostate.cs.cs414.hnefatafl.server;


import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;

import static org.junit.Assert.*;

public class ServerTest {

    @Test
    public void test() {
        try {
            Server server = new Server(54321);

            assertEquals(54321, server.getPort());
            assertTrue(server.isListening());
            server.close();
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            fail(e.getLocalizedMessage());
        }
    }

}
