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
 *
 * @author Adam Gouge
 */
public class GDMSTest extends AbstractGraphTester {

    /**
     * Start node column name.
     */
    private static final String START_NODE = "start_node";
    /**
     * End node column name.
     */
    private static final String END_NODE = "end_node";
    /**
     * Weight column name.
     */
    private static final String WEIGHT = "length";
    /**
     * Start node index.
     */
    private int startNodeIndex = -1;
    /**
     * End node index.
     */
    private int endNodeIndex = -1;
    /**
     * Weight index.
     */
    private int weightIndex = -1;

    /**
     * Allocates space for the graph data structure.
     *
     * @param size The node count.
     *
     * @return The newly created {@link LevelGraph}.
     */
    @Override
    LevelGraph createGraph(int size) {
        LevelGraphStorage g =
                new LevelGraphStorage(
                new RAMDirectory(
                "levelgraph",
                false));
        g.createNew(size);
        return g;
    }

    /**
     * Gets a {@link Scanner} on the given csv file that will be used to parse
     * the file.
     *
     * @param path The path of the csv file.
     *
     * @return The {@link Scanner}.
     *
     * @throws FileNotFoundException
     */
    public Scanner getScannerOnCSVFile(String path) throws FileNotFoundException {
        // Open the edges file.
        File edgesFile = new File(path);
        // We use a BufferedReader for efficiency.
        BufferedReader bufferedReader =
                new BufferedReader(
                new FileReader(
                edgesFile));
        // Get a scanner on the edges file.
        Scanner scanner = new Scanner(bufferedReader);
        return scanner;
    }

    /**
     * Initialize the start node, end node, and weight indices.
     *
     * @param scanner The scanner that will read the first line of the csv file.
     */
    public void initializeIndices(Scanner scanner) {
        String[] parts = scanner.nextLine().split(";");
        for (int i = 0; i < parts.length; i++) {
            // TODO: Make sure all indices are loaded correctly.
            // Have to get rid of the quotation marks.
            if (parts[i].replace("\"",
                    "").equals(START_NODE)) {
                startNodeIndex = i;
            } else if (parts[i].replace("\"",
                    "").equals(END_NODE)) {
                endNodeIndex = i;
            } else if (parts[i].replace("\"",
                    "").equals(WEIGHT)) {
                weightIndex = i;
            }
        }
//        System.out.println("startNodeIndex: " + startNodeIndex
//                + ", endNodeIndex: " + endNodeIndex
//                + ", lengthIndex: " + lengthIndex);
    }

    /**
     * Creates the folder in which to store the graph data structre.
     *
     * @return The folder.
     *
     * @throws IOException
     */
    public File createStorageDirectory() throws IOException {
        File folder = new File("./target/GDMSGraph");
        Helper.deleteDir(folder);
        boolean madeDirectories = folder.mkdirs();
        if (madeDirectories) {
            System.out.println(folder.getCanonicalPath()
                    + " created");
        } else {
            System.out.println("Unable to create "
                    + folder.getCanonicalPath());
        }
        return folder;
    }

    /**
     * Loads the edges from the csv file into the given graph.
     *
     * @param scanner             The scanner that will parse the csv file.
     * @param graph               The graph to which the edges are to be added.
     * @param preserveOrientation {@code true} if the orientation of the edgse
     *                            is to be preserved. {@code false} if the edges
     *                            are considered to be bidirectional.
     */
    public void loadEdges(Scanner scanner, Graph graph,
            boolean preserveOrientation) {
        int startNode, endNode;
        double length;
        while (scanner.hasNextLine()) {
            String[] parts = scanner.nextLine().split(";");
            // Have to get rid of the quotation marks.
            startNode = Integer.
                    parseInt(parts[startNodeIndex].replace("\"", ""));
            endNode = Integer.parseInt(parts[endNodeIndex].replace("\"", ""));
            length = Double.parseDouble(parts[weightIndex].replace("\"", ""));
//            System.out.println("startNode: " + startNode
//                + ", endNode: " + endNode
//                + ", length: " + length);
            graph.edge(startNode, endNode, length, preserveOrientation);
        }
    }

    /**
     * Prints out the edges of the given graph.
     *
     * @param graph The graph whose edges are to be printed.
     */
    public void printEdges(Graph graph) {
        EdgeIterator edgeIterator = graph.getAllEdges();
        while (edgeIterator.next()) {
            System.out.println("EdgeID: " + edgeIterator.edge()
                    + ", " + START_NODE + " " + edgeIterator.fromNode()
                    + ", " + END_NODE + " " + edgeIterator.node()
                    + ", " + WEIGHT + " " + edgeIterator.distance());
        }
    }

    /**
     * Loads a GDMS graph produced in OrbisGIS as the 
     * {@code output.edges} table given by {@code ST_Graph}
     * into a GraphHopper {@link LevelGraphStorage}.
     *
     * @throws IOException
     */
    @Test
    public void testLoadGDMSGraph() throws IOException {

        Scanner scanner = getScannerOnCSVFile("./files/graph2D.edges.csv");

        // Initialize the indices of the start_node, end_node, and length.
        initializeIndices(scanner);

        // INITIATE A LEVELGRAPH OBJECT USING RAMDIRECTORY STORAGE.
        LevelGraphStorage levelGraphStorage =
                new LevelGraphStorage(
                new RAMDirectory(createStorageDirectory().
                getAbsolutePath(),
                true));
        // Create the LevelGraph. 
        // TODO: How big does the nodeCount need to be?
        levelGraphStorage.createNew(10); // TODO: Returns a GraphStorage!

        // Load the edges from the input file into the levelgraph.
        loadEdges(scanner, levelGraphStorage, true);

        // Print out the edges.
        printEdges(levelGraphStorage);

        // Write to disk.
        levelGraphStorage.flush();
        // TODO: close() also calls flush(), so flush is called TWO TIMES!
        levelGraphStorage.close();
        scanner.close();
    }
}
