package com.xfj.lab.dao;

/**
 * Created By xfj on 2017/9/25
 */
public class IdResourceDaoTest {
    public static void main(String args[]){
        IdResourceRefDao dao=new IdResourceRefDao();
        String s = dao.findeById(1L);
        System.out.println(s);
    }
}
