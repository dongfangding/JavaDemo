package main.java.jdk.tool;

public class OracleColumn {
	private String columnName;
	private String dataType;
	private String columnNameStr;
	
	public OracleColumn(){
		
	}
	public OracleColumn(String columnName,String dataType,
			String columnNameStr){
		this.columnName = columnName;
		this.dataType = dataType;
		this.columnNameStr = columnNameStr;
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
	public String getColumnNameStr() {
		return columnNameStr;
	}
	public void setColumnNameStr(String columnNameStr) {
		this.columnNameStr = columnNameStr;
	}
	
	
}
