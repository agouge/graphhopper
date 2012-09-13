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
package de.jetsli.graph.util.shapes;

import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author Peter Karich
 */
public class CircleTest {

    @Test
    public void testIntersectCircleCircle() {
        assertTrue(new Circle(0, 0, 80).intersect(new Circle(1, 1, 80)));
        assertFalse(new Circle(0, 0, 75).intersect(new Circle(1, 1, 80)));
    }

    @Test
    public void testIntersectCircleBBox() {
        assertTrue(new Circle(10, 10, 120).intersect(new BBox(9, 11, 8, 9)));
        assertTrue(new BBox(9, 11, 8, 9).intersect(new Circle(10, 10, 120)));

        assertFalse(new Circle(10, 10, 110).intersect(new BBox(9, 11, 8, 9)));
        assertFalse(new BBox(9, 11, 8, 9).intersect(new Circle(10, 10, 110)));
    }

    @Test
    public void testContains() {
        Circle c = new Circle(10, 10, 120);
        assertTrue(c.contains(new BBox(9, 11, 10, 10.1)));
        assertFalse(c.contains(new BBox(9, 11, 8, 9)));
        assertFalse(c.contains(new BBox(9, 12, 10, 10.1)));
    }

    @Test
    public void testContainsCircle() {
        Circle c = new Circle(10, 10, 120);
        assertTrue(c.contains(new Circle(9.9, 10.2, 90)));
        assertFalse(c.contains(new Circle(10, 10.4, 90)));
    }
}
