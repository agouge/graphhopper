/*
 *  Licensed to Peter Karich under one or more contributor license 
 *  agreements. See the NOTICE file distributed with this work for 
 *  additional information regarding copyright ownership.
 * 
 *  Peter Karich licenses this file to you under the Apache License, 
 *  Version 2.0 (the "License"); you may not use this file except 
 *  in compliance with the License. You may obtain a copy of the 
 *  License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.graphhopper.routing;

import com.graphhopper.routing.util.DefaultEdgeFilter;
import com.graphhopper.routing.util.EdgeFilter;
import com.graphhopper.routing.util.VehicleEncoder;
import com.graphhopper.routing.util.ShortestCalc;
import com.graphhopper.routing.util.WeightCalculation;
import com.graphhopper.storage.EdgeEntry;
import com.graphhopper.storage.Graph;
import com.graphhopper.util.EdgeIterator;

/**
 * @author Peter Karich
 */
public abstract class AbstractRoutingAlgorithm implements RoutingAlgorithm {

    protected Graph graph;
    private EdgeFilter additionalEdgeFilter;
    protected WeightCalculation weightCalc;
    protected final EdgeFilter outEdgeFilter;
    protected final EdgeFilter inEdgeFilter;
    protected final VehicleEncoder flagEncoder;

    public AbstractRoutingAlgorithm(Graph graph, VehicleEncoder encoder) {
        this.graph = graph;
        this.additionalEdgeFilter = EdgeFilter.ALL_EDGES;
        type(new ShortestCalc());   
        this.flagEncoder = encoder;
        outEdgeFilter = new DefaultEdgeFilter(encoder, false, true);
        inEdgeFilter = new DefaultEdgeFilter(encoder, true, false);        
    }

    public RoutingAlgorithm edgeFilter(EdgeFilter additionalEdgeFilter) {
        this.additionalEdgeFilter = additionalEdgeFilter;
        return this;
    }

    protected boolean accept(EdgeIterator iter) {
        return additionalEdgeFilter.accept(iter);
    }

    protected EdgeIterator neighbors(int neighborNode) {
        return graph.getEdges(neighborNode, outEdgeFilter);
    }

    @Override public RoutingAlgorithm type(WeightCalculation wc) {
        this.weightCalc = wc;
        return this;
    }

    protected void updateShortest(EdgeEntry shortestDE, int currLoc) {
    }

    @Override public String toString() {
        return name() + "|" + weightCalc;
    }

    @Override public String name() {
        return getClass().getSimpleName();
    }
}
