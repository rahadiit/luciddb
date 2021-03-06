/*
// Licensed to DynamoBI Corporation (DynamoBI) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  DynamoBI licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at

//   http://www.apache.org/licenses/LICENSE-2.0

// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.
*/
package org.eigenbase.applib.cursor;

import java.sql.*;

import java.util.*;

import net.sf.farrago.syslib.*;

import org.eigenbase.applib.resource.*;


/**
 * Generates a range of integers based on the number of rows passed in, allowing
 * for custom initial values and step values.
 *
 * @author Kevin Secretan
 * @version $Id$
 */
public abstract class GenerateSequenceUdx
{
    //~ Methods ----------------------------------------------------------------

    /**
     * Simple range, unpartitioned.
     *
     * @param inputSet - table of rows to count range for.
     * @param startVal - starting value of the count, defaults to -1
     * @param stepVal - amount to increment the count, defaults to 1, cannot be
     * 0.
     * @param resultInserter - Handles the output.
     */
    public static void execute(
        ResultSet inputSet,
        Long startVal,
        Long stepVal,
        PreparedStatement resultInserter)
        throws ApplibException
    {
        execute(inputSet, null, startVal, stepVal, resultInserter);
    }

    /**
     * The fully general sequence generator, will start the sequence over if it
     * detects a new key, where a key is a unique column or intersection of
     * columns.
     *
     * @param columnNames - list of column names composing a unique key.
     *
     * @see execute(ResultSet,Long,Long,PreparedStatement)
     */
    public static void execute(
        ResultSet inputSet,
        List<String> columnNames,
        Long startVal,
        Long stepVal,
        PreparedStatement resultInserter)
        throws ApplibException
    {
        final long start = (startVal == null) ? 1L : startVal.longValue();
        final long step = (stepVal == null) ? 1L : stepVal.longValue();
        final boolean partition_range = (columnNames != null);
        if (step == 0) {
            throw ApplibResource.instance().IncrementByMustNotBeZero.ex();
        }

        ResultSetMetaData meta_data;
        final int columns;
        try {
            meta_data = inputSet.getMetaData();
            columns = meta_data.getColumnCount();
        } catch (SQLException e) {
            throw ApplibResource.instance().CannotGetMetaData.ex(e);
        }

        try {
            List<String> names = new ArrayList<String>();
            List<Object> current_partition = null;
            long counter = start - step;
            while (inputSet.next()) {
                int i = 1;
                for (; i <= columns; ++i) {
                    resultInserter.setObject(i, inputSet.getObject(i));
                    if (names.size() < columns) {
                        names.add(meta_data.getColumnName(i));
                    }
                }

                if (partition_range) {
                    if (current_partition == null) {
                        current_partition =
                            getPartitionValues(inputSet, columnNames);
                    }
                    List<Object> partition =
                        getPartitionValues(inputSet, columnNames);
                    int compare = compareKeys(current_partition, partition);
                    if (compare > 0) {
                        // row is out of order
                        throw ApplibResource.instance().InputRowsNotSorted.ex(
                            columnNames.toString(),
                            names.toString());
                    } else if (compare < 0) {
                        // new key value
                        counter = start;
                        current_partition =
                            getPartitionValues(inputSet, columnNames);
                    } else {
                        // same
                        counter += step;
                    }
                } else {
                    counter += step;
                }

                resultInserter.setLong(i, counter);
                resultInserter.executeUpdate();
            }
        } catch (SQLException e) {
            throw ApplibResource.instance().DatabaseAccessError.ex(
                e.toString(),
                e);
        }
    }

    /**
     * Gets all the values in the first row of the set for a list of given
     * column names.
     *
     * @param inputSet - set of values to draw from.
     * @param columnNames - set of column names for the set.
     *
     * @return set of the objects in order of columns.
     */
    private static List<Object> getPartitionValues(
        ResultSet inputSet,
        List<String> columnNames)
        throws SQLException
    {
        List<Object> vals = new ArrayList<Object>();
        for (String name : columnNames) {
            vals.add(inputSet.getObject(name));
        }
        return vals;
    }

    /**
     * Compares two equal-length sets of keys key-by-key.
     *
     * @param key_group1 - Left-hand keys
     * @param key_group2 - Right-hand keys
     *
     * @return 0 if the sets are equivalent, -1 if the RHS is ordered before the
     * LHS, 1 if the RHS comes after the LHS.
     */
    private static int compareKeys(
        List<Object> key_group1,
        List<Object> key_group2)
    {
        final int len1 = key_group1.size();
        final int len2 = key_group2.size();
        assert (len1 == len2);

        for (int i = 0; i < len1; ++i) {
            int compare =
                FarragoSyslibUtil.compareKeysUsingGroupBySemantics(
                    key_group1.get(i),
                    key_group2.get(i));
            if (compare != 0) {
                return compare;
            }
        }
        return 0;
    }
}

// End GenerateSequenceUdx.java
