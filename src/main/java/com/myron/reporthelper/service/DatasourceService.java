package com.myron.reporthelper.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.myron.reporthelper.entity.Datasource;
import com.myron.reporthelper.entity.Datasource;

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
     * 测试当前数据库连接
     *
     * @param driverClass
     * @param url
     * @param user
     * @param password
     */
    boolean testConnection(String driverClass, String url, String user, String password);

}
