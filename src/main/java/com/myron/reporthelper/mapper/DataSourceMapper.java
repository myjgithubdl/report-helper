package com.myron.reporthelper.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.myron.reporthelper.entity.Datasource;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 数据源配置信息表 Mapper 接口
 * </p>
 *
 * @author Myron Miao
 * @since 2018-12-27
 */
public interface DataSourceMapper extends BaseMapper<Datasource> {

    /**
     * 查询列表
     *
     * @param params
     * @return
     */
    List<Map<String, Object>> getReportList(Map<String, Object> params);


    /**
     * 查询数量
     * @param params
     * @return
     */
    int getReportCount(Map<String, Object> params);

}
