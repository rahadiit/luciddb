/*
// $Id$
// Package org.eigenbase is a class library of data management components.
// Copyright (C) 2005-2005 The Eigenbase Project
// Copyright (C) 2005-2005 Disruptive Tech
// Copyright (C) 2005-2005 LucidEra, Inc.
//
// This program is free software; you can redistribute it and/or modify it
// under the terms of the GNU General Public License as published by the Free
// Software Foundation; either version 2 of the License, or (at your option)
// any later version approved by The Eigenbase Project.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/
package org.eigenbase.sql;

import org.eigenbase.reltype.*;
import org.eigenbase.sql.validate.*;
import org.eigenbase.sql.util.*;
import org.eigenbase.sql.parser.*;
import org.eigenbase.sql.test.*;
import org.eigenbase.sql.type.*;
import org.eigenbase.util.*;
import org.eigenbase.resource.*;

import java.util.*;

/**
 * The <code>VALUES</code> operator.
 *
 * @author John V. Sichi
 * @version $Id$
 */
public class SqlValuesOperator extends SqlSpecialOperator
{
    public SqlValuesOperator()
    {
        super("VALUES", SqlKind.Values);
    }

    public void unparse(
        SqlWriter writer,
        SqlNode [] operands,
        int leftPrec,
        int rightPrec)
    {
        writer.print("VALUES ");
        for (int i = 0; i < operands.length; i++) {
            if (i > 0) {
                writer.print(", ");
            }
            SqlNode operand = operands[i];
            operand.unparse(writer, 0, 0);
        }
    }

    public void test(SqlTester tester)
    {
        SqlOperatorTests.testValuesOperator(tester);
    }
}

// End SqlValuesOperator.java