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
package de.jetsli.graph.coll;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Peter Karich, info@jetsli.de
 */
public abstract class AbstractMyBitSetTest {

    public abstract MyBitSet createBitSet(int no);

    @Test
    public void testToString() {
        MyBitSet bs = createBitSet(100);
        bs.add(12);
        bs.add(1);
        assertEquals("{1, 12}", bs.toString());
    }

    @Test
    public void testNext() {
        MyBitSet bs = createBitSet(100);
        bs.add(7);
        bs.add(90);
        assertEquals(7, bs.next(0));
        assertEquals(7, bs.next(7));
        assertEquals(90, bs.next(8));
        assertEquals(-1, bs.next(91));
    }

    @Test
    public void testEnsureCapacity() {
        MyBitSet bs = createBitSet(8);
        bs.add(7);
        try {
            bs.add(8);
            assertTrue(false);
        } catch (Throwable ex) {
        }
        bs.ensureCapacity(16);
        bs.add(8);
        bs.add(9);
        assertFalse(bs.contains(6));
        assertTrue(bs.contains(7));
        assertTrue(bs.contains(8));
    }

    @Test
    public void testClear() {
        MyBitSet bs = createBitSet(100);
        bs.add(12);
        bs.add(1);
        assertTrue(bs.contains(1));
        assertFalse(bs.contains(2));
        assertTrue(bs.contains(12));
        bs.clear();
        assertFalse(bs.contains(1));
        assertFalse(bs.contains(2));
        assertFalse(bs.contains(12));
        assertEquals(0, bs.getCardinality());
        bs.add(12);
        bs.add(1);
        assertTrue(bs.contains(1));
        assertFalse(bs.contains(2));
        assertTrue(bs.contains(12));
    }
}
