package com.reporthelper.util;

import com.reporthelper.bo.ReportDataSource;
import com.reporthelper.db.pool.DataSourcePoolFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Jdbc工具类.
 *
 * @author Myron
 */
public class JdbcUtil {
    private static final Logger logger = LoggerFactory.getLogger(JdbcUtil.class);

    /**
     * 数据库连接池
     */
    private static final Map<String, DataSource> dataSourceMap = new ConcurrentHashMap<>(100);

    public static DataSource getDataSource(final ReportDataSource rptDs) {
        //用数据源用户名,密码,jdbcUrl做为key
        final String key = String.format("%s|%s|%s", rptDs.getUser(), rptDs.getPassword(), rptDs.getJdbcUrl())
                .toLowerCase();
        DataSource dataSource = dataSourceMap.get(key);
        if (dataSource == null) {
            dataSource = DataSourcePoolFactory.create(rptDs.getDbPoolClass()).wrap(rptDs);
            dataSourceMap.put(key, dataSource);
        }
        return dataSource;
    }

    public static void releaseJdbcResource(final Connection conn, final Statement stmt, final ResultSet rs) {
        try {
            if (stmt != null) {
                stmt.close();
            }
            if (rs != null) {
                rs.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (final SQLException ex) {
            logger.error("数据库资源释放异常", ex);
            throw new RuntimeException("数据库资源释放异常", ex);
        }
    }
}
