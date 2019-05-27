package com.reporthelper.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.reporthelper.entity.Datasource;
import com.reporthelper.entity.Datasource;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 数据源配置信息表 服务类
 * </p>
 *
 * @author 缪应江
 * @since 2018-12-27
 */
public interface DatasourceService extends IService<Datasource> {

    /**
     * 查询列表
     *
     * @param params
     * @return
     */
    List<Map<String, Object>> getReportList(Map<String, Object> params);


    int getReportCount(Map<String, Object> params);


    /**
     * 测试当前数据库连接
     *
     * @param driverClass
     * @param url
     * @param user
     * @param password
     */
    boolean testConnection(String driverClass, String url, String user, String password);

}
