package com.myron.reporthelper.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easydata.head.TheadColumn;
import com.easydata.head.TheadColumnTree;
import com.myron.reporthelper.bo.*;
import com.myron.reporthelper.bo.pair.TextValuePair;
import com.myron.reporthelper.db.query.QueryerFactory;
import com.myron.reporthelper.entity.Datasource;
import com.myron.reporthelper.entity.Report;
import com.myron.reporthelper.mapper.ReportMapper;
import com.myron.reporthelper.service.DatasourceService;
import com.myron.reporthelper.service.ReportService;
import com.sql.util.SQLUtil;
import net.sf.jsqlparser.JSQLParserException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 报表配置表 服务实现类
 * </p>
 *
 * @author 缪应江
 * @since 2018-12-27
 */
@Service
public class ReportServiceImpl extends ServiceImpl<ReportMapper, Report> implements ReportService {


    @Resource
    private DatasourceService dsService;

    @Resource
    private ReportMapper reportMapper;


    @Override
    public List<TheadColumn> getMetaDataColumns(int dsId, String sqlText) {
        ReportDataSource reportDataSource = getReportDataSource(dsId);
        try {
            sqlText = SQLUtil.replaceSQLParams(sqlText, new HashMap<>());
        } catch (JSQLParserException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
        return QueryerFactory.create(reportDataSource).parseMetaDataColumns(sqlText);
    }


    @Override
    public List<TextValuePair> querySelectOptionList(int dsId, String sqlText) {
        ReportDataSource reportDataSource = getReportDataSource(dsId);
        return QueryerFactory.create(reportDataSource).querySelectOptionList(sqlText);
    }


    @Override
    public ReportDataSource getReportDataSource(int dsId) {
        Datasource ds = this.dsService.getById(dsId);
        Map<String, Object> options = new HashMap<>(3);
        if (StringUtils.isNotEmpty(ds.getOptions())) {
            options = JSON.parseObject(ds.getOptions());
        }
        return new ReportDataSource(
                ds.getUid(),
                ds.getDriverClass(),
                ds.getJdbcUrl(), ds.getUser(), ds.getPassword(),
                ds.getQueryerClass(),
                ds.getPoolClass(),
                options);
    }


    @Override
    public List<Map<String, Object>> getReportList(Map<String, Object> params) {
        return this.reportMapper.getReportList(params);
    }

    @Override
    public int getReportCount(Map<String, Object> params) {
        return this.reportMapper.getReportCount(params);
    }

    @Override
    public Report getReportByUid(String uid) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("uid", uid);
        return reportMapper.selectOne(queryWrapper);
    }


    @Override
    public List<Report> getReportListByCategoryId(Integer categoryId) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("category_id", categoryId);
        queryWrapper.orderByAsc("sequence", "id");
        return reportMapper.selectList(queryWrapper);
    }


    @Override
    public List<Map<String, Object>> getAllCategoryAndReport() {
        return reportMapper.getAllCategoryAndReport();
    }
}
