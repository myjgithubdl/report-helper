package com.reporthelper.service.impl;

import com.reporthelper.entity.Datasource;
import com.reporthelper.mapper.DataSourceMapper;
import com.reporthelper.service.DatasourceService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

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

    @Autowired
    DataSourceMapper dataSourceMapper;

    @Override
    public List<Map<String, Object>> getReportList(Map<String, Object> params) {
        return this.dataSourceMapper.getReportList(params);
    }

    @Override
    public int getReportCount(Map<String, Object> params) {
        return this.dataSourceMapper.getReportCount(params);
    }




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
