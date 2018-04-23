package jdk.tool;

public class OracleColumnEntity {
    private String columnName;
    private String dataType;
    private String dataPrecision;
    private String nullable;
    private String dataScale;

    public OracleColumnEntity() {

    }

    public OracleColumnEntity(String columnName, String dataType,
                              String dataPrecision, String nullable) {
        this.columnName = columnName;
        this.dataType = dataType;
        this.dataPrecision = dataPrecision;
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

    public String getDataPrecision() {
        return dataPrecision;
    }

    public void setDataPrecision(String dataPrecision) {
        this.dataPrecision = dataPrecision;
    }

    public String getNullable() {
        return nullable;
    }

    public void setNullable(String nullable) {
        this.nullable = nullable;
    }

    public String getDataScale() {
        return dataScale;
    }

    public void setDataScale(String dataScale) {
        this.dataScale = dataScale;
    }

}
