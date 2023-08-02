package org.exemple.demo;

import junit.framework.TestCase;
import java.io.IOException;


public class DataServeurTest 
    extends TestCase
{
    public void testConstructor() throws IOException {
        DataServeur dataServer = new DataServeur();
        assertNotNull(dataServer.getDataSocket());
        assertNotNull(dataServer.getAddress());
        assertTrue(dataServer.getPort() > 0);
    } 

}