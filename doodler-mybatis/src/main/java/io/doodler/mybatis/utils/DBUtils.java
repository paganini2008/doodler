package io.doodler.mybatis.utils;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.SqlSessionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Description:
 * Author: Vincent
 * Date: 2022/11/2 4:56 下午
 */
@Component
public class DBUtils {

    @Autowired
    private SqlSessionTemplate sqlSessionTemplate;

    public List<Map<String, Object>> executeQuerySql(String sql) {
        List<Map<String, Object>> list = new ArrayList<>();
        PreparedStatement pst = null;
        SqlSession session = getSqlSession();
        ResultSet result;
        try {
            pst = session.getConnection().prepareStatement(sql);
            result = pst.executeQuery();
            ResultSetMetaData md = result.getMetaData();
            int columnCount = md.getColumnCount();
            while (result.next()) {
                Map<String, Object> rowData = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    rowData.put(md.getColumnName(i), result.getObject(i));
                }
                list.add(rowData);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (pst != null) {
                try {
                    pst.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            closeSqlSession(session);
        }
        return list;
    }

    public void executeDDLSQL(List<String> sqlList) {
        if (CollectionUtils.isEmpty(sqlList)) {
            return;
        }
        SqlSession session = getSqlSession();
        Statement stmt = null;
        try {
            stmt = session.getConnection().createStatement();
            for (String x : sqlList) {
                stmt.addBatch(x);
            }
            stmt.executeBatch();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException sqlException) {
                    sqlException.printStackTrace();
                }
            }
            closeSqlSession(session);
        }
    }

    /**
     * 获取sqlSession
     *
     * @return
     */
    public SqlSession getSqlSession() {
        return SqlSessionUtils.getSqlSession(sqlSessionTemplate.getSqlSessionFactory(),
                sqlSessionTemplate.getExecutorType(), sqlSessionTemplate.getPersistenceExceptionTranslator());
    }

    /**
     * 关闭sqlSession
     *
     * @param session
     */
    public void closeSqlSession(SqlSession session) {
        SqlSessionUtils.closeSqlSession(session, sqlSessionTemplate.getSqlSessionFactory());
    }
}