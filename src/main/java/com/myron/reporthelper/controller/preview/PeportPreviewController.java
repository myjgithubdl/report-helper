package com.myron.reporthelper.controller.preview;

import com.easydata.export.ExportCSVUtil;
import com.easydata.export.ExportExcelUtil;
import com.easydata.export.excel.ExportExcelParams;
import com.myron.reporthelper.annotation.OpLog;
import com.myron.reporthelper.bo.ReportExplain;
import com.myron.reporthelper.bo.ReportOptions;
import com.myron.reporthelper.bo.ReportParameter;
import com.myron.reporthelper.bo.ReportQueryParameter;
import com.myron.reporthelper.bo.form.HtmlFormElement;
import com.myron.reporthelper.bo.pair.TextValuePair;
import com.myron.reporthelper.entity.Report;
import com.myron.reporthelper.resp.ResponseResult;
import com.myron.reporthelper.service.ReportService;
import com.myron.reporthelper.util.LayuiHtmlTableUtil;
import com.myron.reporthelper.util.ReportUtil;
import com.myron.reporthelper.util.ServletRequestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Myron Miao
 * @date 2019-01-10
 */

@Controller
@RequestMapping(value = "/report/preview")
public class PeportPreviewController {

    @Autowired
    private ReportService reportService;


    @OpLog(name = "预览报表")
    @GetMapping(value = {"/uid/{uid}"})
    public ModelAndView preview(final HttpServletRequest request, @PathVariable final String uid) {
        final ModelAndView modelAndView = new ModelAndView("report/display/");
        Report report = reportService.getReportByUid(uid);
        ReportOptions options = ReportUtil.getReportOptions(report);
        Map<String, String> requestParams = ServletRequestUtil.getParameterMapBySQLAndQueryparam(request, report, report.getSqlText());
        if (options.getShowContent() == 1) {
            modelAndView.setViewName("report/display/table");
        } else if (options.getShowContent() == 2) {
            modelAndView.setViewName("report/display/chart/lineChart");
        } else if (options.getShowContent() == 3) {
            modelAndView.setViewName("report/display/chart/barChart");
        } else if (options.getShowContent() == 4) {
            modelAndView.setViewName("report/display/chart/pieChart");
        } else if (options.getShowContent() == 5) {
            modelAndView.setViewName("report/display/chart/funnelChart");
        } else if (options.getShowContent() == 6) {
            modelAndView.setViewName("report/display/chart/scatterChart");
        }


        modelAndView.addObject("report", report);
        modelAndView.addObject("requestParams", requestParams);
        return modelAndView;
    }

    @OpLog(name = "查询报表表格数据")
    @PostMapping(value = {"/queryReportTableData/uid/{uid}"})
    @ResponseBody
    public ResponseResult queryReportTableData(final HttpServletRequest request,
                                               @PathVariable final String uid) {
        Report report = reportService.getReportByUid(uid);
        Map<String, String> requestParams = ServletRequestUtil.getParameterMapBySQLAndQueryparam(request, report, report.getSqlText());
        ReportParameter reportParameter = ReportUtil.queryReportPageData(report, requestParams);
        String htmlTable = LayuiHtmlTableUtil.getHtmlTable(reportParameter.getMetaColumns(), reportParameter.getReportPageInfo().getRows());

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
        Map<String, String> requestParams = ServletRequestUtil.getParameterMapBySQLAndQueryparam(request, report, report.getSqlText());
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
        List<ReportQueryParameter> queryParameters = report.parseQueryParams();
        Map<String, String> requestParams = ServletRequestUtil.getParameterMapBySQLAndQueryparam(request, report, report.getSqlText());
        List<HtmlFormElement> htmlFormElementList = ReportUtil.getHtmlFormElement(report, requestParams);
        return ResponseResult.success(htmlFormElementList);
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
                                                  @PathVariable final String uid, final String triggerParamName) {
        Report report = reportService.getReportByUid(uid);
        List<ReportQueryParameter> reportQueryParameters = report.parseQueryParams();
        ReportQueryParameter reportQueryParameter = null;
        for (ReportQueryParameter p : reportQueryParameters) {
            if (triggerParamName.equals(p.getName())
                    && ("select".equals(p.getFormElement())
                    || "selectMul".equals(p.getFormElement()))) {
                reportQueryParameter = p;
            }
        }
        String sqlText = report.getSqlText();
        if (reportQueryParameter != null) {
            sqlText = reportQueryParameter.getContent();
        }

        Map<String, String> requestParams = ServletRequestUtil.getParameterMapBySQLAndQueryparam(request, report, sqlText);
        List<TextValuePair> textValuePairs = ReportUtil.reloadSelectParamOption(report, requestParams, triggerParamName);
        return ResponseResult.success(textValuePairs);
    }


    @OpLog(name = "导出报表")
    @PostMapping(value = {"/exportReportTableData/uid/{uid}"})
    public void exportReportTableData(final HttpServletRequest request, final HttpServletResponse response,
                                      @PathVariable final String uid, String exportFileType, String charsetName) {
        Report report = reportService.getReportByUid(uid);
        Map<String, String> requestParams = ServletRequestUtil.getParameterMapBySQLAndQueryparam(request, report, report.getSqlText());
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


}
