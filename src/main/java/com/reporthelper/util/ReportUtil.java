package com.reporthelper.util;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSONObject;
import com.reporthelper.bo.*;
import com.reporthelper.bo.form.DateHtmlForm;
import com.reporthelper.bo.form.HtmlFormElement;
import com.reporthelper.bo.form.SelectHtmlForm;
import com.reporthelper.bo.form.TextHtmlForm;
import com.reporthelper.bo.pair.TextValuePair;
import com.reporthelper.db.query.Queryer;
import com.reporthelper.db.query.QueryerFactory;
import com.reporthelper.entity.Report;
import com.reporthelper.service.DatasourceService;
import com.reporthelper.service.ReportService;
import com.sql.constant.Constant;
import com.sql.entity.JdbcTemplateQueryParams;
import com.sql.util.ParamsUtil;
import com.sql.util.SQLUtil;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Types;
import java.util.*;

/**
 * @author Myron Miao
 * @date 2019-01-10
 */
@Slf4j
@Component
public class ReportUtil {
    private static ReportService reportService;

    private static DatasourceService dsService;

    @Autowired
    public ReportUtil(final ReportService reportService, DatasourceService dsService) {
        ReportUtil.reportService = reportService;
        ReportUtil.dsService = dsService;
    }


    /**
     * 如果报表启用分页则查询报表分页数据，否则查询全部数据
     *
     * @param report
     * @return
     */
    public static ReportParameter queryReportPageData(Report report, Map<String, Object> params) {
        try {
            ReportOptions options = report.parseOptions();
            //创建分页信息
            ReportPageInfo pageInfo = ReportPageInfo.builder()
                    .isEnablePage(options.getEnablePage() != null && options.getEnablePage() == 1)
                    .pageSize(options.getPageSize()).build();
            ReportParameter reportParameter = ReportParameter.builder().report(report).sqlText(report.getSqlText()).reportPageInfo(pageInfo).build();
            if (StringUtils.isBlank(report.getSqlText())) {
                return reportParameter;
            }

            ReportDataSource reportDataSource = reportService.getReportDataSource(report.getDsId());

            if (params.get("pageIndex") != null && StringUtils.isNotEmpty(params.get("pageIndex").toString())) {
                pageInfo.setPageIndex(Integer.parseInt(params.get("pageIndex").toString()));
            }
            if (params.get("pageSize") != null && StringUtils.isNotEmpty(params.get("pageSize").toString())) {
                pageInfo.setPageSize(Integer.parseInt(params.get("pageSize").toString()));
            }
            //totalRows>=0 不会触发查询数量操作
            if (params.get("totalRows") != null && StringUtils.isNotEmpty(params.get("totalRows").toString())) {
                pageInfo.setTotalRows(Integer.parseInt(params.get("totalRows").toString()));
            }

            reportParameter.setReportPageInfo(pageInfo);
            reportParameter.setMetaColumns(report.parseMetaColumns());

            Queryer query = QueryerFactory.create(reportDataSource, reportParameter);

            pageInfo = query.getReportParameter().getReportPageInfo();

            reportParameter.setReportPageInfo(pageInfo);

            //String sqlText = SQLUtil.replaceSQLParams2(report.getSqlText(), params);
            //pageInfo.setRows(query.queryForList(sqlText));

            //使用Spring JdbcTemplate做查询
            //JdbcTemplateQueryParams jdbcTemplateQueryParams = getJdbcTemplateQueryParams(report, report.getSqlText(), params);
            //List<Map<String, Object>> dataList = query.queryPageDataList(jdbcTemplateQueryParams.getSql(),
            // jdbcTemplateQueryParams.getArgValues(), jdbcTemplateQueryParams.getArgTypes());
            //使用Spring NamedParameterJdbcTemplate 做查询
            JdbcTemplateQueryParams jdbcTemplateQueryParams = getNamedParameterJdbcTemplateQueryParams(report, report.getSqlText(), params);
            List<Map<String, Object>> dataList = query.queryPageDataList(jdbcTemplateQueryParams.getSql(), params);
            pageInfo.setRows(dataList);

            return reportParameter;
        } catch (JSQLParserException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 获取Spring JdbcTemplate queryForList需要的查询参数
     *
     * @param report
     * @param params
     * @return
     * @throws JSQLParserException
     */
    public static JdbcTemplateQueryParams getJdbcTemplateQueryParams(Report report, String sql, Map<String, String> params) throws JSQLParserException {
        List<ReportQueryParameter> reportQueryParameters = report.parseQueryParams();
        JdbcTemplateQueryParams jdbcTemplateQueryParams = SQLUtil.sqlToJdbcTemplateQuery2(sql, params);

        //参数类型
        List<Integer> argTypeList = new ArrayList<>();
        List<Object> argValueList = new ArrayList<>();
        String[] argNames = jdbcTemplateQueryParams.getArgNames();

        if (reportQueryParameters != null) {
            Map<String, ReportQueryParameter> reportQueryParameterMap = new HashMap<>();
            reportQueryParameters.stream().forEach(a -> reportQueryParameterMap.put(a.getName(), a));
            for (String argName : argNames) {
                Object argValue = null;
                int argType = Types.VARCHAR;
                ReportQueryParameter reportQueryParameter = reportQueryParameterMap.get(argName);
                if (reportQueryParameter != null) {
                    if ("string".equals(reportQueryParameter.getDataType())) {
                        argValue = MapUtils.getString(params, argName, null);
                        argType = Types.VARCHAR;
                    } else if ("float".equals(reportQueryParameter.getDataType())) {
                        argValue = MapUtils.getDouble(params, argName, null);
                        argType = Types.FLOAT;
                    } else if ("integer".equals(reportQueryParameter.getDataType())) {
                        argValue = MapUtils.getInteger(params, argName, null);
                        argType = Types.VARCHAR;
                    } else {
                        argValue = MapUtils.getString(params, argName, null);
                        argType = Types.VARCHAR;
                    }
                } else {
                    argValue = MapUtils.getString(params, argName, null);
                    argType = Types.VARCHAR;
                }
                argTypeList.add(argType);
                argValueList.add(argValue);
            }
        }

        jdbcTemplateQueryParams.setArgTypes(argTypeList.stream().mapToInt(Integer::valueOf).toArray());
        jdbcTemplateQueryParams.setArgValues(argValueList.toArray());

        return jdbcTemplateQueryParams;
    }

    /**
     * 获取Spring JdbcTemplate queryForList需要的查询参数
     *
     * @param report
     * @param params
     * @return
     * @throws JSQLParserException
     */
    public static JdbcTemplateQueryParams getNamedParameterJdbcTemplateQueryParams(Report report, String sql, Map<String, Object> params) throws JSQLParserException {
        //List<ReportQueryParameter> reportQueryParameters = report.parseQueryParams();
        JdbcTemplateQueryParams jdbcTemplateQueryParams = SQLUtil.sqlToNamedParameterJdbcTemplateQuery(sql, params);
        return jdbcTemplateQueryParams;
    }


    /**
     * 获取报表的说明内容
     *
     * @param report
     * @param params
     * @return
     */
    public static ReportExplain getReportExplain(Report report, Map<String, Object> params) {
        try {
            ReportExplain reportExplain = report.parseReportExplain();
            if (reportExplain == null) {
                return null;
            }

            String html = StringUtils.trimToNull(reportExplain.getExplainHtml());
            if (html == null) {
                return null;
            }

            String sqlText = StringUtils.trimToNull(reportExplain.getExplainSqlText());
            if (sqlText != null) {

                //报表提示强制限制为查询100条记录
                ReportPageInfo reportPageInfo = ReportPageInfo.builder().isEnablePage(true).pageIndex(1).totalRows(100).pageSize(100).build();

                ReportDataSource reportDataSource = reportService.getReportDataSource(report.getDsId());
                ReportParameter reportParameter = ReportParameter.builder().report(report).sqlText(sqlText).reportPageInfo(reportPageInfo).build();
                Queryer query = QueryerFactory.create(reportDataSource, reportParameter);


                //sqlText = SQLUtil.replaceSQLParams2(sqlText, params);
                //List<Map<String, Object>> dataList = query.queryForList(sqlText);

                /*//使用Spring JdbcTemplate做查询
                JdbcTemplateQueryParams jdbcTemplateQueryParams = getJdbcTemplateQueryParams(report, sqlText, params);
                List<Map<String, Object>> dataList = query.queryDataList(jdbcTemplateQueryParams.getSql(),
                        jdbcTemplateQueryParams.getArgValues(), jdbcTemplateQueryParams.getArgTypes());*/


                //使用Spring NamedParameterJdbcTemplate 做查询
                JdbcTemplateQueryParams jdbcTemplateQueryParams = getNamedParameterJdbcTemplateQueryParams(report, sqlText, params);
                List<Map<String, Object>> dataList = query.queryPageDataList(jdbcTemplateQueryParams.getSql(), params);

                if (CollUtil.isNotEmpty(dataList)) {
                    int size = dataList.size();
                    for (int i = 0; i < size; i++) {
                        Map<String, Object> data = dataList.get(i);
                        String keyIndex = "";
                        if (size > 1) {
                            keyIndex = (i + 1) + "";
                        }
                        for (String key : data.keySet()) {
                            params.put(key.toLowerCase() + keyIndex, data.get(key));
                        }
                    }
                }
                html = ParamsUtil.replaceAllParams(html, Constant.REG_PARAMS_PATTERN, params, "");
            }
            reportExplain.setExplainHtml(html);
            return reportExplain;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    /**
     * 查询导出报表的数据
     *
     * @param report
     * @return
     */
    public static List<Map<String, Object>> getExportReportTableData(Report report, Map<String, Object> params) {
        List<Map<String, Object>> dataList = new ArrayList<>();
        //设置分页信息
        params.put("pageIndex", "1");
        params.put("pageSize", "10000");
        params.put("totalRows", null);

        ReportParameter reportParameter = queryReportPageData(report, params);
        //分页数量
        int pageNum = reportParameter.getReportPageInfo().getTotalPage();
        if (reportParameter.getReportPageInfo().getRows() != null) {
            dataList.addAll(reportParameter.getReportPageInfo().getRows());
        }

        //查询后面分页的数据
        for (int pageIndex = 2; pageIndex <= pageNum; pageIndex++) {
            params.put("pageIndex", pageIndex + "");
            ReportParameter reportParameter2 = queryReportPageData(report, params);
            dataList.addAll(reportParameter2.getReportPageInfo().getRows());
        }
        return dataList;

    }

    /**
     * 查询报表查询参数是选择框的参数
     *
     * @param report
     * @param params
     * @param reportQueryParameter
     * @return
     */
    public static List<TextValuePair> getSelectOptionData(Report report,
                                                          Map<String, Object> params,
                                                          ReportQueryParameter reportQueryParameter) {
        List<TextValuePair> optionList = new ArrayList<>();

        //值集合、主要用于去重
        HashSet<String> set = new HashSet<>();
        if ("sql".equals(reportQueryParameter.getDataSource())) {//来源是SQL
            try {

                ReportDataSource reportDataSource = reportService.getReportDataSource(report.getDsId());
                ReportParameter reportParameter = ReportParameter.builder().report(report).sqlText(report.getSqlText()).build();
                Queryer query = QueryerFactory.create(reportDataSource, reportParameter);

                /*String sqlText = SQLUtil.replaceSQLParams2(reportQueryParameter.getContent(), params);
                optionList = query.querySelectOptionList(sqlText);*/


                //使用Spring NamedParameterJdbcTemplate 做查询
                JdbcTemplateQueryParams jdbcTemplateQueryParams = getNamedParameterJdbcTemplateQueryParams(report, reportQueryParameter.getContent(), params);
                optionList = query.querySelectOptionList(jdbcTemplateQueryParams.getSql(), params);
            } catch (JSQLParserException e) {
                log.info("解析SQL语句出错,sql语句：" + report.getSqlText());
                log.info("查询参数：" + JSONObject.toJSONString(params));
                e.printStackTrace();
            }
        } else if ("text".equals(reportQueryParameter.getDataSource())) {//来源是text文本
            String[] optionSplits = StringUtils.split(reportQueryParameter.getContent(), '|');
            for (String option : optionSplits) {
                String[] nameValuePairs = StringUtils.split(option.trim(), ',');
                String value = StringUtils.isEmpty(nameValuePairs[0]) ? "" : nameValuePairs[0].trim();// select的option 的value
                String text = nameValuePairs.length > 1 ? (StringUtils.isEmpty(nameValuePairs[1]) ? "未设置" : nameValuePairs[1].trim()) : value;// select的option 的text
                if (!set.contains(value)) {
                    set.add(value);
                    optionList.add(new TextValuePair(value, text));
                }
            }
        }

        String defaultValue = StringUtils.trim(reportQueryParameter.getDefaultValue());
        String defaultText = StringUtils.trim(reportQueryParameter.getDefaultText());

        if (StringUtils.isNotEmpty(defaultValue) || StringUtils.isNotEmpty(defaultText)) {
            boolean optionIsDefaultValue = false;//在option中是否含有默认值
            for (TextValuePair t : optionList) {
                //设置默认值选中
                if (t.getValue().equals(defaultValue) || t.getText().equals(defaultText)) {
                    t.setSelected(true);
                    optionIsDefaultValue = true;
                }
            }

            if (!optionIsDefaultValue) {//在option中没有默认值则将默认值加入选项
                defaultText = StringUtils.isEmpty(defaultText) ? "未设置" : defaultText;
                optionList.add(0, new TextValuePair(defaultValue, defaultText, true));
            }
        }

        return optionList;
    }


    /**
     * 获取报表的查询参数
     *
     * @param report
     * @param params
     * @return
     */
    public static List<HtmlFormElement> getHtmlFormElement(Report report, Map<String, Object> params) {

        List<HtmlFormElement> htmlFormElementList = new ArrayList<>();
        List<ReportQueryParameter> reportQueryParameters = report.parseQueryParams();

        for (ReportQueryParameter reportQueryParameter : reportQueryParameters) {
            HtmlFormElement htmlFormElement;
            String defaultValue = "";
            if ("select".equals(reportQueryParameter.getFormElement())
                    || "selectMul".equals(reportQueryParameter.getFormElement())) {//选择框

                List<TextValuePair> options = getSelectOptionData(report, params, reportQueryParameter);
                boolean isMultiple = "selectMul".equals(reportQueryParameter.getFormElement());
                htmlFormElement = new SelectHtmlForm(isMultiple, reportQueryParameter.getTriggerParamName(), options);
            } else if ("text".equals(reportQueryParameter.getFormElement())) {//输入框
                htmlFormElement = new TextHtmlForm();
            } else if ("date".equals(reportQueryParameter.getFormElement())) {//日期
                htmlFormElement = new DateHtmlForm(reportQueryParameter.getDateFormat(), reportQueryParameter.getDateRange());
                //查找默认值
                if (reportQueryParameter.getDateRange() != null) {
                    if (StringUtils.isNotEmpty(reportQueryParameter.getDefaultValue()) ||
                            reportQueryParameter.getDateRange() == null) {
                        defaultValue = reportQueryParameter.getDefaultValue();
                    } else if (StringUtils.isNotEmpty(reportQueryParameter.getDateFormat())) {
                        if ("YYYY-MM-DD".equals(reportQueryParameter.getDateFormat())) {
                            Date date = DateUtil.addDate(new Date(), 0, 0, reportQueryParameter.getDateRange(), 0, 0, 0, 0);
                            defaultValue = DateUtil.parseDateToStr(date, DateUtil.DATE_FORMAT_YYYY_MM_DD);
                        } else if ("YYYYMMDD".equals(reportQueryParameter.getDateFormat())) {
                            Date date = DateUtil.addDate(new Date(), 0, 0, reportQueryParameter.getDateRange(), 0, 0, 0, 0);
                            defaultValue = DateUtil.parseDateToStr(date, DateUtil.DATE_FORMAT_YYYYMMDD);
                        } else if ("YYYY-MM".equals(reportQueryParameter.getDateFormat())) {
                            Date date = DateUtil.addDate(new Date(), 0, reportQueryParameter.getDateRange(), 0, 0, 0, 0, 0);
                            defaultValue = DateUtil.parseDateToStr(date, DateUtil.DATE_FORMAT_YYYY_MM);
                        } else if ("YYYYMM".equals(reportQueryParameter.getDateFormat())) {
                            Date date = DateUtil.addDate(new Date(), 0, reportQueryParameter.getDateRange(), 0, 0, 0, 0, 0);
                            defaultValue = DateUtil.parseDateToStr(date, DateUtil.DATE_FORMAT_YYYYMM);
                        } else if ("YYYY".equals(reportQueryParameter.getDateFormat())) {
                            Date date = DateUtil.addDate(new Date(), reportQueryParameter.getDateRange(), 0, 0, 0, 0, 0, 0);
                            defaultValue = DateUtil.parseDateToStr(date, DateUtil.DATE_FORMAT_YYYY);
                        } else {
                            Date date = DateUtil.addDate(new Date(), 0, 0, reportQueryParameter.getDateRange(), 0, 0, 0, 0);
                            defaultValue = DateUtil.parseDateToStr(date, DateUtil.DATE_FORMAT_YYYY_MM_DD);
                        }
                    } else {
                        defaultValue = DateUtil.parseDateToStr(new Date(), DateUtil.DATE_FORMAT_YYYY_MM_DD);
                    }
                }
            } else {
                htmlFormElement = new TextHtmlForm();
            }

            //设置节本的属性
            htmlFormElement.setHtmlFormElementValue(reportQueryParameter.getName(), reportQueryParameter.getText(),
                    reportQueryParameter.getDefaultText(), defaultValue,
                    reportQueryParameter.getTextWidth(), reportQueryParameter.getNameWidth(), reportQueryParameter.getHeight(), reportQueryParameter.getFormElement(),
                    reportQueryParameter.getDataType(), reportQueryParameter.getComment(), reportQueryParameter.isRequired());

            htmlFormElementList.add(htmlFormElement);

        }

        return htmlFormElementList;
    }


    /**
     * 查询参数名是triggerParamName的select option
     *
     * @param report
     * @param params
     * @param triggerParamName
     * @return
     */
    public static List<TextValuePair> reloadSelectParamOption(Report report, Map<String, Object> params, String triggerParamName) {
        List<ReportQueryParameter> reportQueryParameters = report.parseQueryParams();
        for (ReportQueryParameter reportQueryParameter : reportQueryParameters) {
            if (triggerParamName.equals(reportQueryParameter.getName())
                    && ("select".equals(reportQueryParameter.getFormElement())
                    || "selectMul".equals(reportQueryParameter.getFormElement()))) {
                List<TextValuePair> options = getSelectOptionData(report, params, reportQueryParameter);
                return options;
            }
        }
        return null;
    }


}
