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
package org.eigenbase.test;

import java.util.*;

import org.eigenbase.reltype.*;
import org.eigenbase.sql.*;
import org.eigenbase.sql.parser.*;
import org.eigenbase.sql.type.*;
import org.eigenbase.sql.validate.*;


/**
 * Mock implementation of {@link SqlValidatorCatalogReader} which returns tables
 * "EMP", "DEPT", "BONUS", "SALGRADE" (same as Oracle's SCOTT schema).
 *
 * @author jhyde
 * @version $Id$
 */
public class MockCatalogReader
    implements SqlValidatorCatalogReader
{
    //~ Static fields/initializers ---------------------------------------------

    protected static final String defaultCatalog = "CATALOG";
    protected static final String defaultSchema = "SALES";

    //~ Instance fields --------------------------------------------------------

    protected final RelDataTypeFactory typeFactory;
    private final Map<List<String>, MockTable> tables =
        new HashMap<List<String>, MockTable>();
    protected final Map<String, MockSchema> schemas =
        new HashMap<String, MockSchema>();
    private RelDataType addressType;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a MockCatalogReader.
     *
     * @param typeFactory Type factory
     */
    public MockCatalogReader(RelDataTypeFactory typeFactory)
    {
        this(typeFactory, false);
        init();
    }

    /**
     * Creates a MockCatalogReader but does not initialize.
     *
     * <p>Protected constructor for use by subclasses, which must call
     * {@link #init} at the end of their public constructor.
     *
     * @param typeFactory Type factory
     * @param dummy Dummy parameter to distinguish from public constructor
     */
    protected MockCatalogReader(RelDataTypeFactory typeFactory, boolean dummy)
    {
        this.typeFactory = typeFactory;
        assert !dummy;
    }

    /**
     * Initializes this catalog reader.
     */
    protected void init()
    {
        final RelDataType intType =
            typeFactory.createSqlType(SqlTypeName.INTEGER);
        final RelDataType varchar10Type =
            typeFactory.createSqlType(SqlTypeName.VARCHAR, 10);
        final RelDataType varchar20Type =
            typeFactory.createSqlType(SqlTypeName.VARCHAR, 20);
        final RelDataType timestampType =
            typeFactory.createSqlType(SqlTypeName.TIMESTAMP);
        final RelDataType booleanType =
            typeFactory.createSqlType(SqlTypeName.BOOLEAN);
        final RelDataType rectilinearCoordType =
            typeFactory.createStructType(
                new RelDataType[] { intType, intType },
                new String[] { "X", "Y" });

        // TODO jvs 12-Feb-2005: register this canonical instance with type
        // factory
        addressType =
            new ObjectSqlType(
                SqlTypeName.STRUCTURED,
                new SqlIdentifier("ADDRESS", SqlParserPos.ZERO),
                false,
                new RelDataTypeField[] {
                    new RelDataTypeFieldImpl("STREET", 0, varchar20Type),
                    new RelDataTypeFieldImpl("CITY", 1, varchar20Type),
                    new RelDataTypeFieldImpl("ZIP", 1, intType),
                    new RelDataTypeFieldImpl("STATE", 1, varchar20Type)
                },
                RelDataTypeComparability.None);

        // Register "SALES" schema.
        MockSchema salesSchema = new MockSchema("SALES");
        registerSchema(salesSchema);

        // Register "EMP" table.
        MockTable empTable = new MockTable(salesSchema, "EMP");
        empTable.addColumn("EMPNO", intType);
        empTable.addColumn("ENAME", varchar20Type);
        empTable.addColumn("JOB", varchar10Type);
        empTable.addColumn("MGR", intType);
        empTable.addColumn("HIREDATE", timestampType);
        empTable.addColumn("SAL", intType);
        empTable.addColumn("COMM", intType);
        empTable.addColumn("DEPTNO", intType);
        empTable.addColumn("SLACKER", booleanType);
        registerTable(empTable);

        // Register "DEPT" table.
        MockTable deptTable = new MockTable(salesSchema, "DEPT");
        deptTable.addColumn("DEPTNO", intType);
        deptTable.addColumn("NAME", varchar10Type);
        registerTable(deptTable);

        // Register "BONUS" table.
        MockTable bonusTable = new MockTable(salesSchema, "BONUS");
        bonusTable.addColumn("ENAME", varchar20Type);
        bonusTable.addColumn("JOB", varchar10Type);
        bonusTable.addColumn("SAL", intType);
        bonusTable.addColumn("COMM", intType);
        registerTable(bonusTable);

        // Register "SALGRADE" table.
        MockTable salgradeTable = new MockTable(salesSchema, "SALGRADE");
        salgradeTable.addColumn("GRADE", intType);
        salgradeTable.addColumn("LOSAL", intType);
        salgradeTable.addColumn("HISAL", intType);
        registerTable(salgradeTable);

        // Register "EMP_ADDRESS" table
        MockTable contactAddressTable =
            new MockTable(salesSchema, "EMP_ADDRESS");
        contactAddressTable.addColumn("EMPNO", intType);
        contactAddressTable.addColumn("HOME_ADDRESS", addressType);
        contactAddressTable.addColumn("MAILING_ADDRESS", addressType);
        registerTable(contactAddressTable);

        // Register "CUSTOMER" schema.
        MockSchema customerSchema = new MockSchema("CUSTOMER");
        registerSchema(customerSchema);

        // Register "CONTACT" table.
        MockTable contactTable = new MockTable(customerSchema, "CONTACT");
        contactTable.addColumn("CONTACTNO", intType);
        contactTable.addColumn("FNAME", varchar10Type);
        contactTable.addColumn("LNAME", varchar10Type);
        contactTable.addColumn("EMAIL", varchar20Type);
        contactTable.addColumn("COORD", rectilinearCoordType);
        registerTable(contactTable);

        // Register "ACCOUNT" table.
        MockTable accountTable = new MockTable(customerSchema, "ACCOUNT");
        accountTable.addColumn("ACCTNO", intType);
        accountTable.addColumn("TYPE", varchar20Type);
        accountTable.addColumn("BALANCE", intType);
        registerTable(accountTable);
    }

    //~ Methods ----------------------------------------------------------------

    protected void registerTable(MockTable table)
    {
        table.onRegister(typeFactory);
        tables.put(
            Arrays.asList(table.getQualifiedName()),
            table);
    }

    protected void registerSchema(MockSchema schema)
    {
        schemas.put(schema.name, schema);
    }

    public SqlValidatorTable getTable(final String [] names)
    {
        switch (names.length) {
        case 1:
            // assume table in SALES schema (the original default)
            // if it's not supplied, because SqlValidatorTest is effectively
            // using SALES as its default schema.
            return tables.get(
                Arrays.asList(defaultCatalog, defaultSchema, names[0]));
        case 2:
            return tables.get(
                Arrays.asList(defaultCatalog, names[0], names[1]));
        case 3:
            return tables.get(Arrays.asList(names));
        default:
            return null;
        }
    }

    public RelDataType getNamedType(SqlIdentifier typeName)
    {
        if (typeName.equalsDeep(
                addressType.getSqlIdentifier(),
                false))
        {
            return addressType;
        } else {
            return null;
        }
    }

    public List<SqlMoniker> getAllSchemaObjectNames(List<String> names)
    {
        List<SqlMoniker> result;
        switch (names.size()) {
        case 0:
            // looking for schema names
            result = new ArrayList<SqlMoniker>();
            for (MockSchema schema : schemas.values()) {
                result.add(
                    new SqlMonikerImpl(schema.name, SqlMonikerType.Schema));
            }
            return result;
        case 1:
            // looking for table names in the given schema
            MockSchema schema = schemas.get(names.get(0));
            if (schema == null) {
                return Collections.emptyList();
            }
            result = new ArrayList<SqlMoniker>();
            for (String tableName : schema.tableNames) {
                result.add(
                    new SqlMonikerImpl(
                        tableName,
                        SqlMonikerType.Table));
            }
            return result;
        default:
            return Collections.emptyList();
        }
    }

    public String getSchemaName()
    {
        return defaultSchema;
    }

    //~ Inner Classes ----------------------------------------------------------

    public static class MockSchema
    {
        private final List<String> tableNames = new ArrayList<String>();
        private String name;

        public MockSchema(String name)
        {
            this.name = name;
        }

        public void addTable(String name)
        {
            tableNames.add(name);
        }

        public String getCatalogName()
        {
            return defaultCatalog;
        }
    }

    /**
     * Mock implementation of {@link SqlValidatorTable}.
     */
    public static class MockTable
        implements SqlValidatorTable
    {
        private final List<String> columnNameList = new ArrayList<String>();
        private final List<RelDataType> columnTypeList =
            new ArrayList<RelDataType>();
        private RelDataType rowType;
        private final String [] names;

        public MockTable(MockSchema schema, String name)
        {
            this.names =
                new String[] {
                    schema.getCatalogName(), schema.name, name
                };
            schema.addTable(name);
        }

        public RelDataType getRowType()
        {
            return rowType;
        }

        public void onRegister(RelDataTypeFactory typeFactory)
        {
            rowType =
                typeFactory.createStructType(
                    columnTypeList,
                    columnNameList);
        }

        public String [] getQualifiedName()
        {
            return names;
        }

        public SqlMonotonicity getMonotonicity(String columnName)
        {
            return SqlMonotonicity.NotMonotonic;
        }

        public SqlAccessType getAllowedAccess()
        {
            return SqlAccessType.ALL;
        }

        public void addColumn(int index, String name, RelDataType type)
        {
            columnNameList.add(index, name);
            columnTypeList.add(index, type);
        }

        public void addColumn(String name, RelDataType type)
        {
            columnNameList.add(name);
            columnTypeList.add(type);
        }
    }
}

// End MockCatalogReader.java
