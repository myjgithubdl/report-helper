package com.reporthelper.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easydata.head.TheadColumn;
import com.reporthelper.bo.ReportDataSource;
import com.reporthelper.bo.ReportParameter;
import com.reporthelper.db.query.QueryerFactory;
import com.reporthelper.entity.Datasource;
import com.reporthelper.entity.Report;
import com.reporthelper.mapper.ReportMapper;
import com.reporthelper.service.DatasourceService;
import com.reporthelper.service.ReportService;
import com.sql.util.SQLUtil;
import net.sf.jsqlparser.JSQLParserException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 报表配置表 服务实现类
 * </p>
 *
 * @author Myron
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
        Report report = Report.builder().id(-1).name("解析元数据列").build();
        ReportParameter reportParameter = ReportParameter.builder().report(report).sqlText(sqlText).build();
        return QueryerFactory.create(reportDataSource, reportParameter).parseMetaDataColumns(sqlText);
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
