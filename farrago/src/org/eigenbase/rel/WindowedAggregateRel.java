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
package org.eigenbase.rel;

import org.eigenbase.relopt.*;
import org.eigenbase.reltype.*;
import org.eigenbase.rex.*;


/**
 * A relational expression representing a set of window aggregates.
 *
 * <p>Rules:
 *
 * <ul>
 * <li>Created by {@link
 * net.sf.farrago.fennel.rel.WindowedAggSplitterRule}.
 * <li>Triggers {@link net.sf.farrago.fennel.rel.FennelWindowRule}.
 */
public final class WindowedAggregateRel
    extends SingleRel
{
    //~ Instance fields --------------------------------------------------------

    public final RexProgram program;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a WindowedAggregateRel.
     *
     * @param cluster
     * @param traits
     * @param child
     * @param program Program containing an array of expressions. The program
     * must not have a condition, and each expression must be either a {@link
     * RexLocalRef}, or a {@link RexOver} whose arguments are all {@link
     * RexLocalRef}.
     * @param rowType
     */
    public WindowedAggregateRel(
        RelOptCluster cluster,
        RelTraitSet traits,
        RelNode child,
        RexProgram program,
        RelDataType rowType)
    {
        super(cluster, traits, child);
        this.rowType = rowType;
        this.program = program;
        assert isValid(true);
    }

    //~ Methods ----------------------------------------------------------------

    public boolean isValid(boolean fail)
    {
        if (!program.isValid(fail)) {
            return false;
        }
        if (program.getCondition() != null) {
            assert !fail : "Agg program must not have condition";
            return false;
        }
        int i = -1;
        for (RexNode agg : program.getExprList()) {
            ++i;
            if (agg instanceof RexOver) {
                RexOver over = (RexOver) agg;
                for (int j = 0; j < over.operands.length; j++) {
                    RexNode operand = over.operands[j];
                    if (!(operand instanceof RexLocalRef)) {
                        assert !fail : "aggs[" + i + "].operand[" + j
                            + "] is not a RexLocalRef";
                        return false;
                    }
                }
            } else if (agg instanceof RexInputRef) {
                ;
            } else {
                assert !fail : "aggs[" + i + "] is a " + agg.getClass()
                    + ", expecting RexInputRef or RexOver";
            }
        }
        return true;
    }

    public RexProgram getProgram()
    {
        return program;
    }

    public void explain(RelOptPlanWriter pw)
    {
        program.explainCalc(this, pw);
    }

    public WindowedAggregateRel clone()
    {
        return new WindowedAggregateRel(
            getCluster(),
            traits.clone(),
            getChild(),
            program,
            rowType);
    }
}

// End WindowedAggregateRel.java
