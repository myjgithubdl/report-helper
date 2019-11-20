package com.reporthelper.util;

import com.google.common.collect.Maps;
import com.reporthelper.bo.ReportQueryParameter;
import com.reporthelper.entity.Report;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServletRequestUtil {


    /**
     * 将参数转为 Map<String, String>
     *
     * @param request
     * @return
     */
    public static Map<String, String> getRequestParameter(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, String> returnMap = new HashMap<>();
        for (String key : parameterMap.keySet()) {
            List<String> valueList = new ArrayList<>();
            String[] valueArray = parameterMap.get(key);
            for (String value : valueArray) {
                try {
                    //valueList.add(new String(value.getBytes("ISO-8859-1"), "utf-8"));
                    valueList.add(value);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            //数组key去除[]
            returnMap.put(key.replace("[]", ""), String.join(",", valueList));
        }
        return returnMap;
    }

    /**
     * 将参数转为 Map<String, String>
     *
     * @param request
     * @return
     */
    public static Map<String, String> getStringValParameterMap(HttpServletRequest request, Report report, String sqlText) {
        List<ReportQueryParameter> queryParameters = report.parseQueryParams();

        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, String> returnMap = new HashMap<>();

        if (StringUtils.isBlank(sqlText)) {
            returnMap.putAll(getRequestParameter(request));

            return returnMap;
        }

        for (String key : parameterMap.keySet()) {
            List<String> valueList = new ArrayList<>();
            String[] valueArray = parameterMap.get(key);
            for (String value : valueArray) {
                try {
                    //valueList.add(new String(value.getBytes("ISO-8859-1"), "utf-8"));
                    valueList.add(value);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            //数组key去除[]
            String reKey = key.replace("[]", "");
            String value = String.join(",", valueList);

            for (ReportQueryParameter parameter : queryParameters) {
                if (reKey.equals(parameter.getName())) {
                    if ("string".equals(parameter.getDataType())) {
                        if (sqlText.contains("'${" + reKey + "}'")) {//sql中字符串参数已经含有''
                            value = String.join("','", valueList);
                        } else if (sqlText.contains("${" + reKey + "}")) {//sql中字符串参数没有''
                            value = "'" + String.join("','", valueList) + "'";
                        }
                    } else if ("float".equals(parameter.getDataType())) {

                    } else if ("integer".equals(parameter.getDataType())) {

                    }

                }
            }

            returnMap.put(reKey, value);
        }
        return returnMap;
    }


    /**
     * 将参数转为 Map<String, String>
     *
     * @param request
     * @return
     */
    public static Map<String, Object> getObjectValParameterMap(HttpServletRequest request, Report report, String sql) {
        List<ReportQueryParameter> reportQueryParameters = report.parseQueryParams();

        //请求参数
        Map<String, String[]> parameterMap = request.getParameterMap();
        //返回数据
        Map<String, Object> returnMap = new HashMap<>();
        returnMap.putAll(getRequestParameter(request));
        if (CollectionUtils.isNotEmpty(reportQueryParameters)) {
            for (ReportQueryParameter queryParameter : reportQueryParameters) {
                String queryParameterName = queryParameter.getName();
                String dataType = queryParameter.getDataType();
                String formElement = queryParameter.getFormElement();
                Object val = returnMap.get(queryParameterName);
                if (val == null) {
                    continue;
                }
                //多选情况
                if ("selectMul".equals(formElement)) {
                    if ("string".equals(dataType)) {
                        returnMap.put(queryParameterName, ValueUtil.getStringList(val, ","));
                    } else if ("double".equals(dataType)) {
                        returnMap.put(queryParameterName, ValueUtil.getDoubleList(val, ","));
                    } else if ("integer".equals(dataType)) {
                        returnMap.put(queryParameterName, ValueUtil.getIntegerList(val, ","));
                    } else {
                        returnMap.put(queryParameterName, ValueUtil.getStringList(val, ","));
                    }
                } else {
                    if ("string".equals(dataType)) {
                        returnMap.put(queryParameterName, ValueUtil.getString(val));
                    } else if ("double".equals(dataType)) {
                        returnMap.put(queryParameterName, ValueUtil.getDouble(val));
                    } else if ("integer".equals(dataType)) {
                        returnMap.put(queryParameterName, ValueUtil.getInteger(val));
                    } else {
                        returnMap.put(queryParameterName, ValueUtil.getString(val));
                    }
                }
            }
        }
        return returnMap;
    }


    public static Map<String, Object> getObjectValParameterMap(Report report,
                                                               Map<String, Object> params) {
        if (report == null || params == null || CollectionUtils.isEmpty(params.keySet())) {
            return params;
        }

        Map<String, Object> returnMap = Maps.newLinkedHashMap();
        returnMap.putAll(params);
        String sqlText = report.getSqlText();
        //替换所有空格  容易判SQL in查询
        String noneBlankSqlText = RegExUtils.replaceAll(sqlText, " ", "");
        List<ReportQueryParameter> reportQueryParameters = report.parseQueryParams();

        for (ReportQueryParameter queryParameter : reportQueryParameters) {
            String queryParameterName = queryParameter.getName();
            String dataType = queryParameter.getDataType();
            String formElement = queryParameter.getFormElement();
            Object val = returnMap.get(queryParameterName);
            if (val == null) {
                continue;
            }
            //多选情况
            if ("selectMul".equals(formElement)) {
                if ("string".equals(dataType)) {
                    returnMap.put(queryParameterName, ValueUtil.getStringList(val, ","));
                } else if ("double".equals(dataType)) {
                    returnMap.put(queryParameterName, ValueUtil.getDoubleList(val, ","));
                } else if ("integer".equals(dataType)) {
                    returnMap.put(queryParameterName, ValueUtil.getIntegerList(val, ","));
                } else {
                    returnMap.put(queryParameterName, ValueUtil.getStringList(val, ","));
                }
            } else {
                if ("string".equals(dataType)) {
                    returnMap.put(queryParameterName, ValueUtil.getString(val));
                } else if ("double".equals(dataType)) {
                    returnMap.put(queryParameterName, ValueUtil.getDouble(val));
                } else if ("integer".equals(dataType)) {
                    returnMap.put(queryParameterName, ValueUtil.getInteger(val));
                } else {
                    returnMap.put(queryParameterName, ValueUtil.getString(val));
                }
            }
        }
        return returnMap;
    }

    public static Map<String, Object> getObjectValParameterMap(HttpServletRequest request,
                                                               Report report,
                                                               Map<String, Object> params) {
        Map<String, Object> returnMap = Maps.newLinkedHashMap();

        return returnMap;
    }
}
