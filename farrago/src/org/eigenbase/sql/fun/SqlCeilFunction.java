/*
// $Id$
// Package org.eigenbase is a class library of data management components.
// Copyright (C) 2005-2005 The Eigenbase Project
// Copyright (C) 2002-2005 Disruptive Tech
// Copyright (C) 2005-2005 LucidEra, Inc.
// Portions Copyright (C) 2003-2005 John V. Sichi
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
package org.eigenbase.sql.fun;

import org.eigenbase.sql.*;
import org.eigenbase.sql.validate.SqlValidatorScope;
import org.eigenbase.sql.test.SqlTester;
import org.eigenbase.sql.test.SqlOperatorTests;
import org.eigenbase.sql.type.*;

// REVIEW jvs 12-May-2005:  I suggest you follow Intellij's recommendation
// and edit your template to match all of the other classes in this package.
/**
 * Created by IntelliJ IDEA.
 * User: jack
 * Date: Apr 13, 2005
 * Time: 2:17:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class SqlCeilFunction extends SqlFunction
{
    public SqlCeilFunction()
    {
        super("CEIL", SqlKind.Function,
            SqlTypeStrategies.rtiFirstArgType,
            null,
            SqlTypeStrategies.otcNumeric,
            SqlFunctionCategory.Numeric);

    }

    public void test(SqlTester tester)
    {
        SqlOperatorTests.testCeilFunc(tester);
    }

    public boolean isMonotonic(SqlCall call, SqlValidatorScope scope)
    {
        SqlNode node = (SqlNode)call.operands[0];
        return scope.isMonotonic(node);
    }
}