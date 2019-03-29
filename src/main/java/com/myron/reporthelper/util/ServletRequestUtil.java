package com.myron.reporthelper.util;

import com.myron.reporthelper.bo.ReportQueryParameter;
import com.myron.reporthelper.entity.Report;

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
        List<ReportQueryParameter> queryParameters = report.parseQueryParams();

        sql = sql.replaceAll(" ", "");

        //请求参数
        Map<String, String[]> parameterMap = request.getParameterMap();
        //返回数据
        Map<String, Object> returnMap = new HashMap<>();

        //处理报表参数
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

            boolean isReportParam = false;//是否是报表参数

            //报表参数
            for (ReportQueryParameter parameter : queryParameters) {
                if (reKey.equals(parameter.getName())) {
                    //简单的校验是不是in表达式
                    List<String> sqlInParamEl = new ArrayList<>();
                    sqlInParamEl.add("in(${" + parameter.getName() + "})");
                    sqlInParamEl.add("in(#{" + parameter.getName() + "})");
                    sqlInParamEl.add("in('${" + parameter.getName() + "})'");
                    sqlInParamEl.add("in('#{" + parameter.getName() + "})'");

                    if (sql.indexOf(sqlInParamEl.get(0)) > 0
                            || sql.indexOf(sqlInParamEl.get(1)) > 0
                            || sql.indexOf(sqlInParamEl.get(2)) > 0
                            || sql.indexOf(sqlInParamEl.get(3)) > 0) {//将其判断为in表达式 值则为数组
                        if ("string".equals(parameter.getDataType())) {
                            returnMap.put(reKey, valueList);
                        } else if ("float".equals(parameter.getDataType())) {
                            List<Double> valueDoubleList = new ArrayList<>();
                            valueList.stream().forEach(s -> valueDoubleList.add(Double.parseDouble(s)));
                            returnMap.put(reKey, valueDoubleList);
                        } else if ("integer".equals(parameter.getDataType())) {
                            List<Integer> valueIntegerList = new ArrayList<>();
                            valueList.stream().forEach(s -> valueIntegerList.add(Integer.parseInt(s)));
                            returnMap.put(reKey, valueIntegerList);
                        } else {
                            returnMap.put(reKey, valueList);
                        }
                    } else {
                        if ("string".equals(parameter.getDataType())) {
                            returnMap.put(reKey, String.join(",", valueList).toString());
                        } else if ("float".equals(parameter.getDataType())) {
                            returnMap.put(reKey, Double.parseDouble(valueList.get(0)));
                        } else if ("integer".equals(parameter.getDataType())) {
                            returnMap.put(reKey, Integer.parseInt(valueList.get(0)));
                        } else {
                            returnMap.put(reKey, String.join(",", valueList));
                        }
                    }

                    isReportParam = true;
                }

            }

            if (!isReportParam) {//不是报表参数
                returnMap.put(reKey, String.join(",", valueList));
            }

        }


        return returnMap;
    }

}
