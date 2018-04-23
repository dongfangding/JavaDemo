package jdk.tool;

public class MysqlColumnEntity {
    private String columnName;
    private String dataType;
    private String nullable;

    public MysqlColumnEntity() {

    }

    public MysqlColumnEntity(String columnName, String dataType,
                             String dataPrecision, String nullable) {
        this.columnName = columnName;
        this.dataType = dataType;
        this.nullable = nullable;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getNullable() {
        return nullable;
    }

    public void setNullable(String nullable) {
        this.nullable = nullable;
    }

}
