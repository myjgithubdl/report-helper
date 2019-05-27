package com.reporthelper.db.query;


import com.easydata.head.TheadColumn;
import com.reporthelper.bo.ReportParameter;
import com.reporthelper.bo.pair.TextValuePair;

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


    ReportParameter getReportParameter();


    /**
     * 从sql语句中解析出报表查询参数(如下拉列表参数）的列表项集合
     *
     * @param sqlText sql语句
     * @return List[TextValuePair]
     */
    List<TextValuePair> querySelectOptionList(String sqlText);


    /**
     * 给SQL增加 count(1) as count_num
     *
     * @param sqlText
     * @return
     */
    String getCountSql(String sqlText);

    /**
     * 给SQL增加分页
     *
     * @param sqlText
     * @return
     */
    String getPageSql(String sqlText);


    /**
     * 查询SQL的记录数
     *
     * @param sqlText
     * @return
     */
    int queryCount(String sqlText);


    /**
     * 查询数量的同事并且设置分页信息
     *
     * @param sqlText
     * @return
     */
    int queryCountAndSetPageInfo(String sqlText);

    /**
     * 查询SQL的记录数
     *
     * @param sqlText
     * @return
     */
    List<Map<String, Object>> queryDataList(String sqlText);


    /**
     * 查询SQL的记录数，同时会检查分页信息，如果启用分页并且总记录数为null会同时出发分页操作
     *
     * @param sqlText
     * @return
     */
    List<Map<String, Object>> queryPageDataList(String sqlText);


    /**
     * 使用Spring jdbcTemplate 查询能否防止SQL注入
     * 能否防止SQL注入、JdbcTemplate不解决in查询问题
     *
     * @param sqlText
     * @return
     */
    int queryCount(String sqlText, Object[] args, int[] argTypes);

    /**
     * 使用Spring jdbcTemplate 查询能否防止SQL注入
     * 能否防止SQL注入、JdbcTemplate不解决in查询问题
     *
     * @param sqlText
     * @return
     */
    int queryCountAndSetPageInfo(String sqlText, Object[] args, int[] argTypes);

    /**
     * 使用Spring jdbcTemplate 查询能否防止SQL注入
     * 能否防止SQL注入、JdbcTemplate不解决in查询问题
     *
     * @param sqlText
     * @param args
     * @param argTypes
     * @return
     */
    List<Map<String, Object>> queryDataList(String sqlText, Object[] args, int[] argTypes);

    /**
     * 查询SQL的记录数，同时会检查分页信息，如果启用分页并且总记录数为null会同时出发分页操作
     * 能否防止SQL注入、JdbcTemplate不解决in查询问题
     *
     * @param sqlText
     * @param args
     * @param argTypes
     * @return
     */
    List<Map<String, Object>> queryPageDataList(String sqlText, Object[] args, int[] argTypes);


    /**
     * 使用Spring NamedParameterJdbcTemplate 查询
     * 能防止SQL注入、解决in查询问题
     *
     * @param sqlText
     * @return
     */
    int queryCount(String sqlText, Map<String, ?> paramMap);

    /**
     * 使用Spring NamedParameterJdbcTemplate 查询
     * 能防止SQL注入、解决in查询问题
     *
     * @param sqlText
     * @return
     */
    int queryCountAndSetPageInfo(String sqlText, Map<String, ?> paramMap);

    /**
     * 使用Spring NamedParameterJdbcTemplate 查询
     * 能防止SQL注入、解决in查询问题
     *
     * @param sqlText
     * @param paramMap
     * @return
     */
    List<Map<String, Object>> queryDataList(String sqlText, Map<String, ?> paramMap);

    /**
     * 查询SQL的记录数，同时会检查分页信息，如果启用分页并且总记录数为null会同时出发分页操作
     * * 使用Spring NamedParameterJdbcTemplate 查询
     * 能防止SQL注入、解决in查询问题
     *
     * @param sqlText
     * @param paramMap
     * @return
     */
    List<Map<String, Object>> queryPageDataList(String sqlText, Map<String, ?> paramMap);


    /**
     * 使用Spring NamedParameterJdbcTemplate 查询
     * 根据SQL和参数解析出选项
     * @param sqlText
     * @param paramMap
     * @return
     */
    List<TextValuePair> querySelectOptionList(String sqlText, Map<String, ?> paramMap);


}
