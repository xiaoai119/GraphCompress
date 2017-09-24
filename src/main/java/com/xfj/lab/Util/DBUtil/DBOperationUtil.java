package com.xfj.lab.Util.DBUtil;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
/**
 * Created by xfj on 2017/9/24
 */
public class DBOperationUtil {
    /**
     *
     * @param sql
     * @return
     */
    public static boolean excute(String sql) {
        Connection con = DBConnectionUtil.getConn();
        //System.out.println("con");
        Statement stmt = null;
        try {
            stmt = con.createStatement();
            int num = stmt.executeUpdate(sql);
            return num > 0 ? true : false;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        } finally {
            DBConnectionUtil.close(con, stmt, null);
        }
    }

    /**
     * 执行查询
     *
     * @param sql
     * @return ResultSet
     */
    public static ResultSet query(String sql) {
        Connection con = DBConnectionUtil.getConn();
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery(sql);
            return rs;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }
}
