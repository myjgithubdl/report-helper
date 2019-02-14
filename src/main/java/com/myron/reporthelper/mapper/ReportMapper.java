package com.myron.reporthelper.mapper;

import com.myron.reporthelper.entity.Report;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 报表配置表 Mapper 接口
 * </p>
 *
 * @author 缪应江
 * @since 2018-12-27
 */
public interface ReportMapper extends BaseMapper<Report> {

    /**
     * 查询列表
     * @param params
     * @return
     */
    List<Map<String,Object>> getReportList(Map<String,Object> params);


    int getReportCount(Map<String,Object> params);



    /**
     * 获取所有的分类和报表
     *
     * @return
     */
    List<Map<String, Object>> getAllCategoryAndReport();


}
