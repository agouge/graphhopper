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
package com.graphhopper.storage;

import java.io.Closeable;

/**
 * Interface for a storage abstraction.
 *
 * @author Peter Karich
 */
public interface Storable<T> extends Closeable {

    /**
     * @return true if successfully loaded from persistent storage.
     */
    boolean loadExisting();

    /**
     * Creates the underlying storage. First operation if it cannot be loaded.
     */
    T create(long size);

    /**
     * This method makes sure that the underlying data is written to the
     * storage. Keep in mind that a disc normally has an IO cache so that
     * flush() is (less) probably not save against power loses.
     */
    void flush();

    /**
     * This method makes sure that the underlying used resources are released.
     * WARNING: it does NOT flush on close!
     */
    @Override
    void close();

    /**
     * @return the allocated storage size in bytes
     */
    long capacity();
}
