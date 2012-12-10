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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

/**
 * A {@link LevelGraphStorage} created from a {@code output.edges} table (csv
 * format) produced by the {@code ST_Graph} function of the GDMS-Topology
 * function of OrbisGIS.
 *
 * @author Adam Gouge
 */
public class GDMSGraphStorage extends LevelGraphStorage {

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
    private String weight;
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

    public GDMSGraphStorage(Directory dir, String weight) {
        super(dir);
        this.weight = weight;
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
                    "").equals(weight)) {
                weightIndex = i;
            }
        }
//        System.out.println("startNodeIndex: " + startNodeIndex
//                + ", endNodeIndex: " + endNodeIndex
//                + ", lengthIndex: " + lengthIndex);
    }

    /**
     * Loads the edges from the csv file into the given graph.
     *
     * @param scanner             The scanner that will parse the csv file.
     * @param graph               The graph to which the edges are to be added.
     * @param preserveOrientation {@code true} if the edges are considered to be
     *                            bidirectional. {@code false} if the orientation
     *                            of the edges is to be preserved.
     */
    public void loadEdges(Scanner scanner, Graph graph,
            boolean bothDirections) {
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
            graph.edge(startNode, endNode, length, bothDirections);
        }
    }

    /**
     * Prints out the edges of the given graph.
     *
     */
    public void printEdges() {
        EdgeIterator edgeIterator = this.getAllEdges();
        while (edgeIterator.next()) {
            System.out.println("EdgeID: " + edgeIterator.edge()
                    + ", " + START_NODE + " " + edgeIterator.fromNode()
                    + ", " + END_NODE + " " + edgeIterator.node()
                    + ", " + weight + " " + edgeIterator.distance());
        }
    }
}
