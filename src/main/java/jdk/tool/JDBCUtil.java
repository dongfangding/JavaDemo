package jdk.tool;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class JDBCUtil {
    /**
     * �版��搴���绉�
     */
    private static String dbName = "test";
    /**
     * �版��搴�杩��ョ�ㄦ�峰��
     */
    private static String dbUser = "root";
    /**
     * �版��搴�杩��ュ����
     */
    private static String dbPassword = "123456";
    /**
     * �版��搴�杩���ip
     */
    private static String ip = "localhost";
    /**
     * �版��搴�杩��ョ被��  ORACLE �� MYSQL
     */
    private String jdbcType = "MYSQL";
    private String driver = "";
    private String tableNameStr;
    private OracleTableEntity tableEntity;
    private String driverUrl = "";


    public String getDriver() {
        if ("ORACLE".equalsIgnoreCase(this.getJdbcType())) {
            return "oracle.jdbc.driver.OracleDriver";
        } else if ("MYSQL".equalsIgnoreCase(this.getJdbcType())) {
            return "com.mysql.jdbc.Driver";
        } else {
            return "oracle.jdbc.driver.OracleDriver";
        }
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getJdbcType() {
        return jdbcType;
    }

    public void setJdbcType(String jdbcType) {
        this.jdbcType = jdbcType;
    }

    public String getDriverUrl() {
        if ("ORACLE".equalsIgnoreCase(this.getJdbcType())) {
            return "jdbc:oracle:thin:@" + ip + ":1521:hdeditstdb";
        } else if ("MYSQL".equalsIgnoreCase(this.getJdbcType())) {
            return "jdbc:mysql://" + ip + "/information_schema?useUnicode=true&amp;characterEncoding=utf-8";
        } else {
            return "jdbc:oracle:thin:@" + ip + ":1521:hdeditstdb";
        }
    }

    public void setDriverUrl(String driverUrl) {
        this.driverUrl = driverUrl;
    }

    public OracleTableEntity getTableEntity() {
        return tableEntity;
    }

    public void setTableEntity(OracleTableEntity tableEntity) {
        this.tableEntity = tableEntity;
    }

    public String getCommentMapSql(String tableName) {
        if ("ORACLE".equalsIgnoreCase(this.getJdbcType())) {
            return "select   column_name,comments   from   all_col_comments   "
                    + "where Table_Name=\'" + tableName + "\' ";
        } else if ("MYSQL".equalsIgnoreCase(this.getJdbcType())) {
            return "select column_name,column_comment  from columns where table_schema='" + this.getDatabaseStr() + "' and table_name=\'" + tableName + "\'";
        } else {
            return "select   column_name,comments   from   all_col_comments   "
                    + "where Table_Name=\'" + tableName + "\' ";
        }
    }

    public String getDatabaseStr() {
        return dbName;
    }

    public Connection getStatement() throws ClassNotFoundException, SQLException {
        //1.准锟斤拷锟斤拷锟斤拷

        //2.锟斤拷锟斤拷锟斤拷实锟斤拷
        Class.forName(this.getDriver());

        //3.锟斤拷锟斤拷锟斤拷锟斤拷
        //锟斤拷锟斤拷锟街凤拷锟角固讹拷锟斤拷锟斤拷式,oracle锟斤拷锟斤拷式:
        String url
                = this.getDriverUrl();
        Connection conn = DriverManager.getConnection(url, dbUser, dbPassword);

        return conn;
    }

    /**
     * 锟斤拷锟斤拷锟斤拷斜锟�
     *
     * @return
     */
    public List<String> getTableList() {
        List<String> resList = new ArrayList<String>();

        Connection conn;
        try {
            conn = getStatement();

            //4.执锟斤拷SQL锟斤拷锟�
            String sql = "select table_name from user_tables";
            Statement stmt = conn.createStatement();
            ResultSet rs
                    = stmt.executeQuery(sql);//执锟斤拷sql锟斤拷锟�
            while (rs.next()) {
                String tableName = rs.getString(1);
                resList.add(tableName);
            }
            rs.close();
            stmt.close();
            conn.close();
        } catch (ClassNotFoundException e) {
            return resList;
        } catch (SQLException e) {
            return resList;
        }
        return resList;
    }

    /**
     * 锟斤拷帽锟斤拷锟斤拷锟较�
     *
     * @param tableName
     * @return
     */
    public List<OracleColumnEntity> getTableColumn(String tableName, String typeStr) {
        List<OracleColumnEntity> resList = new ArrayList<OracleColumnEntity>();

        Connection conn;
        try {
            conn = getStatement();

            //4.执锟斤拷SQL锟斤拷锟�
            String sql = "select column_name,data_type,data_precision,nullable,data_scale "
                    + "from user_tab_columns "
                    + "where Table_Name=\'" + tableName + "\' order by column_id asc";
            Statement stmt = conn.createStatement();
            ResultSet rs
                    = stmt.executeQuery(sql);//执锟斤拷sql锟斤拷锟�
            while (rs.next()) {
                OracleColumnEntity col = new OracleColumnEntity();
                String cn = rs.getString(1);
                col.setColumnName(cn);
                col.setDataType(rs.getString(2));
                col.setDataPrecision(rs.getString(3));
                col.setNullable(rs.getString(4));
                col.setDataScale(rs.getString(5));
                if ("baseDomain".equalsIgnoreCase(typeStr)) {
                    if ("CREATE_BY".equalsIgnoreCase(cn) || "CREATE_TIME".equalsIgnoreCase(cn)
                            || "MODIFY_BY".equalsIgnoreCase(cn) || "MODIFY_TIME".equalsIgnoreCase(cn)
                            || "COMP_CODE".equalsIgnoreCase(cn) || "VERSION".equalsIgnoreCase(cn)
                            || "UUID".equalsIgnoreCase(cn) || "REMOVED".equalsIgnoreCase(cn)) {
                    } else {
                        resList.add(col);
                    }
                } else if ("idDomain".equalsIgnoreCase(typeStr)) {
                    if ("CREATE_BY".equalsIgnoreCase(cn) || "CREATE_TIME".equalsIgnoreCase(cn)
                            || "MODIFY_BY".equalsIgnoreCase(cn) || "MODIFY_TIME".equalsIgnoreCase(cn)
                            || "COMP_CODE".equalsIgnoreCase(cn) || "VERSION".equalsIgnoreCase(cn)
                            || "UUID".equalsIgnoreCase(cn) || "REMOVED".equalsIgnoreCase(cn)) {
                    } else {
                        resList.add(col);
                    }
                } else {
                    resList.add(col);
                }
            }
            rs.close();
            stmt.close();
            conn.close();
        } catch (ClassNotFoundException e) {
            return resList;
        } catch (SQLException e) {
            return resList;
        }
        return resList;
    }

    public List<MysqlColumnEntity> getTableColumnMysql(String tableName, String typeStr) {
        List<MysqlColumnEntity> resList = new ArrayList<MysqlColumnEntity>();

        Connection conn;
        try {
            conn = getStatement();

            //4.执锟斤拷SQL锟斤拷锟�
            String sql = "select column_name,data_type,is_nullable "
                    + "from columns where table_schema=\'" + this.getDatabaseStr() + "\' "
                    + " and table_name=\'" + tableName + "\' order by ordinal_position asc";
            Statement stmt = conn.createStatement();
            ResultSet rs
                    = stmt.executeQuery(sql);//执锟斤拷sql锟斤拷锟�
            while (rs.next()) {
                MysqlColumnEntity col = new MysqlColumnEntity();
                String cn = rs.getString(1);
                col.setColumnName(cn);
                col.setDataType(rs.getString(2));
                col.setNullable(rs.getString(3));
                if ("baseDomain".equalsIgnoreCase(typeStr)) {
                    if ("CREATE_BY".equalsIgnoreCase(cn) || "CREATE_TIME".equalsIgnoreCase(cn)
                            || "MODIFY_BY".equalsIgnoreCase(cn) || "MODIFY_TIME".equalsIgnoreCase(cn)
                            || "COMP_CODE".equalsIgnoreCase(cn) || "VERSION".equalsIgnoreCase(cn)
                            || "UUID".equalsIgnoreCase(cn) || "REMOVED".equalsIgnoreCase(cn)
                            || "ID".equalsIgnoreCase(cn)) {
                    } else {
                        resList.add(col);
                    }
                } else if ("idDomain".equalsIgnoreCase(typeStr)) {
                    if ("CREATE_BY".equalsIgnoreCase(cn) || "CREATE_TIME".equalsIgnoreCase(cn)
                            || "MODIFY_BY".equalsIgnoreCase(cn) || "MODIFY_TIME".equalsIgnoreCase(cn)
                            || "COMP_CODE".equalsIgnoreCase(cn) || "VERSION".equalsIgnoreCase(cn)
                            || "UUID".equalsIgnoreCase(cn) || "REMOVED".equalsIgnoreCase(cn)
                            || "ID".equalsIgnoreCase(cn)) {
                    } else {
                        resList.add(col);
                    }
                } else {
                    resList.add(col);
                }
            }
            rs.close();
            stmt.close();
            conn.close();
        } catch (ClassNotFoundException e) {
            return resList;
        } catch (SQLException e) {
            return resList;
        }
        return resList;
    }

    public HashMap<String, String> getCommentMap(String tableName, String typeStr) {
        HashMap<String, String> map = new HashMap<String, String>();

        Connection conn;
        try {
            conn = getStatement();

            //4.执锟斤拷SQL锟斤拷锟�
            String sql = this.getCommentMapSql(tableName);
            Statement stmt = conn.createStatement();
            ResultSet rs
                    = stmt.executeQuery(sql);//执锟斤拷sql锟斤拷锟�
            while (rs.next()) {
                String cn = rs.getString(1);
                if ("baseDomain".equalsIgnoreCase(typeStr)) {
                    if ("CREATE_BY".equalsIgnoreCase(cn) || "CREATE_TIME".equalsIgnoreCase(cn)
                            || "MODIFY_BY".equalsIgnoreCase(cn) || "MODIFY_TIME".equalsIgnoreCase(cn)
                            || "COMP_CODE".equalsIgnoreCase(cn) || "VERSION".equalsIgnoreCase(cn)
                            || "UUID".equalsIgnoreCase(cn) || "REMOVED".equalsIgnoreCase(cn)) {
                    } else {
                        map.put(cn, rs.getString(2));
                    }
                } else if ("idDomain".equalsIgnoreCase(typeStr)) {
                    if ("CREATE_BY".equalsIgnoreCase(cn) || "CREATE_TIME".equalsIgnoreCase(cn)
                            || "MODIFY_BY".equalsIgnoreCase(cn) || "MODIFY_TIME".equalsIgnoreCase(cn)
                            || "COMP_CODE".equalsIgnoreCase(cn) || "VERSION".equalsIgnoreCase(cn)
                            || "UUID".equalsIgnoreCase(cn) || "REMOVED".equalsIgnoreCase(cn)) {
                    } else {
                        map.put(cn, rs.getString(2));
                    }
                } else {
                    map.put(cn, rs.getString(2));
                }
            }
            rs.close();
            stmt.close();
            conn.close();
        } catch (ClassNotFoundException e) {
            return map;
        } catch (SQLException e) {
            return map;
        }
        return map;
    }

    /**
     * 锟斤拷帽锟绞碉拷锟�
     *
     * @param tableName
     * @return
     */
    public OracleTableEntity getTableEntity(String tableName, String typeStr) {
        OracleTableEntity table = new OracleTableEntity();
        table.setTableName(tableName);
        table.setTableNameStr(StrUtil.tableNameRender(tableName));
        List<OracleColumn> list = getTableColumnEntity(tableName, typeStr);

        table.setCols(list);
        HashMap<String, String> map = getCommentMap(tableName, typeStr);
        table.setCommentMap(map);
        return table;
    }

    /**
     * 锟斤拷锟斤拷锟绞碉拷锟�
     *
     * @param tableName
     * @return
     */
    public List<OracleColumn> getTableColumnEntity(String tableName, String typeStr) {
        JDBCUtil util = new JDBCUtil();
        List<OracleColumn> resList = new ArrayList<OracleColumn>();

        if ("ORACLE".equalsIgnoreCase(this.getJdbcType())) {
            List<OracleColumnEntity> list = util.getTableColumn(tableName, typeStr);
            for (OracleColumnEntity s : list) {
                String dataType = "String";
                String type = s.getDataType();

                if ("NUMBER".equals(type)) {
                    int precision = Integer.valueOf(s.getDataPrecision());

                    if (s.getDataScale() == null) {
                        dataType = "Integer";
                    } else {
                        int dataScale = Integer.valueOf(s.getDataScale());
                        if (0 == dataScale) {
                            if (this.matchTheId(s.getColumnName())) {//������ID
                                dataType = "Long";
                            } else if (3 == precision) {
                                dataType = "Byte";
                            } else {
                                dataType = "Integer";
                            }
                        } else {
                            dataType = "Double";
                        }
                    }
                } else if ("VARCHAR2".equals(type) || "CLOB".equals(type)) {
                    dataType = "String";
                } else if ("TIMESTAMP(6)".equals(type) || "DATE".equals(type)) {
                    dataType = "Date";
                } else {
                    dataType = "String";
                }

                OracleColumn c = new OracleColumn();
                c.setColumnName(s.getColumnName());
                c.setColumnNameStr(StrUtil.colNameRender(s.getColumnName()));
                c.setDataType(dataType);
                resList.add(c);
            }
        } else {
            List<MysqlColumnEntity> list = util.getTableColumnMysql(tableName, typeStr);
            for (MysqlColumnEntity s : list) {
                String dataType = "String";
                String type = s.getDataType();

                if (("decimal".equalsIgnoreCase(type)) || ("double".equalsIgnoreCase(type))
                        || ("float".equalsIgnoreCase(type))) {
                    dataType = "Double";
                } else if ("tinyint".equalsIgnoreCase(type)) {
                    dataType = "Byte";
                } else if ("bigint".equalsIgnoreCase(type)) {
                    dataType = "Long";
                } else if ("int".equalsIgnoreCase(type)) {
                    if (this.matchTheId(s.getColumnName())) {//������ID
                        dataType = "Long";
                    } else {
                        dataType = "Integer";
                    }
                } else if ("varchar".equalsIgnoreCase(type) || "BLOB".equalsIgnoreCase(type)) {
                    dataType = "String";
                } else if ("datetime".equalsIgnoreCase(type) || "DATE".equalsIgnoreCase(type)) {
                    dataType = "Date";
                } else {
                    dataType = "String";
                }

                OracleColumn c = new OracleColumn();
                c.setColumnName(s.getColumnName());
                c.setColumnNameStr(StrUtil.colNameRender(s.getColumnName()));
                c.setDataType(dataType);
                resList.add(c);
            }
        }
        return resList;
    }

    public boolean matchTheId(String line) {
        String regex = ".*?_ID";
        Pattern pattern1 = Pattern.compile(regex);
        Matcher matcher1 = pattern1.matcher(line.toUpperCase());
        regex = "ID";
        Pattern pattern2 = Pattern.compile(regex);
        Matcher matcher2 = pattern2.matcher(line.toUpperCase());
        return (matcher1.matches()) || (matcher2.matches());
    }

    public String printTableHeader(OracleTableEntity table) {
        StringBuffer sb = new StringBuffer();
        sb.append("@Entity\n");
        sb.append("@org.hibernate.annotations.Entity(dynamicInsert = true)\n");
        sb.append("@Table(name = \"" + table.getTableName() + "\")\n");
        sb.append("@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)\n");
        return sb.toString();
    }

    public String printDefind(OracleTableEntity table) {
        List<OracleColumn> cols = table.getCols();
        HashMap<String, String> map = table.getCommentMap();
        StringBuffer sb = new StringBuffer();
        for (OracleColumn c : cols) {
            sb.append("	// " + map.get(c.getColumnName()) + "\n");
            sb.append("	private " + c.getDataType() + " " + c.getColumnNameStr() + ";\n");
        }
        return sb.toString();
    }

    public String printGene(OracleTableEntity table) {
        StringBuffer sb = new StringBuffer();
        sb.append("	public " + table.getTableNameStr() + "() {\n");
        sb.append("	}\n");
        return sb.toString();
    }

    public String printGet(OracleColumn c, String tableName) {
        String columnName = c.getColumnName();
        String columnNameStr = c.getColumnNameStr();
        StringBuffer sb = new StringBuffer();
        if ("Date".equals(c.getDataType())) {
            sb.append("	@Temporal(TemporalType.TIMESTAMP)\n");
        }
        if ("ID".equalsIgnoreCase(columnName)) {
            String sequenceName = "SEQ_" + tableName + "_ID";
            sb.append("	@Id\n");
            sb.append("	@GeneratedValue(strategy = GenerationType.SEQUENCE,  generator = \"" + sequenceName + "\")\n");
            sb.append("	@SequenceGenerator(name = \"" + sequenceName + "\",sequenceName = \"" + sequenceName + "\", allocationSize=1 )\n");
            sb.append("	@Column(insertable = true, updatable = false, name = \"" + columnName + "\")\n");
        } else {
            sb.append("	@Column(name = \"" + columnName + "\")\n");
        }
        sb.append("	public " + c.getDataType() + " get" + StrUtil.notFirstLetter(columnNameStr) + "() {\n");
        sb.append("		return " + columnNameStr + ";\n");
        sb.append("	}\n\n");
        return sb.toString();
    }

    public String printSet(OracleColumn c) {
        String columnNameStr = c.getColumnNameStr();
        String dataType = c.getDataType();
        StringBuffer sb = new StringBuffer();
        sb.append("	public void set" + StrUtil.notFirstLetter(columnNameStr) + "(" + dataType + " " + columnNameStr + ") {\n");
        sb.append("		this." + columnNameStr + " = " + columnNameStr + ";\n");
        sb.append("	}\n\n");
        return sb.toString();
    }

    public String printSetGet(OracleTableEntity table) {
        List<OracleColumn> cols = table.getCols();
        StringBuffer sb = new StringBuffer();
        for (OracleColumn c : cols) {
            sb.append(printGet(c, table.getTableName()));
            sb.append(printSet(c));
        }
        return sb.toString();
    }

    public String printEntity(String tableName, String typeStr) {
        StringBuffer sb = new StringBuffer();
        OracleTableEntity table = getTableEntity(tableName, typeStr);
        this.setTableEntity(table);
        String extendStr = " ";
        String heander = printTableHeader(table);
        String defindStr = printDefind(table);
        String gene = printGene(table);
        String setget = printSetGet(table);

        if ("baseDomain".equalsIgnoreCase(typeStr)) {
            extendStr = " extends BaseDomain implements Serializable";
        } else if ("idDomain".equalsIgnoreCase(typeStr)) {
            extendStr = " extends IdDomain implements Serializable";
        } else {
            extendStr = " ";
        }

        tableNameStr = table.getTableNameStr();
        sb.append(heander);
        sb.append("public class " + table.getTableNameStr() + extendStr + " {\n");
        sb.append(defindStr);
        sb.append("\n");
        sb.append(gene);
        sb.append("\n");
        sb.append(setget);
        sb.append("}\n");

        String printString = sb.toString();
        System.out.print(printString);
        return printString;
    }

    public String printDao() {
        StringBuffer sb = new StringBuffer();
        String tableStr = tableNameStr;


        sb.append("import com.hitisoft.fw.orm.jpa.BaseDao;\n");
        sb.append("public interface " + tableStr + "Dao extends BaseDao<" + tableStr + ", Long> {\n");
        sb.append("");

        sb.append("}\n");

        String printString = sb.toString();
        return printString;
    }

    public String printDaoImpl() {
        StringBuffer sb = new StringBuffer();
        String tableStr = tableNameStr;
        String tableDaoStr = tableStr + "Dao";
        String tableDaoImplStr = tableDaoStr + "Impl";


        sb.append("import org.springframework.stereotype.Repository;\n");
        sb.append("import com.hitisoft.fw.orm.jpa.JpaDao;\n");
        sb.append("@Repository\n");
        sb.append("public class " + tableDaoImplStr + " extends JpaDao<" + tableStr + ", Long> implements "
                + tableDaoStr + " {\n");
        sb.append("	public " + tableDaoImplStr + "() {\n");
        sb.append("		super(" + tableStr + ".class);\n");
        sb.append("	}\n");

        sb.append("}\n");

        String printString = sb.toString();
        return printString;
    }

    public String printService() {
        StringBuffer sb = new StringBuffer();
        String tableStr = tableNameStr;
        String tableDaoStr = tableStr + "Dao";
        String tableServiceStr = tableStr + "Service";

        sb.append("import java.util.List;\n");
        sb.append("import org.springframework.beans.factory.annotation.Autowired;\n");
        sb.append("import org.springframework.stereotype.Service;\n");
        sb.append("import org.springframework.transaction.annotation.Transactional;\n");
        sb.append("import com.hitisoft.fw.orm.util.HtQuery;\n");
        sb.append("\n");
        sb.append("@Service\n");
        sb.append("public class " + tableServiceStr + " {\n");
        sb.append("	@Autowired\n");
        sb.append("	private " + tableDaoStr + " dao;\n");
        sb.append("	@Autowired\n");
        sb.append("	private RequestContext requestContext;\n");
        sb.append("	@Autowired\n");
        sb.append("	private SessionContext sessionContext;\n");
        sb.append("\n");
        sb.append("	@Transactional\n");
        sb.append("	public List<" + tableStr + "> save(List<" + tableStr + "> entityList) {\n");
        sb.append("		return dao.saveByRowAction(entityList);\n");
        sb.append("	}\n");
        sb.append("\n");
        sb.append("	@Transactional(readOnly = true)\n");
        sb.append("	public List<" + tableStr + "> query(List<HtQuery> conditions) {\n");
        sb.append("		if (conditions.size() > 0) {\n");
        sb.append("			return dao.query(conditions);\n");
        sb.append(" 		} else {\n");
        sb.append("			return dao.findByProperties();\n");
        sb.append(" 		}\n");
        sb.append("	}\n");
        sb.append("\n");
        sb.append("}\n");

        String printString = sb.toString();
        return printString;
    }

    public String printRecord() {
        return this.printExt5Record();
    }

    public String printWebRecord() {
        StringBuffer sb = new StringBuffer();
        String tableStr = tableNameStr;
        OracleTableEntity theTableModel = this.getTableEntity();
        List<OracleColumn> cols = theTableModel.getCols();

        sb.append(tableStr + ": [\n");
        String csb = "";
        for (int i = 0; i < cols.size(); i++) {
            OracleColumn c = cols.get(i);
            if (i == 0) {
                csb = c.getColumnNameStr();
            } else if (i == (cols.size() - 1)) {
                csb += "," + c.getColumnNameStr() + "\n";
            } else {
                if ((i % 5) == 0) {
                    csb += "," + c.getColumnNameStr() + "\n";
                } else {
                    csb += "," + c.getColumnNameStr();
                }
            }
        }
        sb.append(csb);
        sb.append("]\n");

        String printString = sb.toString();
        return printString;
    }

    public String printExt5Record() {
        StringBuffer sb = new StringBuffer();
        OracleTableEntity theTableModel = this.getTableEntity();
        List<OracleColumn> cols = theTableModel.getCols();

        String csb = "";
        for (int i = 0; i < cols.size(); i++) {
            OracleColumn c = cols.get(i);
            String dataType = c.getDataType();
            String lineStr = JDBCUtil.checkIsNull(JDBCUtil.setType(dataType)) + "name: '" + c.getColumnNameStr() + "', type: '" + JDBCUtil.setType(dataType) +
                    JDBCUtil.checkDateType(JDBCUtil.setType(dataType)) + "'}";
            if (i == 0) {
                csb = lineStr;
            } else if (i == (cols.size() - 1)) {
                csb += ",\n" + lineStr;
            } else {
                csb += ",\n" + lineStr;
            }
        }
        sb.append(csb);

        String printString = sb.toString();
        return printString;
    }

    public static String setType(String fieldTypeName) {
        String retStr = "";
        // �村������瀛�绗�涓茬被��瀵瑰�����string
        if (fieldTypeName.equalsIgnoreCase("String") || fieldTypeName.equalsIgnoreCase("Byte")) {
            retStr = "string";
        }
        if (fieldTypeName.equalsIgnoreCase("Double") || fieldTypeName.equalsIgnoreCase("BigDecimal")) {
            retStr = "float";
        }
        if (fieldTypeName.equalsIgnoreCase("Long") || fieldTypeName.equalsIgnoreCase("Integer")) {
            retStr = "int";
        }
        // �ユ��绫诲��瀵瑰�����date
        if (fieldTypeName.equalsIgnoreCase("Date")) {
            retStr = "date";
        }
        return retStr;
    }

    public static String checkDateType(String str) {
        String retStr = "";
        if ("date".equals(str)) {
            retStr += "', dateFormat: 'Y-m-d H:i:s";
        }
        return retStr;
    }

    public static String checkIsNull(String str) {
        String retStr = "{";
        //if(!"float".equals(str)) {
        retStr += "allowNull: true, ";
        //}
        return retStr;
    }
}
