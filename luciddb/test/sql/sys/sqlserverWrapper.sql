-- test built-in sql server jdbc wrapper

-- check that it's been tagged for browse connect
select * from sys_boot.mgmt.browse_connect_foreign_wrappers 
where foreign_wrapper_name = 'SQL SERVER'
order by 2;

select * from table(
  sys_boot.mgmt.browse_connect_foreign_server(
    'SQL SERVER', 
    cursor(
      select '' as option_name, '' as option_value 
      from sys_boot.jdbc_metadata.empty_view)))
order by option_ordinal, option_choice_ordinal;

-- see options available
select * from table(
  sys_boot.mgmt.browse_connect_foreign_server(
    'SQL SERVER', 
    cursor(
      values('URL', 'jdbc:jtds:sqlserver://akela.lucidera.com:1433'),
            ('EXTENDED_OPTIONS', 'TRUE'))))
order by option_ordinal, option_choice_ordinal;

-- create sqlserver server without qualifying catalog name
create server my_mssql
foreign data wrapper "SQL SERVER"
options(
  url 'jdbc:jtds:sqlserver://akela.lucidera.com:1433',
  user_name 'schoi',
  password 'schoi',
  table_types 'TABLE'
);

-- browse foreign schemas will show default catalog schemas
select * from table( sys_boot.mgmt.browse_foreign_schemas('MY_MSSQL'))
order by schema_name;

drop server my_mssql cascade;

-- moved test to FRG-162-sqlserverWrapper
