package ru.host.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO
 * Created by Zloy on 05.07.2017.
 */
public class MetaDto {

    private String schemaName;
    private String tableName;

    private List<Column> columns = new ArrayList<>();
    private List<PKConstraint> constaints = new ArrayList<>();

    public static MetaDto.Column createColumn(String columnName, String typeName, Integer columnSize, boolean nullable){
        return new Column(columnName, typeName, columnSize, nullable);
    }

    public static MetaDto.PKConstraint createConstraint(String columnName, String constraintName){
        return new PKConstraint(columnName, constraintName);
    }

    public MetaDto(String schemaName, String tableName) {
        this.schemaName = schemaName;
        this.tableName = tableName;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public List<PKConstraint> getConstaints() {
        return constaints;
    }


    public static class Column{
        private String columnName;
        private String typeName;
        private Integer columnSize;
        private boolean nullable;

        Column(String columnName, String typeName, Integer columnSize, boolean nullable) {
            this.columnName = columnName;
            this.typeName = typeName;
            this.columnSize = columnSize;
            this.nullable = nullable;
        }

        public String getSpecColomn() {
            return columnName + ' ' + typeName + ' ' +  (nullable ? "" : " not null");
        }

        public String getColumnName() {
            return columnName;
        }

        public void setColumnName(String columnName) {
            this.columnName = columnName;
        }

        public String getTypeName() {
            return typeName;
        }

        public void setTypeName(String typeName) {
            this.typeName = typeName;
        }

        public Integer getColumnSize() {
            return columnSize;
        }

        public void setColumnSize(Integer columnSize) {
            this.columnSize = columnSize;
        }

        public boolean isNullable() {
            return nullable;
        }

        public void setNullable(boolean nullable) {
            this.nullable = nullable;
        }
    }

    public static class PKConstraint{
        private String columnName;
        private String constraintName;

        PKConstraint(String columnName, String constraintName) {
            this.columnName = columnName;
            this.constraintName = constraintName;
        }

        public String getColumnName() {
            return columnName;
        }

        public void setColumnName(String columnName) {
            this.columnName = columnName;
        }

        public String getConstraintName() {
            return constraintName;
        }

        public void setConstraintName(String constraintName) {
            this.constraintName = constraintName;
        }
    }
}
