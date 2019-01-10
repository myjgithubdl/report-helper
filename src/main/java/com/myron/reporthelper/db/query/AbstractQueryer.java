package com.myron.reporthelper.db.query;

import com.easydata.head.TheadColumn;
import com.myron.reporthelper.bo.ReportDataSource;
import com.myron.reporthelper.bo.ReportParameter;
import com.myron.reporthelper.bo.ReportQueryParamItem;
import com.myron.reporthelper.util.JdbcUtil;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 *
 */

@Data
public abstract class AbstractQueryer {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    protected final ReportDataSource reportDataSource;
    protected final ReportParameter parameter;
    protected final List<TheadColumn> metaDataColumns;

    protected AbstractQueryer(final ReportDataSource reportDataSource, final ReportParameter parameter) {
        this.reportDataSource = reportDataSource;
        this.parameter = parameter;
        this.metaDataColumns = this.parameter == null ?
                new ArrayList<>(0) :
                new ArrayList<>(this.parameter.getMetaColumns());
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
            this.logger.debug("Parse Report MetaDataColumns SQL:{},", sqlText);
            conn = this.getJdbcConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(this.processParseMetaDataColumnsSql(sqlText));
            final ResultSetMetaData rsMataData = rs.getMetaData();
            final int count = rsMataData.getColumnCount();
            columns = new ArrayList<>(count);
            for (int i = 1; i <= count; i++) {
                final TheadColumn column = new TheadColumn();
                column.setId(i+"");
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
     * 处理解析元数据列的SQL，将查询记录限制为一条，预防SQL查询时数据量太大影响性能
     *
     * @param sqlText
     * @return
     */
    protected abstract String processParseMetaDataColumnsSql(final String sqlText);


    /**
     * 查询参数类型为select切内容是sql的option内容
     *
     * @param sqlText
     * @return
     */
    public List<ReportQueryParamItem> parseQueryParamItems(final String sqlText) {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        final HashSet<String> set = new HashSet<>();
        final List<ReportQueryParamItem> rows = new ArrayList<>();

        try {
            this.logger.debug(sqlText);
            conn = this.getJdbcConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sqlText);
            while (rs.next()) {
                String name = rs.getString("name");
                String text = rs.getString("text");
                name = (name == null) ? "" : name.trim();
                text = (text == null) ? "" : text.trim();
                if (!set.contains(name)) {
                    set.add(name);
                }
                rows.add(new ReportQueryParamItem(name, text));
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
            DataSource reportDataSource = JdbcUtil.getDataSource(this.reportDataSource);
            JdbcTemplate jdbcTemplate = new JdbcTemplate(reportDataSource);
            return jdbcTemplate;
        } catch (Exception e) {
            e.printStackTrace();

        }
        return null;
    }


    /**
     * 获取记录总数并设置分页信息
     *
     * @param sql
     */
    protected int queryCountAndSetPageInfo(String sql) {
        int totalRows = 0;
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            this.logger.debug(sql);
            conn = this.getJdbcConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                totalRows = rs.getInt(1);
            }
            this.parameter.getReportPageInfo().setPageInfo(totalRows);
        } catch (final SQLException ex) {
            throw new RuntimeException(ex);
        } finally {
            JdbcUtil.releaseJdbcResource(conn, stmt, rs);
        }
        return totalRows;
    }


    /**
     * 将SQL 查询的结果转为List<Map<String, Object>>
     *
     * @param sql
     * @return
     * @throws SQLException
     */
    public List<Map<String, Object>> queryDataList(String sql) throws SQLException {

        List<Map<String, Object>> results = new ArrayList();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            this.logger.debug(sql);
            conn = this.getJdbcConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            int index = 0;
            //调用Spring的实现方法
            ColumnMapRowMapper columnMapRowMapper = new ColumnMapRowMapper();
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


}
