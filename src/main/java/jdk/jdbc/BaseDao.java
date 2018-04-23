package jdk.jdbc;


import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 */
public class BaseDao {

    protected Connection conn = null;
    protected PreparedStatement pstmt = null;
    protected ResultSet rs = null;


    /**
     * 閫氳繃Apache Proxool 杩炴帴姹犺幏鍙栨暟鎹簱杩炴帴
     *
     * @return Connection
     */
    protected Connection getConnByProxool() {
        Connection conn = null;
        try {
            Class.forName("org.logicalcobwebs.proxool.ProxoolDriver");// 娉ㄥ唽椹卞姩
            // org.logicalcobwebs.proxool.configuration.PropertyConfigurator.configure("Proxool.properties");
            conn = DriverManager.getConnection("proxool.llsys"); // 鑾峰緱鏁版嵁搴撹繛鎺�
        } catch (ClassNotFoundException e) {
            System.out.println("鎵句笉鍒皃roxool椹卞姩绫伙紒");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("");
            e.printStackTrace();
        }

        return conn;
    }

    /**
     * 鎵цSQL璇彞锛屽彲浠ヨ繘琛屽(涓嶈繑鍥瀒dentity)銆佸垹銆佹敼鐨勬搷浣滐紝涓嶈兘鎵ц鏌ヨ
     *
     * @param sql   棰勭紪璇戠殑SQL璇彞
     * @param param 棰勭紪璇戠殑SQL璇彞涓殑'?'鍙傛暟鐨勫瓧绗︿覆鏁扮粍
     * @return 鍙楀奖鍝嶇殑琛屾暟
     */
    protected int executeSQL(String sql, String[] param) {
        int rowNum = 0; // 鏁版嵁搴撳彈褰卞搷鐨勮鏁�
        try {
            pstmt = this.conn.prepareStatement(sql); // 寰楀埌prepareStatement瀵硅薄
            if (param != null && param.length != 0) {
                for (int i = 0; i < param.length; i++) {
                    pstmt.setObject(i + 1, param[i]); // 涓洪缂栬瘧sql璁剧疆鍙傛暟
                }
            }
            rowNum = pstmt.executeUpdate(); // 鍙戦�SQL璇彞 杩斿洖鍙楀奖鍝嶇殑琛屾暟
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            releaseConnResource(null, pstmt, conn); // 閲婃斁璧勬簮
        }

        return rowNum; // 杩斿洖鍙楀奖鍝嶇殑琛屾暟
    }

    /**
     * 鎵цSQL璇彞锛屽彲浠ヨ繘琛屽(杩斿洖identity)鎿嶄綔锛屼笉鑳芥墽琛屾煡璇�
     *
     * @param sql   棰勭紪璇戠殑SQL璇彞
     * @param param 棰勭紪璇戠殑SQL璇彞涓殑'?'鍙傛暟鐨勫瓧绗︿覆鏁扮粍
     * @return 鍙楀奖鍝嶇殑琛屾暟
     */
    protected long executeInsert(String sql, String[] param) {
        long identityNum = 0; // 杩斿洖identity
        try {
            pstmt = this.conn.prepareStatement(sql,
                    Statement.RETURN_GENERATED_KEYS); // 鑾峰彇鑷閲忓璞�
            if (param != null && param.length != 0) {
                for (int i = 0; i < param.length; i++) {
                    pstmt.setObject(i + 1, param[i]); // 涓洪缂栬瘧sql璁剧疆鍙傛暟
                }
            }
            pstmt.executeUpdate(); // 鍙戦�SQL璇彞
            rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                identityNum = rs.getLong(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            releaseConnResource(null, pstmt, conn); // 閲婃斁璧勬簮
        }

        return identityNum; // 杩斿洖鍙楀奖鍝嶇殑琛屾暟
    }

    /**
     * 閲婃斁绯荤粺鏁版嵁搴撹繛鎺ヨ祫婧�
     *
     * @param conn 鏁版嵁搴撹繛鎺�
     * @param stmt 璇彞
     * @param rs   缁撴灉闆�
     */
    protected void releaseConnResource(ResultSet rs, PreparedStatement pstmt, Connection conn) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        if (pstmt != null) {
            try {
                pstmt.close();
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }


    /**
     * 鎵цsql璇彞锛岃繘琛屾煡璇㈠崟涓垨澶氫釜鏁版嵁鐨勬搷浣�
     */
    protected List<Map<String, Object>> getObject(String sql, String[] param) {

        List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
        Map<String, Object> data = null;
        try {
            pstmt = this.conn.prepareStatement(sql); // 寰楀埌prepareStatement瀵硅薄
            if (param != null && param.length != 0) {
                for (int i = 0; i < param.length; i++) {
                    pstmt.setObject(i + 1, param[i]); // 涓洪缂栬瘧sql璁剧疆鍙傛暟
                }
            }
            rs = pstmt.executeQuery();

            // 鍙栧緱缁撴灉闆嗗垪鏁�
            ResultSetMetaData rsmd = pstmt.getMetaData();
            int columnCount = rsmd.getColumnCount();
            if (rs.getRow() == 1) {
                rs.next();
                data = new HashMap<String, Object>();
                // 姣忓惊鐜竴鏉″皢鍒楀悕鍜屽垪鍊煎瓨鍏ap
                for (int i = 1; i <= columnCount; i++) {
                    data.put(rsmd.getColumnLabel(i), rs.getObject(rsmd
                            .getColumnLabel(i)));
                }
                datas.add(data);
                return datas;
            }

            // 鏋勯�娉涘瀷缁撴灉闆�
            while (rs.next()) {
                data = new HashMap<String, Object>();
                // 姣忓惊鐜竴鏉″皢鍒楀悕鍜屽垪鍊煎瓨鍏ap
                for (int i = 1; i <= columnCount; i++) {
                    data.put(rsmd.getColumnLabel(i), rs.getObject(rsmd
                            .getColumnLabel(i)));
                }
                // 灏嗘暣鏉℃暟鎹殑Map瀛樺叆鍒癓ist涓�
                datas.add(data);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            releaseConnResource(rs, pstmt, conn); // 閲婃斁璧勬簮
        }
        return datas;
    }

    /**
     * 閫氳繃鍙嶅皠鐢熸垚瀵硅薄闆嗗悎
     *
     * @param rs
     * @param clazz
     * @return
     * @throws Exception
     */
    protected List populateList(ResultSet rs, Class clazz) throws Exception {
        ResultSetMetaData metaData = rs.getMetaData();
        int colCount = metaData.getColumnCount();
        List ret = new ArrayList();
        Field[] fields = clazz.getDeclaredFields();
        while (rs.next()) {
            Object newInstance = clazz.newInstance();
            for (int i = 1; i <= colCount; i++) {
                try {
                    Object value = rs.getObject(i);
                    for (int j = 0; j < fields.length; j++) {
                        Field f = fields[j];
                        String fName = f.getName().replaceAll("_", "")
                                .toLowerCase();
                        String metaName = metaData.getColumnName(i)
                                .replaceAll("_", "").toLowerCase();
                        if (fName.equalsIgnoreCase(metaName)) {
                            if (value != null) {
                                if (value instanceof java.sql.Timestamp) {
                                    java.sql.Timestamp temp = (java.sql.Timestamp) value;
                                    value = new java.sql.Date(temp.getTime());
                                }

                                BeanUtils.copyProperty(newInstance,
                                        f.getName(), value);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            ret.add(newInstance);
        }
        return ret;

    }

    /**
     * 閫氳繃鍙嶅皠鐢熸垚鍗曚釜瀵硅薄
     *
     * @param rs
     * @param clazz
     * @return
     * @throws Exception
     */
    protected Object populateObj(ResultSet rs, Class clazz) throws Exception {
        ResultSetMetaData metaData = rs.getMetaData();
        int colCount = metaData.getColumnCount();
        List ret = new ArrayList();
        Object obj = new Object();
        Field[] fields = clazz.getDeclaredFields();

        while (rs.next()) {
            Object newInstance = clazz.newInstance();
            for (int i = 1; i <= colCount; i++) {
                try {
                    Object value = rs.getObject(i);
                    for (int j = 0; j < fields.length; j++) {
                        Field f = fields[j];
                        String fName = f.getName().replaceAll("_", "")
                                .toLowerCase();
                        String metaName = metaData.getColumnName(i)
                                .replaceAll("_", "").toLowerCase();
                        if (fName.equalsIgnoreCase(metaName)) {
                            if (value != null) {
                                if (value instanceof java.sql.Timestamp) {
                                    java.sql.Timestamp temp = (java.sql.Timestamp) value;
                                    value = new java.sql.Date(temp.getTime());
                                }

                                BeanUtils.copyProperty(newInstance,
                                        f.getName(), value);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            ret.add(newInstance);
            if (ret.size() > 0) {
                obj = ret.get(0);
            }
        }
        return obj;

    }

    /**
     * @param entityObj
     * @param tableName
     * @return 瀵硅薄鐨勯泦鍚�
     */
    public List getAllObjMapping(Object entityObj, String tableName) {
        List objList = new ArrayList();
        try {
            pstmt = this.conn.prepareStatement(" SELECT * FROM  " + tableName);
            rs = pstmt.executeQuery();
            objList = populateList(rs, entityObj.getClass());
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            releaseConnResource(rs, pstmt, conn);
        }
        return objList;
    }

}
