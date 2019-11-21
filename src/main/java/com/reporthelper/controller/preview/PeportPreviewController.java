package com.reporthelper.controller.preview;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.easydata.export.ExportCSVUtil;
import com.easydata.export.ExportExcelUtil;
import com.easydata.export.excel.ExportExcelParams;
import com.easydata.head.TheadColumn;
import com.easydata.pivottable.PivotTableDataUtil;
import com.easydata.pivottable.core.PivotTableDataCore;
import com.easydata.pivottable.domain.PivotTableCalCol;
import com.easydata.pivottable.enmus.AggFunc;
import com.google.common.collect.Maps;
import com.reporthelper.annotation.CurrentUser;
import com.reporthelper.annotation.OpLog;
import com.reporthelper.bo.*;
import com.reporthelper.bo.form.HtmlFormElement;
import com.reporthelper.bo.pair.TextValuePair;
import com.reporthelper.entity.Report;
import com.reporthelper.entity.ReportCompose;
import com.reporthelper.entity.User;
import com.reporthelper.resp.ResponseResult;
import com.reporthelper.service.ReportComposeService;
import com.reporthelper.service.ReportService;
import com.reporthelper.util.LayuiHtmlTableUtil;
import com.reporthelper.util.ReportUtil;
import com.reporthelper.util.ServletRequestUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Myron Miao
 * @date 2019-01-10
 */

@Slf4j
@Controller
@RequestMapping(value = "/report/preview")
public class PeportPreviewController {

    @Autowired
    private ReportService reportService;

    @Autowired
    private ReportComposeService reportComposeService;

    @OpLog(name = "预览报表")
    @GetMapping(value = {"/uid/{uid}"})
    public ModelAndView preview(final HttpServletRequest request, @PathVariable final String uid) {
        final ModelAndView modelAndView = new ModelAndView("report/display/");
        Report report = reportService.getReportByUid(uid);
        List<ReportCompose> reportComposeList = reportComposeService.getReportComposeList(report.getId());

        //剔除禁用报表
        if (CollUtil.isNotEmpty(reportComposeList)) {
            reportComposeList = reportComposeList.stream().filter(r -> {
                return (r.getStatus() == null ? 1 : r.getStatus()) == 1;
            }).collect(Collectors.toList());
        }
        report.setReportComposeList(reportComposeList);

        Map<String, Object> requestParams = ServletRequestUtil.getObjectValParameterMap(request, report, report.getSqlText());
        //是否是通过表格查看数据
        modelAndView.setViewName("report/display/display");
        modelAndView.addObject("report", report);
        modelAndView.addObject("requestParams", requestParams);
        modelAndView.addObject("reportStr", JSONObject.toJSONString(report));
        return modelAndView;
    }

    /**
     * 查询报表数据
     * @param loginUser
     * @param report
     * @param reportComposeList
     * @param paramBody
     * @param request
     * @param isExportData
     * @return
     * @throws Exception
     */
    private Map<String, ReportQueryData> getTableData(User loginUser, Report report,
                                                      List<ReportCompose> reportComposeList,
                                                      String paramBody,
                                                      HttpServletRequest request,
                                                      boolean isExportData) throws Exception {
        if (StringUtils.isBlank(paramBody)) {
            String errorMsg = "参数" + paramBody + "为空";
            log.info(errorMsg);
            throw new Exception(errorMsg);
        }
        //表单传入参数
        JSONObject paramJSONObject = JSONObject.parseObject(paramBody);
        if (report == null) {
            String errorMsg = "报表不存在";
            log.info(errorMsg);
            throw new Exception(errorMsg);
        }
        //是否指定金查询某一个特定的SQL
        String queryComposeUid = MapUtils.getString(paramJSONObject, "queryReportComposeUid");

        if (CollUtil.isNotEmpty(reportComposeList) && StrUtil.isNotBlank(queryComposeUid)) {
            reportComposeList = reportComposeList.stream().filter(r -> queryComposeUid.equals(r.getUid())).collect(Collectors.toList());
        }

        //剔除禁用报表
        if (CollUtil.isNotEmpty(reportComposeList)) {
            reportComposeList = reportComposeList.stream().filter(r -> {
                return (r.getStatus() == null ? 1 : r.getStatus()) == 1;
            }).collect(Collectors.toList());
        }

        if (CollUtil.isEmpty(reportComposeList)) {
            String errorMsg = "报表为设置SQL查询语句不存在";
            log.info(errorMsg);
            throw new Exception(errorMsg);
        }

        report.setReportComposeList(reportComposeList);
        //是否共用查询参数
        Integer paramShare = report.getParamShare();
        Map<String, ReportQueryData> respLayuiTableMap = Maps.newLinkedHashMap();

        //公共表单参数和对应的值
        Map<String, Object> commonParam = paramJSONObject.getJSONObject("common");
        Map<String, Object> commonFormValMap = ServletRequestUtil.getObjectValParameterMap(report, commonParam);
        for (ReportCompose reportCompose : reportComposeList) {
            //具体报表参数和对应的值
            JSONObject composeFormValJson = paramJSONObject.getJSONObject(reportCompose.getUid());
            //对参数进行处理、主要处理设置的参数为选择框且是in查询的情况，如SQL写法为 in ('${province}') 前端传入的值为 北京,广州 则需要转化为 北京','广州   最后SQL则为 in ('北京','广州')
            Map<String, Object> composeFormValMap = ServletRequestUtil.getObjectValParameterMap(reportCompose, composeFormValJson);
            //使用报表组成解析公共参数
            Map<String, Object> commonComposeFormValMap = ServletRequestUtil.getObjectValParameterMap(reportCompose, commonParam);

            Map<String, Object> totalParamMap = Maps.newLinkedHashMap();
            //参数共享
            if (paramShare == 1) {
                totalParamMap.putAll(commonComposeFormValMap);
            } else {
                totalParamMap.putAll(composeFormValMap);
            }

            ReportQueryData tableData = this.getTableData(reportCompose, paramJSONObject, totalParamMap, isExportData);

            respLayuiTableMap.put(reportCompose.getUid(), tableData);
        }

        return respLayuiTableMap;
    }

    /**
     * 查询报表数据
     *
     * @param reportCompose 需要查询的报表
     * @param params        查询参数
     * @param isExportData  是否查询导出、因为导出不分页
     * @return
     */
    private ReportQueryData getTableData(ReportCompose reportCompose, JSONObject allParamJson, Map<String, Object> params, boolean isExportData) {
        ReportQueryData reportQueryData = new ReportQueryData();
        try {
            ReportOptions options = reportCompose.parseOptions();
            List<TheadColumn> theadColumns = reportCompose.parseMetaColumns();


            Integer showContent = options.getShowContent();
            //表格导出数据则禁用分页、查询所有数据
            List<Map<String, Object>> dataList = null;
            int count = 0, pageSize = 0;
            String tableHtml = null;
            //导出，查询全部数据
            if (isExportData) {
                if (showContent == 4) {
                    JSONObject reportComposeParamJson = allParamJson.getJSONObject(reportCompose.getUid());
                    params.putAll(reportComposeParamJson.getJSONObject("pivotTableParam"));
                    ReportParameter reportParameter = ReportUtil.queryReportPageData(reportCompose, params);
                    PivotTableDataCore pivotTableData = this.getPivotTableData(reportParameter, params);
                    theadColumns = pivotTableData.getPivotTableTheadColumnList();
                    dataList = pivotTableData.getPivotTableDataList();
                    reportQueryData.setTheadColumn(theadColumns);
                } else {
                    dataList = ReportUtil.getExportReportTableData(reportCompose, params);
                }
                count = CollectionUtils.size(dataList);
            } else {
                ReportParameter reportParameter = ReportUtil.queryReportPageData(reportCompose, params);
                dataList = reportParameter.getReportPageInfo().getRows();
                count = reportParameter.getReportPageInfo().getTotalRows();
                pageSize = reportParameter.getReportPageInfo().getPageSize();

                if (showContent == 1) {
                    //数据表格
                    tableHtml = LayuiHtmlTableUtil.getHtmlTable(reportParameter.getMetaColumns(), dataList, "static-table-" + reportCompose.getUid());
                } else if (showContent == 4) {
                    //透视表
                    JSONObject reportComposeParamJson = allParamJson.getJSONObject(reportCompose.getUid());
                    params.putAll(reportComposeParamJson.getJSONObject("pivotTableParam"));
                    PivotTableDataCore pivotTableData = this.getPivotTableData(reportParameter, params);
                    theadColumns = pivotTableData.getPivotTableTheadColumnList();
                    dataList = pivotTableData.getPivotTableDataList();

                    tableHtml = LayuiHtmlTableUtil.getHtmlTable(theadColumns, dataList, "static-table-" + reportCompose.getUid());
                    count = CollectionUtils.size(dataList);
                    pageSize = count;
                }
            }

            ReportExplain reportExplain = ReportUtil.getReportExplain(reportCompose, params);
            String explainHtml = reportExplain == null ? null : reportExplain.getExplainHtml();

            reportQueryData.setCount(count);
            reportQueryData.setData(dataList);
            reportQueryData.setTableHtml(tableHtml);
            reportQueryData.setExplain(explainHtml);
            reportQueryData.setPageSize(pageSize);
            if (!isExportData && (showContent == 1 || showContent == 4)) {
                reportQueryData.setData(null);
            }
            if (isExportData) {
                reportQueryData.setTheadColumn(theadColumns);
            }
            return reportQueryData;
        } catch (Exception e) {
            e.printStackTrace();

        }
        return reportQueryData;
    }

    /**
     * 查询报表数据
     * @param loginUser
     * @param uid
     * @param paramBody
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = {"/getReportData/{uid}"})
    public ResponseResult getReportData(@CurrentUser final User loginUser, @PathVariable String uid,
                                        @RequestParam String paramBody,
                                        HttpServletRequest request) {
        ResponseResult result = ResponseResult.error("查询数据错误");
        try {
            Report report = reportService.getReportByUid(uid);
            if (report == null) {
                result.setRespDesc("报表" + uid + "不存在");
                log.info(result.getRespDesc());
                return result;
            }
            List<ReportCompose> reportComposeList = reportComposeService.getReportComposeList(report.getId());

            //查询报表数据
            Map<String, ReportQueryData> reportQueryDataMap = this.getTableData(loginUser, report, reportComposeList, paramBody, request, false);

            Map<String, Object> respMap = Maps.newLinkedHashMap();
            for (String reportComposeUid : reportQueryDataMap.keySet()) {
                ReportQueryData layuiTableData = reportQueryDataMap.get(reportComposeUid);
                respMap.put(reportComposeUid, layuiTableData);
            }
            return ResponseResult.success(respMap);
        } catch (Exception e) {
            e.printStackTrace();
            result.setRespDesc(e.getMessage());
        }
        return result;
    }


    /**
     * 导出Excel数据
     * @param loginUser
     * @param uid
     * @param paramBody
     * @param request
     * @param response
     */
    @PostMapping(value = "/exportToFile/{uid}")
    @OpLog(name = "导出报表数据")
    public void exportToFile(@CurrentUser User loginUser,
                             @PathVariable String uid,
                             @RequestParam String paramBody,
                             HttpServletRequest request, HttpServletResponse response) {
        try {
            Report report = reportService.getReportByUid(uid);
            if (report == null) {
                String errorMsg = "报表" + uid + "不存在";
                log.info(errorMsg);
                return;
            }
            List<ReportCompose> reportComposeList = reportComposeService.getReportComposeList(report.getId());

            JSONObject paramJSONObject = JSONObject.parseObject(paramBody);
            String fileType = MapUtils.getString(paramJSONObject, "fileType", "excel");
            String fileCharset = MapUtils.getString(paramJSONObject, "fileCharset", "GB2312");
            //是否指定金查询某一个特定的SQL
            String queryComposeUid = MapUtils.getString(paramJSONObject, "queryReportComposeUid");
            List<ReportCompose> reportComposeList2 = reportComposeList;
            if (CollUtil.isNotEmpty(reportComposeList) && StrUtil.isNotBlank(queryComposeUid)) {
                reportComposeList2 = reportComposeList.stream().filter(r -> queryComposeUid.equals(r.getUid())).collect(Collectors.toList());
            }

            Map<String, ReportQueryData> reportQueryDataMap = this.getTableData(loginUser, report, reportComposeList, paramBody, request, true);

            List<ExportExcelParams> exportExcelParamsList = new ArrayList<>();
            for (ReportCompose reportCompose : reportComposeList2) {
                String reportComposeName = reportCompose.getName();
                String reportComposeUid = reportCompose.getUid();
                if (!reportQueryDataMap.containsKey(reportComposeUid)) {
                    continue;
                }
                ReportQueryData reportQueryData = reportQueryDataMap.get(reportComposeUid);
                ExportExcelParams exportExcelParams = new ExportExcelParams();
                exportExcelParams.setDataList(reportQueryData.getData());
                exportExcelParams.setTheadColumnList(reportQueryData.getTheadColumn());
                exportExcelParams.setSheetName(StringUtils.isNoneBlank(reportComposeName) ? reportComposeName : reportComposeUid);

                exportExcelParamsList.add(exportExcelParams);
            }
            ExportExcelUtil.exportExcel(response, report.getName(), exportExcelParamsList);

            log.info("用户" + SecurityUtils.getSubject().getPrincipal() + "导出报表数据条数，报表uid：" + uid);
        } catch (Exception ex) {
            log.error("导出Excel失败", ex);
        }
    }

    @OpLog(name = "查询报表表格数据")
    @PostMapping(value = {"/queryReportTableData/uid/{uid}"})
    @ResponseBody
    public ResponseResult queryReportTableData(final HttpServletRequest request,
                                               @PathVariable final String uid) {
        Report report = reportService.getReportByUid(uid);
        Map<String, Object> requestParams = ServletRequestUtil.getObjectValParameterMap(request, report, report.getSqlText());
        ReportParameter reportParameter = ReportUtil.queryReportPageData(report, requestParams);
        String htmlTable = LayuiHtmlTableUtil.getHtmlTable(reportParameter.getMetaColumns(), reportParameter.getReportPageInfo().getRows(), null);

        ReportExplain reportExplain = ReportUtil.getReportExplain(report, requestParams);

        Map<String, Object> data = new HashMap<>();
        data.put("htmlTable", htmlTable);
        data.put("reportExplain", reportExplain);
        reportParameter.getReportPageInfo().setRows(null);
        data.put("reportPageInfo", reportParameter.getReportPageInfo());

        return ResponseResult.success(data);
    }

    @OpLog(name = "查询报表图表")
    @PostMapping(value = {"/queryReportChartData/uid/{uid}"})
    @ResponseBody
    public ResponseResult queryReportChartData(final HttpServletRequest request,
                                               @PathVariable final String uid) {
        Report report = reportService.getReportByUid(uid);
        //Map<String, String> requestParams = ServletRequestUtil.getStringValParameterMap(request, report, report.getSqlText());
        Map<String, Object> requestParams = ServletRequestUtil.getObjectValParameterMap(request, report, report.getSqlText());
        ReportParameter reportParameter = ReportUtil.queryReportPageData(report, requestParams);

        ReportExplain reportExplain = ReportUtil.getReportExplain(report, requestParams);

        Map<String, Object> data = new HashMap<>();
        data.put("reportExplain", reportExplain);
        data.put("reportPageInfo", reportParameter.getReportPageInfo());

        return ResponseResult.success(data);
    }

    /**
     * 查询报表的查询参数
     *
     * @param request
     * @param uid
     * @return
     */
    @OpLog(name = "查询报表查询参数")
    @PostMapping(value = {"/queryReportParameter/uid/{uid}"})
    @ResponseBody
    public ResponseResult queryReportParameter(final HttpServletRequest request,
                                               @PathVariable final String uid) {

        Report report = reportService.getReportByUid(uid);
        Integer paramShare = report.getParamShare();
        List<ReportCompose> reportComposeList = reportComposeService.getReportComposeList(report.getId());
        report.setReportComposeList(reportComposeList);

        Map<String, Object> requestParams = ServletRequestUtil.getObjectValParameterMap(request, report, report.getSqlText());

        Map<String, Object> dataMap = Maps.newLinkedHashMap();
        dataMap.put("paramShare", paramShare);

        List<HtmlFormElement> htmlFormElementList = ReportUtil.getHtmlFormElement(report, requestParams);
        Map<String, Object> commomParams = MapUtil.of("queryElements", htmlFormElementList);
        dataMap.put("params_common", commomParams);

        //不共享参数
        if (paramShare == 0 && reportComposeList != null) {
            for (ReportCompose reportCompose : reportComposeList) {
                List<HtmlFormElement> htmlFormElementList2 = ReportUtil.getHtmlFormElement(reportCompose, requestParams);
                Map<String, Object> composeParams = MapUtil.of("queryElements", htmlFormElementList2);
                dataMap.put("params_" + reportCompose.getUid(), composeParams);
            }
        }
        return ResponseResult.success(dataMap);
    }

    /**
     * 查询报表的查询参数
     *
     * @param request
     * @param uid
     * @return
     */
    @OpLog(name = "查询报表选择框参数")
    @PostMapping(value = {"/reloadSelectParamOption/uid/{uid}"})
    @ResponseBody
    public ResponseResult reloadSelectParamOption(final HttpServletRequest request,
                                                  @PathVariable final String uid,
                                                  final String triggerParamName,
                                                  final String queryReportComposeUid) {
        Report report = reportService.getReportByUid(uid);
        List<ReportQueryParameter> queryParameterList = null;
        Report queryOptionReport = null;
        //共享参数
        if (report.getParamShare() == 1) {
            queryParameterList = report.parseQueryParams();
            queryOptionReport = report;
        } else {
            List<ReportCompose> reportComposeList = reportComposeService.getReportComposeList(report.getId());
            report.setReportComposeList(reportComposeList);
            if (StrUtil.isNotBlank(queryReportComposeUid)) {
                reportComposeList = reportComposeList.stream().filter(r -> queryReportComposeUid.equals(r.getUid())).collect(Collectors.toList());
            }
            if (CollUtil.isNotEmpty(reportComposeList)) {
                queryOptionReport = reportComposeList.get(0);
                queryParameterList = queryOptionReport.parseQueryParams();
            }
        }
        if (CollUtil.isEmpty(queryParameterList)) {
            return ResponseResult.error("查询参数为空");
        }

        List<ReportQueryParameter> queryParameters = queryParameterList.stream().filter(p -> triggerParamName.equals(p.getName())).collect(Collectors.toList());
        if (CollUtil.isEmpty(queryParameters)) {
            return ResponseResult.error("未查询到参数：" + triggerParamName);
        }

        ReportQueryParameter queryParameter = queryParameters.get(0);

        if (!("select".equals(queryParameter.getFormElement())
                || "selectMul".equals(queryParameter.getFormElement()))) {
            return ResponseResult.error("参数不为选择框：" + triggerParamName);
        }


        String sqlText = queryOptionReport.getSqlText();
        Map<String, Object> requestParams = new HashMap<>();
        if ("sql".equals(queryParameter.getDataSource())) {
            sqlText = queryParameter.getContent();
            requestParams = ServletRequestUtil.getObjectValParameterMap(request, queryOptionReport, sqlText);
        }

        List<TextValuePair> textValuePairs = ReportUtil.reloadSelectParamOption(queryOptionReport, requestParams, triggerParamName);
        return ResponseResult.success(textValuePairs);
    }


    @OpLog(name = "导出报表")
    @PostMapping(value = {"/exportReportTableData/uid/{uid}"})
    public void exportReportTableData(final HttpServletRequest request, final HttpServletResponse response,
                                      @PathVariable final String uid, String exportFileType, String charsetName) {
        Report report = reportService.getReportByUid(uid);
        //Map<String, String> requestParams = ServletRequestUtil.getParameterMapBySQLAndQueryparam(request, report, report.getSqlText());
        Map<String, Object> requestParams = ServletRequestUtil.getObjectValParameterMap(request, report, report.getSqlText());

        List<Map<String, Object>> dataList = ReportUtil.getExportReportTableData(report, requestParams);

        if ("excel".equals(exportFileType)) {
            ExportExcelParams exportExcelParams = new ExportExcelParams();
            exportExcelParams.setDataList(dataList);
            exportExcelParams.setTheadColumnList(report.parseMetaColumns());
            exportExcelParams.setSheetName(report.getName());
            ExportExcelUtil.exportExcel(response, report.getName(), exportExcelParams);
        } else {
            ExportCSVUtil.exportCSV(response, report.getName(), charsetName, report.parseMetaColumns(), dataList);
        }

    }


    @OpLog(name = "查询透视表格数据")
    @PostMapping(value = {"/queryPivotTableData/uid/{uid}"})
    @ResponseBody
    public ResponseResult queryPivotTableData(final HttpServletRequest request,
                                              @PathVariable final String uid) {
        Report report = reportService.getReportByUid(uid);
        //Map<String, String> requestParams = ServletRequestUtil.getParameterMapBySQLAndQueryparam(request, report, report.getSqlText());
        Map<String, Object> requestParams = ServletRequestUtil.getObjectValParameterMap(request, report, report.getSqlText());

        ReportParameter reportParameter = ReportUtil.queryReportPageData(report, requestParams);
        PivotTableDataCore pivotTableData = this.getPivotTableData(reportParameter, requestParams);
        List<TheadColumn> pivotTableTheadColumnList = pivotTableData.getPivotTableTheadColumnList();
        List<Map<String, Object>> pivotTableDataList = pivotTableData.getPivotTableDataList();

        String htmlTable = LayuiHtmlTableUtil.getHtmlTable(pivotTableTheadColumnList, pivotTableDataList, null);

        ReportExplain reportExplain = ReportUtil.getReportExplain(report, requestParams);

        Map<String, Object> data = new HashMap<>();
        data.put("htmlTable", htmlTable);
        data.put("reportExplain", reportExplain);
        reportParameter.getReportPageInfo().setRows(null);
        data.put("reportPageInfo", reportParameter.getReportPageInfo());

        return ResponseResult.success(data);
    }


    /**
     * 转化为透视表数据
     * @param reportParameter
     * @param requestParams
     * @return
     */
    private PivotTableDataCore getPivotTableData(ReportParameter reportParameter, Map<String, Object> requestParams) {
        List<String> rows = CollUtil.newArrayList(Arrays.asList(MapUtils.getString(requestParams, "pivotTableRow", "").split(",")));
        if (requestParams.containsKey("pivotTableRow")) {
            if (requestParams.get("pivotTableRow") instanceof JSONArray) {
                JSONArray pivotTableRowArray= (JSONArray) requestParams.get("pivotTableRow");
                rows.clear();
                pivotTableRowArray.forEach(name -> {
                    rows.add(name.toString());
                });
            }
        }

        List<String> cols = Arrays.asList(MapUtils.getString(requestParams, "pivotTableColumn", ""));
        String valColNames = MapUtils.getString(requestParams, "pivotTableValue", "");
        AggFunc func = AggFunc.getAggFunc(MapUtils.getString(requestParams, "pivotTableAggfunc", ""));

        PivotTableCalCol pivotTableCalCol = new PivotTableCalCol(valColNames, func);
        List<PivotTableCalCol> calCols = new ArrayList<>();
        calCols.add(pivotTableCalCol);

        PivotTableDataCore pivotTableData = PivotTableDataUtil.getPivotTableData(rows, cols, calCols, reportParameter.getMetaColumns(), reportParameter.getReportPageInfo().getRows());

        return pivotTableData;
    }


    @OpLog(name = "导出透视表数据")
    @PostMapping(value = {"/exportPivotTableData/uid/{uid}"})
    public void exportPivotTableData(final HttpServletRequest request, final HttpServletResponse response,
                                     @PathVariable final String uid, String exportFileType, String charsetName) {
        Report report = reportService.getReportByUid(uid);
        //Map<String, String> requestParams = ServletRequestUtil.getParameterMapBySQLAndQueryparam(request, report, report.getSqlText());
        Map<String, Object> requestParams = ServletRequestUtil.getObjectValParameterMap(request, report, report.getSqlText());

        ReportParameter reportParameter = ReportUtil.queryReportPageData(report, requestParams);
        PivotTableDataCore pivotTableData = this.getPivotTableData(reportParameter, requestParams);
        List<TheadColumn> pivotTableTheadColumnList = pivotTableData.getPivotTableTheadColumnList();
        List<Map<String, Object>> pivotTableDataList = pivotTableData.getPivotTableDataList();

        if ("excel".equals(exportFileType)) {
            ExportExcelParams exportExcelParams = new ExportExcelParams();
            exportExcelParams.setDataList(pivotTableDataList);
            exportExcelParams.setTheadColumnList(pivotTableTheadColumnList);
            exportExcelParams.setSheetName(report.getName());
            ExportExcelUtil.exportExcel(response, report.getName(), exportExcelParams);
        } else {
            ExportCSVUtil.exportCSV(response, report.getName(), charsetName, pivotTableTheadColumnList, pivotTableDataList);
        }

    }

}
