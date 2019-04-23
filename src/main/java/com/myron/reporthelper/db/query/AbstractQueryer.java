package com.myron.reporthelper.db.query;

import com.alibaba.fastjson.JSONObject;
import com.easydata.head.TheadColumn;
import com.myron.reporthelper.bo.ReportDataSource;
import com.myron.reporthelper.bo.ReportPageInfo;
import com.myron.reporthelper.bo.ReportParameter;
import com.myron.reporthelper.bo.pair.TextValuePair;
import com.myron.reporthelper.util.JdbcUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 */

@Data
@Slf4j
public abstract class AbstractQueryer {

    protected final ReportDataSource reportDataSource;
    protected final ReportParameter parameter;

    protected AbstractQueryer(final ReportDataSource reportDataSource, final ReportParameter parameter) {
        this.reportDataSource = reportDataSource;
        this.parameter = parameter;
    }


    /**
     * 解析元数据列是限制记录数为1
     *
     * @param sql
     * @return
     */
    public String getParseMetaDataColumns(String sql) {
        //创建分页信息
        ReportPageInfo pageInfo = ReportPageInfo.builder().isEnablePage(true).pageIndex(1).totalRows(1).pageSize(1).build();
        this.parameter.setReportPageInfo(pageInfo);
        //设置分页信息
        this.parameter.getReportPageInfo().setPageInfo(1);

        sql = this.getPageSql(sql);

        log.info("解析元数据列SQL[{}]", sql);

        return sql;
    }


    /**
     * 解析出SQL元数据列
     *
     * @param sqlText
     * @return
     */
    public List<TheadColumn> parseMetaDataColumns(final String sqlText) {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        List<TheadColumn> columns = null;

        try {
            log.debug("Parse Report MetaDataColumns SQL:{},", sqlText);
            conn = this.getJdbcConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(this.getParseMetaDataColumns(sqlText));
            final ResultSetMetaData rsMataData = rs.getMetaData();
            final int count = rsMataData.getColumnCount();
            columns = new ArrayList<>(count);
            for (int i = 1; i <= count; i++) {
                final TheadColumn column = new TheadColumn();
                column.setId(i + "");
                column.setName(rsMataData.getColumnLabel(i));
                column.setText(rsMataData.getColumnLabel(i));
                column.setDataType(rsMataData.getColumnTypeName(i));
                column.setColumnWidth(rsMataData.getColumnDisplaySize(i));
                columns.add(column);
            }
        } catch (final SQLException ex) {
            throw new RuntimeException(ex);
        } finally {
            JdbcUtil.releaseJdbcResource(conn, stmt, rs);
        }
        return columns;
    }

    /**
     * 查询参数类型为select切内容是sql的option内容
     *
     * @param sqlText
     * @return
     */
    public List<TextValuePair> querySelectOptionList(final String sqlText) {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        final HashSet<String> set = new HashSet<>();
        final List<TextValuePair> rows = new ArrayList<>();

        try {
            log.info("查询选择框选项SQL:" + sqlText);
            conn = this.getJdbcConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sqlText);
            while (rs.next()) {
                String value = rs.getString("value");
                String text = rs.getString("text");
                value = (value == null) ? "" : value.trim();
                text = (text == null) ? "未设置" : text.trim();
                if (!set.contains(value)) {
                    set.add(value);
                }
                rows.add(new TextValuePair(value, text));
            }
        } catch (final SQLException ex) {
            throw new RuntimeException(ex);
        } finally {
            JdbcUtil.releaseJdbcResource(conn, stmt, rs);
        }
        set.clear();
        return rows;
    }


    protected List<TheadColumn> getSqlColumns(final List<TheadColumn> metaDataColumns) {
        return metaDataColumns.stream()
                .filter(x -> x.getName() != null && x.getName().length() > 0)
                .collect(Collectors.toList());
    }


    /**
     * 获取当前报表查询器的JDBC Connection对象
     *
     * @return Connection
     */
    protected Connection getJdbcConnection() {
        try {
            Class.forName(this.reportDataSource.getDriverClass());
            return JdbcUtil.getDataSource(this.reportDataSource).getConnection();
        } catch (final Exception ex) {
            throw new RuntimeException(ex);
        }
    }


    /**
     * 可以使用Spring的JdbcTemplate执行数据库查询操作
     *
     * @return
     */
    protected JdbcTemplate getJdbcTemplate() {
        try {
            Class.forName(this.reportDataSource.getDriverClass());
            DataSource dataSource = JdbcUtil.getDataSource(this.reportDataSource);
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            return jdbcTemplate;
        } catch (Exception e) {
            e.printStackTrace();

        }
        return null;
    }

    /**
     * 可以使用Spring的JdbcTemplate执行数据库查询操作
     *
     * @return
     */
    protected NamedParameterJdbcTemplate getNamedParameterJdbcTemplate() {
        try {
            Class.forName(this.reportDataSource.getDriverClass());
            DataSource dataSource = JdbcUtil.getDataSource(this.reportDataSource);
            NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
            return jdbcTemplate;
        } catch (Exception e) {
            e.printStackTrace();

        }
        return null;
    }


    /**
     * 将给SQL套一层查询总数的SQL
     *
     * @param sqlText
     * @return
     */
    protected abstract String getCountSql(String sqlText);


    /**
     * 根据分页信息获取分页SQL
     *
     * @param sqlText
     * @return
     */
    protected abstract String getPageSql(String sqlText);

    /**
     * 获取记录总数并设置分页信息
     *
     * @param sql
     */
    public int queryCount(String sql) {

        if (StringUtils.stripToNull(sql) == null)
            return 0;

        sql = this.getCountSql(sql);

        this.printQueryLog(sql);

        int totalRows = 0;
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = this.getJdbcConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                totalRows = rs.getInt(1);
            }
        } catch (final SQLException ex) {
            throw new RuntimeException(ex);
        } finally {
            JdbcUtil.releaseJdbcResource(conn, stmt, rs);
        }
        return totalRows;
    }


    /**
     * 查询总数并且设置分页信息
     *
     * @param sql
     * @return
     */
    public int queryCountAndSetPageInfo(String sql) {
        if (StringUtils.stripToNull(sql) == null)
            return 0;

        int totalRows = this.queryCount(sql);
        //设置分页信息
        this.parameter.getReportPageInfo().setPageInfo(totalRows);

        return totalRows;


    }

    /**
     * 将SQL 查询的结果转为List<Map<String, Object>>
     *
     * @param sql
     * @return
     * @throws SQLException
     */
    public List<Map<String, Object>> queryDataList(String sql) {
        if (StringUtils.stripToNull(sql) == null)
            return null;

        this.printQueryLog(sql);

        List<Map<String, Object>> results = new ArrayList();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = this.getJdbcConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            int index = 0;
            //调用Spring的实现方法
            ColumnMapRowMapper columnMapRowMapper = new ColumnMapRowMapper();
            //ColumnMapRowMapper columnMapRowMapper = getColumnMapRowMapper();
            while (rs.next()) {
                results.add(columnMapRowMapper.mapRow(rs, index++));
            }
            return results;
        } catch (final SQLException ex) {
            throw new RuntimeException(ex);
        } finally {
            JdbcUtil.releaseJdbcResource(conn, stmt, rs);
        }
    }

    /**
     * 将SQL 查询的结果转为List<Map<String, Object>>
     *
     * @param sql
     * @return
     * @throws SQLException
     */
    public List<Map<String, Object>> queryPageDataList(String sql) {
        if (StringUtils.stripToNull(sql) == null)
            return null;

        ReportPageInfo pageInfo = this.parameter.getReportPageInfo();
        //总记录数为null ， 查询分页信息
        int totalRows = 0;
        if (pageInfo != null && (pageInfo.getTotalRows() == null || pageInfo.getTotalRows() < 0)) {
            totalRows = this.queryCountAndSetPageInfo(sql);
        }

        if (totalRows < 1) {
            return null;
        }

        sql = this.getPageSql(sql);

        return this.queryDataList(sql);
    }

    /**
     * 在SQL中使用?代替参数可以防止SQL注入
     *
     * @param sql      select * from tb where id=? and name=?
     * @param args
     * @param argTypes ?占位符对应的数值类型,java.sql.Types
     * @return
     * @throws DataAccessException
     */
    public int queryCount(String sql, Object[] args, int[] argTypes) {
        if (StringUtils.stripToNull(sql) == null)
            return 0;

        sql = this.getCountSql(sql);

        this.printJdbcTemplateLog(sql, args, argTypes);

        JdbcTemplate jdbcTemplate = this.getJdbcTemplate();
        int totalRows = jdbcTemplate.queryForObject(sql, args, argTypes, Integer.class);
        return totalRows;
    }

    /**
     * 查询总数并且设置分页信息
     *
     * @param sql
     * @return
     */
    public int queryCountAndSetPageInfo(String sql, Object[] args, int[] argTypes) {

        if (StringUtils.stripToNull(sql) == null)
            return 0;

        int totalRows = this.queryCount(sql, args, argTypes);
        //设置分页信息
        this.parameter.getReportPageInfo().setPageInfo(totalRows);
        return totalRows;

    }

    /**
     * 在SQL中使用?代替参数可以防止SQL注入
     *
     * @param sql      select * from tb where id=? and name=?
     * @param args
     * @param argTypes ?占位符对应的数值类型,java.sql.Types
     * @return
     * @throws DataAccessException
     */
    public List<Map<String, Object>> queryDataList(String sql, Object[] args, int[] argTypes) {

        if (StringUtils.stripToNull(sql) == null)
            return null;

        this.printJdbcTemplateLog(sql, args, argTypes);
        try {
            JdbcTemplate jdbcTemplate = this.getJdbcTemplate();
            List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, args, argTypes);
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 查询SQL的记录数，同时会检查分页信息，如果启用分页并且总记录数为null会同时出发分页操作 , 在SQL中使用?代替参数可以防止SQL注入
     *
     * @param sql      select * from tb where id=? and name=?
     * @param args
     * @param argTypes ?占位符对应的数值类型,java.sql.Types
     * @return
     * @throws DataAccessException
     */
    public List<Map<String, Object>> queryPageDataList(String sql, Object[] args, int[] argTypes) {

        if (StringUtils.stripToNull(sql) == null)
            return null;

        ReportPageInfo pageInfo = this.parameter.getReportPageInfo();
        //总记录数为null ， 查询分页信息
        int totalRows = 0;
        if (pageInfo != null && (pageInfo.getTotalRows() == null || pageInfo.getTotalRows() < 0)) {
            totalRows = this.queryCountAndSetPageInfo(sql, args, argTypes);
        }

        if (totalRows < 1) {
            return null;
        }

        sql = this.getPageSql(sql);

        return this.queryDataList(sql, args, argTypes);
    }


    /**
     * 使用NamedParameterJdbcTemplate查询
     *
     * @param sql
     * @param paramMap
     * @return
     */
    public int queryCount(String sql, Map<String, ?> paramMap) {
        if (StringUtils.stripToNull(sql) == null)
            return 0;

        sql = this.getCountSql(sql);
        this.printJdbcTemplateLog(sql, paramMap);

        NamedParameterJdbcTemplate namedParameterJdbcTemplate = this.getNamedParameterJdbcTemplate();
        int totalRows = namedParameterJdbcTemplate.queryForObject(sql, paramMap, Integer.class);
        return totalRows;
    }

    /**
     * 使用NamedParameterJdbcTemplate查询
     *
     * @param sql
     * @param paramMap
     * @return
     */
    public int queryCountAndSetPageInfo(String sql, Map<String, ?> paramMap) {
        if (StringUtils.stripToNull(sql) == null)
            return 0;

        int totalRows = this.queryCount(sql, paramMap);
        //设置分页信息
        this.parameter.getReportPageInfo().setPageInfo(totalRows);
        return totalRows;
    }

    /**
     * 使用NamedParameterJdbcTemplate查询
     *
     * @param sql
     * @param paramMap
     * @return
     */
    public List<Map<String, Object>> queryDataList(String sql, Map<String, ?> paramMap) {
        if (StringUtils.stripToNull(sql) == null)
            return null;

        this.printJdbcTemplateLog(sql, paramMap);
        try {
            NamedParameterJdbcTemplate namedParameterJdbcTemplate = this.getNamedParameterJdbcTemplate();
            List<Map<String, Object>> list = namedParameterJdbcTemplate.queryForList(sql, paramMap);
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 使用NamedParameterJdbcTemplate查询
     *
     * @param sql
     * @param paramMap
     * @return
     */
    public List<Map<String, Object>> queryPageDataList(String sql, Map<String, ?> paramMap) {
        if (StringUtils.stripToNull(sql) == null)
            return null;

        ReportPageInfo pageInfo = this.parameter.getReportPageInfo();
        int totalRows = 0;
        //启用分页
        if (pageInfo != null && pageInfo.isEnablePage()) {
            if (pageInfo.getTotalRows() == null || pageInfo.getTotalRows() < 0) {
                totalRows = this.queryCountAndSetPageInfo(sql, paramMap);
            } else {
                totalRows = pageInfo.getTotalRows();
            }
            if (totalRows < 1) {
                return null;
            }
            sql = this.getPageSql(sql);
        }

        return this.queryDataList(sql, paramMap);
    }

    public List<TextValuePair> querySelectOptionList(String sqlText, Map<String, ?> paramMap) {
        final HashSet<String> set = new HashSet<>();
        final List<TextValuePair> rows = new ArrayList<>();

        this.printJdbcTemplateLog(sqlText, paramMap);
        try {
            NamedParameterJdbcTemplate namedParameterJdbcTemplate = this.getNamedParameterJdbcTemplate();
            List<Map<String, Object>> list = namedParameterJdbcTemplate.queryForList(sqlText, paramMap);

            if (list != null && list.size() > 0) {
                for (int i = 0; i < list.size(); i++) {
                    Map<String, Object> map = list.get(i);
                    String value = MapUtils.getString(map, "value", "");
                    String text = MapUtils.getString(map, "text", "");

                    value = value.trim().length() < 1 ? "" : value.trim();
                    text = text.trim().length() < 1 ? "未设置" : text.trim();
                    if (!set.contains(value)) {
                        set.add(value);
                    }
                    rows.add(new TextValuePair(value, text));
                }

                return rows;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 打印使用
     *
     * @param sql
     */
    private void printQueryLog(String sql) {
        log.info("报表ID[{}],报表名称[{}],执行SQL[{}]",
                this.getReportParameter().getReport().getId(), this.getReportParameter().getReport().getName(), sql);
    }

    /**
     * 打印使用
     *
     * @param sql
     * @param args
     * @param argTypes
     */
    private void printJdbcTemplateLog(String sql, Object[] args, int[] argTypes) {
        log.info("报表ID[{}],报表名称[{}],执行SQL[{}],查询参数值[{}],查询参数类型[{}]",
                this.getReportParameter().getReport().getId(), this.getReportParameter().getReport().getName(), sql,

                JSONObject.toJSON(args), JSONObject.toJSON(argTypes));
    }

    /**
     * 打印使用
     *
     * @param sql
     * @param paramMap
     */
    private void printJdbcTemplateLog(String sql, Map<String, ?> paramMap) {
        log.info("报表ID[{}],报表名称[{}],执行SQL[{}],查询参数值[{}],查询参数类型[{}]",
                this.getReportParameter().getReport().getId(), this.getReportParameter().getReport().getName(), sql,
                JSONObject.toJSON(paramMap));
    }

    /*protected RowMapper<Map<String, Object>> getColumnMapRowMapper() {
        return new ColumnMapRowMapper();
    }*/


    public ReportParameter getReportParameter() {
        return this.parameter;
    }

}
