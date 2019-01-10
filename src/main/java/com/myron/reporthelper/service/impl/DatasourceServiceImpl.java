package com.myron.reporthelper.service.impl;

import com.myron.reporthelper.entity.Datasource;
import com.myron.reporthelper.mapper.DataSourceMapper;
import com.myron.reporthelper.service.DatasourceService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * <p>
 * 数据源配置信息表 服务实现类
 * </p>
 *
 * @author 缪应江
 * @since 2018-12-27
 */
@Slf4j
@Service
public class DatasourceServiceImpl extends ServiceImpl<DataSourceMapper, Datasource> implements DatasourceService {
    /**
     * @param driverClass
     * @param url
     * @param user
     * @param password
     * @return
     */
    @Override
    public boolean testConnection(final String driverClass, final String url, final String user,
                                  final String password) {
        Connection conn = null;
        try {
            Class.forName(driverClass);
            conn = DriverManager.getConnection(url, user, password);
            return true;
        } catch (final Exception e) {
            log.error("testConnection", e);
            return false;
        } finally {
            this.releaseConnection(conn);
        }
    }



    private void releaseConnection(final Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (final SQLException ex) {
                log.error("测试数据库连接后释放资源失败", ex);
            }
        }
    }
}
