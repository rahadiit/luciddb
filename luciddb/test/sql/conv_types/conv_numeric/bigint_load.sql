set schema 's'
;

-- Test will load all datatypes into a bigint column.

drop table datatype_target
;
create table datatype_target(col bigint)
;

-- tinyint to bigint; min/max range for source datatype [same as target]

insert into datatype_target
 select coltiny from datatype_source
  where target_type='bigint'
    and range_for='source'
;
select * from datatype_target
;

drop table datatype_target
;
create table datatype_target(col bigint)
;

-- smallint to bigint; min/max range for source datatype [same as target]

insert into datatype_target 
 select colsmall from datatype_source
  where target_type='bigint'
    and range_for='source'
;
select * from datatype_target
;

drop table datatype_target
;
create table datatype_target(col bigint)
;

-- integer to bigint; min/max range for source datatype [same as target]

insert into datatype_target
 select colint from datatype_source
  where target_type='bigint'
    and range_for='source'
;
select * from datatype_target
;

drop table datatype_target
;
create table datatype_target(col bigint)
;

-- bigint to bigint; min/max range for source datatype [same as target]

insert into datatype_target
 select colbig from datatype_source
  where target_type='bigint'
    and range_for='source'
;
select * from datatype_target
;

drop table datatype_target
;
create table datatype_target(col bigint)
;

-- decimal to bigint; min/max range for target datatype

insert into datatype_target
 select coldec from datatype_source
  where target_type='bigint'
    and range_for='target'
;
select * from datatype_target
;

drop table datatype_target
;
create table datatype_target(col bigint)
;

-- decimal to bigint; min/max range for source datatype

insert into datatype_target
 select coldec from datatype_source
  where target_type='bigint'
    and range_for='source'
;
select * from datatype_target
;

drop table datatype_target
;
create table datatype_target(col bigint)
;

-- numeric to bigint; min/max range for target datatype

insert into datatype_target
 select colnum from datatype_source
  where target_type='bigint'
    and range_for='target'
;
select * from datatype_target
;

drop table datatype_target
;
create table datatype_target(col bigint)
;

-- numeric to bigint; min/max range for source datatype

insert into datatype_target
 select colnum from datatype_source
  where target_type='bigint'
    and range_for='source'
;
select * from datatype_target
;

drop table datatype_target
;
create table datatype_target(col bigint)
;

-- double to bigint; min/max range for target datatype

insert into datatype_target
 select coldouble from datatype_source
  where target_type='bigint'
    and range_for='target'
;
select * from datatype_target
;

drop table datatype_target
;
create table datatype_target(col bigint)
;

-- double to bigint; min/max range for source datatype

insert into datatype_target
 select coldouble from datatype_source
  where target_type='bigint'
    and range_for='source'
;
select * from datatype_target
;

drop table datatype_target
;
create table datatype_target(col bigint)
;

-- float to bigint; min/max range for target datatype

insert into datatype_target
 select colfloat from datatype_source
  where target_type='bigint'
    and range_for='target'
;
select * from datatype_target
;

drop table datatype_target
;
create table datatype_target(col bigint)
;

-- float to bigint; min/max range for source datatype

insert into datatype_target
 select colfloat from datatype_source
  where target_type='bigint'
    and range_for='source'
;
select * from datatype_target
;

drop table datatype_target
;
create table datatype_target(col bigint)
;

-- real to bigint; min/max range for target datatype

insert into datatype_target
 select colreal from datatype_source
  where target_type='bigint'
    and range_for='target'
;
select * from datatype_target
;

drop table datatype_target
;
create table datatype_target(col bigint)
;

-- real to bigint; min/max range for source datatype

insert into datatype_target
 select colreal from datatype_source
  where target_type='bigint'
    and range_for='source'
;
select * from datatype_target
;

drop table datatype_target
;
create table datatype_target(col bigint)
;

-- test to drop scale

insert into datatype_target values(123.456789)
;
select * from datatype_target
;
-- PASS: if value = 123
