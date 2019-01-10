package com.myron.reporthelper.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.easydata.head.TheadColumn;
import com.easydata.head.TheadColumnTree;
import com.myron.reporthelper.bo.*;
import com.myron.reporthelper.entity.Report;
import com.baomidou.mybatisplus.extension.service.IService;
import com.myron.reporthelper.util.PageInfo;

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
     * @param dsId
     * @param sqlText
     * @return
     */
    List<TheadColumn> getMetaDataColumns(int dsId, String sqlText);


    List<ReportQueryParamItem> executeQueryParamSqlText(int dsId, String sqlText);

    /**
     * 解析json格式的报表元数据列为ReportMetaDataColumn对象集合
     *
     * @return List<TheadColumn>
     */
    List<TheadColumn> parseMetaColumns(String json);

    /**
     * 解析json格式的报表查询参数为QueryParameter对象集合
     *
     * @return List<QueryParameterPo>
     */
    List<ReportQueryParameter> parseQueryParams(String json);

    /**
     * 解析json格式的报表选项ReportOptions
     *
     * @return ReportOptions
     */
    ReportOptions parseOptions(String json);

    ReportDataSource getReportDataSource(int dsId);

    /**
     * 解析json格式的报表元数据列为 CompositionReport  对象集合
     *
     * @return List<CompositionReport>
     */
    List<CompositionReport> parseCompositionReport(String json);


    List<TheadColumnTree> parseMetaColumnTrees(String json);

    /**
     * 查询列表
     * @param params
     * @return
     */
    List<Map<String,Object>> getReportList(Map<String, Object> params);


    /**
     * 查询列表
     * @param params
     * @return
     */
    int getReportCount(Map<String, Object> params);


}
