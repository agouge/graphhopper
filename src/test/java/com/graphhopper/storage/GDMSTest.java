/*
 *  Copyright 2012 Peter Karich 
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
package com.graphhopper.storage;

import com.graphhopper.routing.DijkstraSimple;
import com.graphhopper.routing.Path;
import com.graphhopper.util.EdgeIterator;
import com.graphhopper.util.Helper;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import org.junit.Test;

/**
 * A class to test {@link GDMSGraphStorage}.
 *
 * @author Adam Gouge
 */
public class GDMSTest {

    /**
     * Loads a GDMS graph produced in OrbisGIS as the {@code output.edges} table
     * given by {@code ST_Graph} into a GraphHopper {@link LevelGraphStorage}.
     *
     * @throws IOException
     */
    public void testLoadGDMSGraph(String graphDirectory, String csvFile,
            String weightField, boolean bothDirections) throws IOException {

        // Initiate a  object using ramdirectory storage.
        GDMSGraphStorage graph =
                new GDMSGraphStorage(
                new RAMDirectory(
                graphDirectory,
                true), // true that we can write the graph to disk.
                weightField);
        if (graph.loadExisting()) {
            System.out.println("Loaded a previously created graph. ");
        } else {
            System.out.println("Creating a graph from CSV. ");
            Scanner scanner = graph.getScannerOnCSVFile(csvFile);
//            Scanner scanner = getScannerOnCSVFile(
//                    "./files/nantes_metropole_1_edges.csv"); // Département

            // Initialize the indices of the start_node, end_node, and length.
            graph.initializeIndices(scanner);

            // Create the LevelGraph. 
            // TODO: How big does the nodeCount need to be?
            graph.createNew(10); // TODO: Returns a GraphStorage!

            // Load the edges from the input file into the levelgraph.
            // Put true iff the edges are bidirectional.
            graph.loadEdges(scanner, graph, bothDirections);
            scanner.close();
        }

        // Print out the edges.
        graph.printEdges();

        // Shortest path calculations.
        Path path = new DijkstraSimple(graph).calcPath(2, 1);
        System.out.println(path.toDetailsString());

        // Write to disk.
        graph.flush();
        // TODO: close() also calls flush(), so flush is called TWO TIMES!
        graph.close();
    }

    /**
     * Tests loading a 2D graph with orientation preserved.
     *
     * @throws IOException
     */
    @Test
    public void testLoadGraph2D() throws IOException {
        testLoadGDMSGraph("./target/GDMSGraph",
                "./files/graph2D.edges.csv", "length", false);
    }
}
