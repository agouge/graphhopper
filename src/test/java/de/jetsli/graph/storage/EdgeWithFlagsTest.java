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
package de.jetsli.graph.storage;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Peter Karich, info@jetsli.de
 */
public class EdgeWithFlagsTest {

    @Test
    public void testFlags() {
        EdgeWithFlags de = new EdgeWithFlags(1, 12, (byte) 1);
        de.flags |= 1;
        assertEquals(1, de.flags);
        de.flags |= 2;
        assertEquals(3, de.flags);

        de = new EdgeWithFlags(1, 12, (byte) 1);
        de.flags |= 2;
        assertEquals(3, de.flags);

        de = new EdgeWithFlags(1, 12, (byte) 2);
        de.flags |= 1;
        assertEquals(3, de.flags);
    }
}
