package com.myron.reporthelper.db.query;


import com.easydata.head.TheadColumn;
import com.myron.reporthelper.bo.ReportParameter;
import com.myron.reporthelper.bo.pair.TextValuePair;

import java.util.List;
import java.util.Map;

/**
 * 报表查询器接口
 *
 * @author 缪应江
 */
public interface Queryer {
    /**
     * 从sql语句中解析出报表元数据列集合
     *
     * @param sqlText sql语句
     * @return List[ReportMetaDataColumn]
     */
    List<TheadColumn> parseMetaDataColumns(String sqlText);

    /**
     * 从sql语句中解析出报表查询参数(如下拉列表参数）的列表项集合
     *
     * @param sqlText sql语句
     * @return List[TextValuePair]
     */
    List<TextValuePair> querySelectOptionList(String sqlText);


    /**
     * 查询SQL的记录数
     *
     * @param sqlText
     * @return
     */
    int queryCount(String sqlText);


    /**
     *
     * 查询SQL的记录数，同时会检查分页信息，如果启用分页并且总记录数为null会同时出发分页操作
     * @param sqlText
     * @return
     */
    List<Map<String, Object>> queryForList(String sqlText);


    ReportParameter getReportParameter();

}
