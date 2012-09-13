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
package de.jetsli.graph.util;

import java.nio.MappedByteBuffer;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * Put the usage of proprietary "sun" classes and after jdk6 classes into this class.
 * To use Helper class under Android as well.
 *
 * @author Peter Karich
 */
public class Helper7 {

    public static void cleanMappedByteBuffer(MappedByteBuffer mapping) {
        if (mapping == null)
            return;

        sun.misc.Cleaner cleaner = ((sun.nio.ch.DirectBuffer) mapping).cleaner();
        if (cleaner != null)
            cleaner.clean();
    }

    public static String getBeanMemInfo() {
        java.lang.management.OperatingSystemMXBean mxbean = java.lang.management.ManagementFactory.getOperatingSystemMXBean();
        com.sun.management.OperatingSystemMXBean sunmxbean = (com.sun.management.OperatingSystemMXBean) mxbean;
        long freeMemory = sunmxbean.getFreePhysicalMemorySize();
        long availableMemory = sunmxbean.getTotalPhysicalMemorySize();
        return "free:" + freeMemory / Helper.MB + ", available:" + availableMemory / Helper.MB
                + ", rfree:" + Runtime.getRuntime().freeMemory() / Helper.MB;
    }
    
    public static void close(XMLStreamReader r) {
        try {
            if (r != null)
                r.close();
        } catch (XMLStreamException ex) {
            throw new RuntimeException("Couldn't close xml reader", ex);
        }
    }
}
