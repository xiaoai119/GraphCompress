package com.xfj.lab.dao;

import org.apache.ibatis.session.SqlSession;

/**
 * Created By xfj on 2017/9/25
 */
public class IdResourceRefDao extends BaseDao{
    public String findeById(Long id){
        SqlSession sqlSession = this.getSessionFactory().openSession();
        try{
            return (String) sqlSession.selectOne("findById", id);
        }finally {
            sqlSession.close();
        }
    }
}
