package com.myron.reporthelper.util;

import com.alibaba.fastjson.JSONObject;
import com.myron.reporthelper.bo.*;
import com.myron.reporthelper.bo.form.DateHtmlForm;
import com.myron.reporthelper.bo.form.HtmlFormElement;
import com.myron.reporthelper.bo.form.SelectHtmlForm;
import com.myron.reporthelper.bo.form.TextHtmlForm;
import com.myron.reporthelper.bo.pair.TextValuePair;
import com.myron.reporthelper.db.query.Queryer;
import com.myron.reporthelper.db.query.QueryerFactory;
import com.myron.reporthelper.entity.Report;
import com.myron.reporthelper.service.DatasourceService;
import com.myron.reporthelper.service.ReportService;
import com.sql.util.ParamsUtil;
import com.sql.util.SQLUtil;
import io.swagger.models.parameters.QueryParameter;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
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


    public ReportParameter queryReportData(Report report, Map<String, String> requestParams) {


        ReportParameter reportParameter = ReportUtil.queryReportPageData(report, requestParams);

        return reportParameter;
    }


    /**
     * 解析报表参数
     *
     * @param report
     * @return
     */
    public static ReportOptions getReportOptions(Report report) {
        if (report != null) {
            return JSONObject.parseObject(report.getOptions(), ReportOptions.class);
        }
        return null;
    }

    /**
     * 如果报表启用分页则查询报表分页数据，否则查询全部数据
     *
     * @param report
     * @return
     */
    public static ReportParameter queryReportPageData(Report report, Map<String, String> params) {
        try {
            ReportDataSource reportDataSource = reportService.getReportDataSource(report.getDsId());
            ReportOptions options = getReportOptions(report);

            //创建分页信息
            ReportPageInfo pageInfo = ReportPageInfo.builder()
                    .isEnablePage(options.getEnablePage() != null && options.getEnablePage() == 1)
                    .pageSize(options.getPageSize()).build();

            if (StringUtils.isNotEmpty(params.get("pageIndex"))) {
                pageInfo.setPageIndex(Integer.parseInt(params.get("pageIndex")));
            }
            if (StringUtils.isNotEmpty(params.get("pageSize"))) {
                pageInfo.setPageSize(Integer.parseInt(params.get("pageSize")));
            }
            //totalRows>=0 不会触发查询数量操作
            if (StringUtils.isNotEmpty(params.get("totalRows"))) {
                pageInfo.setTotalRows(Integer.parseInt(params.get("totalRows")));
            }

            ReportParameter reportParameter = new ReportParameter();
            reportParameter.setReportPageInfo(pageInfo);
            reportParameter.setMetaColumns(report.parseMetaColumns());

            Queryer query = QueryerFactory.create(reportDataSource, reportParameter);

            String sqlText = SQLUtil.replaceSQLParams2(report.getSqlText(), params);

            pageInfo = query.getReportParameter().getReportPageInfo();

            reportParameter.setReportPageInfo(pageInfo);

            pageInfo.setRows(query.queryForList(sqlText));
            return reportParameter;
        } catch (JSQLParserException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取报表的说明内容
     *
     * @param report
     * @param params
     * @return
     */
    public static ReportExplain getReportExplain(Report report, Map<String, String> params) {
        try {
            ReportExplain reportExplain = report.parseReportExplain();
            if (reportExplain == null) {
                return null;
            }

            String html = StringUtils.trimToNull(reportExplain.getHtml());
            String sqlText = StringUtils.trimToNull(reportExplain.getSqlText());
            if (sqlText != null) {
                ReportDataSource reportDataSource = reportService.getReportDataSource(report.getDsId());
                ReportParameter reportParameter = new ReportParameter();
                Queryer query = QueryerFactory.create(reportDataSource, reportParameter);
                sqlText = SQLUtil.replaceSQLParams2(sqlText, params);
                List<Map<String, Object>> dataList = query.queryForList(sqlText);
                StringBuilder stringBuilder = new StringBuilder();
                for (Map<String, Object> map : dataList) {
                    for (String key : map.keySet()) {
                        stringBuilder.append(StringUtils.trimToEmpty(map.get(key) == null ? "" : (map.get(key).toString())));
                    }
                }

                if (html.indexOf("reportTipContent") != -1) {
                    Map<String, Object> maps = new HashMap<>();
                    maps.put("reportTipContent", stringBuilder.toString());

                    html = ParamsUtil.replaceAllParams(html, maps, "");

                } else {
                    html += stringBuilder.toString();
                }

                reportExplain.setHtml(html);

            }
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
    public static List<Map<String, Object>> getExportReportTableData(Report report, Map<String, String> params) {
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
                                                          Map<String, String> params,
                                                          ReportQueryParameter reportQueryParameter) {
        List<TextValuePair> optionList = new ArrayList<>();

        //值集合、主要用于去重
        HashSet<String> set = new HashSet<>();
        if ("sql".equals(reportQueryParameter.getDataSource())) {//来源是SQL
            try {
                String sqlText = SQLUtil.replaceSQLParams2(reportQueryParameter.getContent(), params);
                optionList = reportService.querySelectOptionList(report.getDsId(), sqlText);
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
    public static List<HtmlFormElement> getHtmlFormElement(Report report, Map<String, String> params) {

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
    public static List<TextValuePair> reloadSelectParamOption(Report report, Map<String, String> params, String triggerParamName) {
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
