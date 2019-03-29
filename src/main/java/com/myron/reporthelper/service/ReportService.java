package com.myron.reporthelper.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easydata.head.TheadColumn;
import com.myron.reporthelper.bo.ReportDataSource;
import com.myron.reporthelper.entity.Report;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 报表配置表 服务类
 * </p>
 *
 * @author 缪应江
 * @since 2018-12-27
 */
public interface ReportService extends IService<Report> {


    /**
     * 根据数据源ID 和查询SQL解析出元数据列
     *
     * @param dsId
     * @param sqlText
     * @return
     */
    List<TheadColumn> getMetaDataColumns(int dsId, String sqlText);


    ReportDataSource getReportDataSource(int dsId);

    /**
     * 查询列表
     *
     * @param params
     * @return
     */
    List<Map<String, Object>> getReportList(Map<String, Object> params);


    /**
     * 查询列表
     *
     * @param params
     * @return
     */
    int getReportCount(Map<String, Object> params);


    /**
     * 根据uid查询报表
     *
     * @param uid
     * @return
     */
    Report getReportByUid(String uid);


    /**
     * 根据报表的分类查询列表
     *
     * @param categoryId
     * @return
     */
    List<Report> getReportListByCategoryId(Integer categoryId);


    /**
     * 获取所有的分类和报表
     *
     * @return
     */
    List<Map<String, Object>> getAllCategoryAndReport();

}
