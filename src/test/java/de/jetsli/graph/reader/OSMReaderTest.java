/*
 *  Copyright 2012 Peter Karich info@jetsli.de
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package de.jetsli.graph.reader;

import de.jetsli.graph.routing.util.CarStreetType;
import de.jetsli.graph.storage.Graph;
import de.jetsli.graph.util.EdgeIterator;
import de.jetsli.graph.util.GraphUtility;
import de.jetsli.graph.util.Helper;
import java.io.File;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Peter Karich, info@jetsli.de
 */
public class OSMReaderTest {

    private String dir = "/tmp/OSMReaderTrialsTest/test-db";
    private OSMReader reader;

    @Before
    public void setUp() {
        new File(dir).mkdirs();
    }

    @After
    public void tearDown() {
        Helper.deleteDir(new File(dir));
    }

    @Test public void testMain() {
        reader = new OSMReader(dir, 1000);
        reader.preprocessAcceptHighwaysOnly(getClass().getResourceAsStream("test-osm.xml"));
        reader.writeOsm2Graph(getClass().getResourceAsStream("test-osm.xml"));
        reader.flush();
        Graph graph = reader.getGraph();
        assertEquals(4, graph.getNodes());
        assertEquals(1, GraphUtility.count(graph.getOutgoing(0)));
        assertEquals(3, GraphUtility.count(graph.getOutgoing(1)));
        assertEquals(1, GraphUtility.count(graph.getOutgoing(2)));

        EdgeIterator iter = graph.getOutgoing(1);
        assertTrue(iter.next());
        assertEquals(0, iter.node());
        assertEquals(88.643, iter.distance(), 1e-3);
        assertTrue(iter.next());
        assertEquals(2, iter.node());
        assertEquals(93.146888, iter.distance(), 1e-3);
        CarStreetType flags = new CarStreetType(iter.flags());
        assertTrue(flags.isMotorway());
        assertTrue(flags.isForward());
        assertTrue(flags.isBackward());
        assertTrue(iter.next());
        flags = new CarStreetType(iter.flags());
        assertTrue(flags.isService());
        assertTrue(flags.isForward());
        assertTrue(flags.isBackward());

        // get third added location id=30
        iter = graph.getOutgoing(2);
        assertTrue(iter.next());
        assertEquals(1, iter.node());
        assertEquals(93.146888, iter.distance(), 1e-3);
    }

    @Test public void testWithBounds() {
        reader = new OSMReader(dir, 1000) {
            @Override public boolean isInBounds(double lat, double lon) {
                return lat > 49 && lon > 8;
            }
        };
        reader.preprocessAcceptHighwaysOnly(getClass().getResourceAsStream("test-osm.xml"));
        reader.writeOsm2Graph(getClass().getResourceAsStream("test-osm.xml"));
        reader.flush();
        Graph graph = reader.getGraph();
        assertEquals(3, graph.getNodes());
        assertEquals(1, GraphUtility.count(graph.getOutgoing(0)));
        assertEquals(2, GraphUtility.count(graph.getOutgoing(1)));
        assertEquals(1, GraphUtility.count(graph.getOutgoing(2)));

        EdgeIterator iter = graph.getOutgoing(1);
        assertTrue(iter.next());
        assertEquals(0, iter.node());
        assertEquals(88.643, iter.distance(), 1e-3);
        assertTrue(iter.next());
        assertEquals(2, iter.node());
        assertEquals(93.146888, iter.distance(), 1e-3);

        // get third added location => 2
        iter = graph.getOutgoing(2);
        assertTrue(iter.next());
        assertEquals(1, iter.node());
        assertEquals(93.146888, iter.distance(), 1e-3);
    }

    @Test public void testOneWay() {
        reader = new OSMReader(dir, 1000);
        reader.preprocessAcceptHighwaysOnly(getClass().getResourceAsStream("test-osm2.xml"));
        reader.writeOsm2Graph(getClass().getResourceAsStream("test-osm2.xml"));
        reader.flush();
        Graph graph = reader.getGraph();

        assertEquals(1, GraphUtility.count(graph.getOutgoing(0)));
        assertEquals(1, GraphUtility.count(graph.getOutgoing(1)));
        assertEquals(0, GraphUtility.count(graph.getOutgoing(2)));

        EdgeIterator iter = graph.getOutgoing(1);
        assertTrue(iter.next());
        assertEquals(2, iter.node());

        iter = graph.getEdges(1);
        assertTrue(iter.next());
        CarStreetType flags = new CarStreetType(iter.flags());
        assertTrue(flags.isMotorway());
        assertFalse(flags.isForward());
        assertTrue(flags.isBackward());

        assertTrue(iter.next());
        flags = new CarStreetType(iter.flags());
        assertTrue(flags.isMotorway());
        assertTrue(flags.isForward());
        assertFalse(flags.isBackward());
    }
}
