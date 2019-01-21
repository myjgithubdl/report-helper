package com.myron.reporthelper.util;

import com.myron.reporthelper.bo.ReportQueryParameter;
import com.myron.reporthelper.entity.Report;
import org.apache.velocity.anakia.Escape;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServletRequestUtil {


    /**
     * 将参数转为map
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
     * 将参数转为map
     *
     * @param request
     * @return
     */
    public static Map<String, String> getParameterMapBySQLAndQueryparam(HttpServletRequest request, Report report, String sqlText) {
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

}
