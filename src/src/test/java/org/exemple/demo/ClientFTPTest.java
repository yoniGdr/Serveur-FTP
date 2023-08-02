package org.exemple.demo;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class ClientFTPTest 
    extends TestCase
{
    public void testIsConnected() {
        ClientFTP client = new ClientFTP();
        client.setConnected(true);
        assertTrue(client.isConnected());
    }

    public void testSetUsername() {
        ClientFTP client = new ClientFTP();
        client.setUsername("user");
        assertEquals("user", client.getUsername());
    }

    public void testSetPassword() {
        ClientFTP client = new ClientFTP();
        client.setPassword("password");
        assertEquals("password", client.getPassword());
    }
}
