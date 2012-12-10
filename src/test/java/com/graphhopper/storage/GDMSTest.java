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
import com.graphhopper.routing.ch.PrepareContractionHierarchies;
import com.graphhopper.routing.util.ShortestCarCalc;
import com.graphhopper.util.GraphUtility;
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
    public GDMSGraphStorage loadGDMSGraph(String graphDirectory, String csvFile,
            String weightField, boolean bothDirections) throws IOException {

        // Initiate a graph object using RAMDirectory storage.
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
        return graph;
    }

    /**
     * Calculates the shortest path between two nodes and prints out the time it
     * took to calculate this path.
     *
     * @param ds        Used to calculate the shortest path.
     * @param startNode The start node.
     * @param endNode   The end node.
     */
    public void timeShortestPathCalculation(DijkstraSimple ds, int startNode,
            int endNode) {
        long start = System.currentTimeMillis();
        Path path = ds.calcPath(startNode, endNode);
        long stop = System.currentTimeMillis();
        long time = stop - start;
        System.out.println("Time: " + time + " ms, " + path.toDetailsString());
    }

    /**
     * Tests loading a 2D graph with orientation preserved.
     *
     * @throws IOException
     */
    @Test
    public void testLoadGraph2DOriented() throws IOException {
        GDMSGraphStorage graph = loadGDMSGraph("./target/Graph2D",
                "./files/graph2D.edges.csv", "length", false);
        // Print out the edges.
        graph.printEdges();
//        PrepareTowerNodesShortcutsTest.printEdges(graph);
    }

//    /**
//     * Tests loading the Loire Atlantique road network where all roads are
//     * considered bidirectional.
//     *
//     * @throws IOException
//     */
//    @Test
//    public void testLoadGraphLoireAtlantiqueBidirectional() throws IOException {
//        GDMSGraphStorage graph = loadGDMSGraph("./target/GraphLoireAtlantique",
//                "./files/loire_atlantique_1_edges.csv", "weight", true);
//        // Shortest path calculations.
//        DijkstraSimple ds = new DijkstraSimple(graph);
//        timeShortestPathCalculation(ds, 20384, 59847);
//        timeShortestPathCalculation(ds, 84576, 37548);
//        timeShortestPathCalculation(ds, 42156, 17542);
//    }

    public GDMSGraphStorage prepareContractionHierarchies(String graphDirectory, String csvFile,
            String weightField, boolean bothDirections) throws IOException {

        // Initiate a graph object using RAMDirectory storage.
        GDMSGraphStorage graph = loadGDMSGraph(graphDirectory,
                csvFile, weightField, bothDirections);

        PrepareContractionHierarchies ch = new PrepareContractionHierarchies();
        ch.setGraph(graph);
        ch.setType(ShortestCarCalc.DEFAULT); // By default.
        if (!ch.isPrepared()) {
            System.out.println("Edges before CH: " + GraphUtility.count(graph.
                    getAllEdges()));
            System.out.println("Preparing contraction hierachies. ");
            ch.doWork();
        } else { // TODO: This is never returned.
            System.out.println("Already prepared contraction hierachies! ");
        }
        System.out.println("Edges after CH: " + GraphUtility.count(graph.
                getAllEdges()));

        // Write to disk. This overwrites the original graph with the 
        // graph constructed using contraction hiearchies.
        System.out.println("Writing changes to disk. ");
        graph.flush();
        // TODO: close() also calls flush(), so flush is called TWO TIMES!
//        graph.close();
        return graph;
    }
    
    @Test
    public void testContractionHierarchiesGraph2DOriented() throws IOException {
        GDMSGraphStorage graph = prepareContractionHierarchies("./target/Graph2D",
                "./files/graph2D.edges.csv", "length", false);
    }
    
//    @Test
//    public void testContractionHierarchiesGraphLoireAtlantiqueBidirectional() throws IOException {
//        GDMSGraphStorage graph = prepareContractionHierarchies("./target/GraphLoireAtlantique",
//                "./files/loire_atlantique_1_edges.csv", "weight", true);
//    }
}
