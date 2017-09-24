package com.xfj.lab.util.dbUtil;

import java.sql.*;
/**
 * Created by xfj on 2017/9/24
 */
public class DBConnectionUtil {
    public static final String URL = "jdbc:mysql://localhost/jenatest";
    public static final String NAME = "root";// 数据库用户名
    public static final String PASS = "root";// 数据库用户名密码
    //加载驱动�??
    static {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取连接
     *
     * @return Connection
     */
    public static Connection getConn() {
        try {
            return DriverManager.getConnection(URL, NAME, PASS);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 关闭连接
     *
     * @param conn
     * @param st
     * @param rs
     */
    public static void close(Connection conn, Statement st, ResultSet rs) {
        try {
            if (conn != null)
                conn.close();
            if (st != null)
                st.close();
            if (rs != null)
                rs.close();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
