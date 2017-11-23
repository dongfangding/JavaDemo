package main.java.jdk.tool;

import java.util.HashMap;
import java.util.List;

public class OracleTableEntity {
	private String tableName;
	private String tableNameStr;
	private List<OracleColumn> cols;
	private HashMap<String,String> commentMap;
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getTableNameStr() {
		return tableNameStr;
	}
	public void setTableNameStr(String tableNameStr) {
		this.tableNameStr = tableNameStr;
	}
	public List<OracleColumn> getCols() {
		return cols;
	}
	public void setCols(List<OracleColumn> cols) {
		this.cols = cols;
	}
	public HashMap<String, String> getCommentMap() {
		return commentMap;
	}
	public void setCommentMap(HashMap<String, String> commentMap) {
		this.commentMap = commentMap;
	}
	
	
}
