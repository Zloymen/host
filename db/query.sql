select * from information_schema.tables;
select * from information_schema.columns where column_name = 'id';
select * from information_schema.table_constraints where column_name = 'id';

select * from information_schema.key_column_usage where column_name = 'id';