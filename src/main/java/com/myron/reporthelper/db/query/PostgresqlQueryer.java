package com.myron.reporthelper.db.query;

import com.myron.reporthelper.bo.ReportDataSource;
import com.myron.reporthelper.bo.ReportPageInfo;
import com.myron.reporthelper.bo.ReportParameter;
import org.apache.commons.lang3.StringUtils;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Postgresql数据库查询器类。
 * 在使用该查询器时,请先参考:https://jdbc.postgresql.org/download.html
 * 获取与相应版本的Postgresql jdbc driver,然后把相关jdbc driver的jar包加入该系统的类路径下(如WEB-INF/lib)
 *
 * @author 缪应江
 */
public class PostgresqlQueryer extends AbstractQueryer implements Queryer {
    public PostgresqlQueryer(final ReportDataSource dataSource, final ReportParameter parameter) {
        super(dataSource, parameter);
    }

    @Override
    protected String processParseMetaDataColumnsSql(String sqlText) {
        if (StringUtils.stripToNull(sqlText) == null)
            return null;

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM ( ");
        sb.append(sqlText);
        sb.append(" ) AS  t limit 1");

        return sb.toString();
    }

    @Override
    public int queryCount(String sqlText) {
        if (StringUtils.stripToNull(sqlText) == null)
            return 0;

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT COUNT(1) as count_num FROM ( ");
        sb.append(sqlText);
        sb.append(" ) AS t");
        int totalRows = queryCountAndSetPageInfo(sb.toString());

        return totalRows;
    }

    @Override
    public List<Map<String, Object>> queryForList(String sqlText) {
        ReportPageInfo pageInfo = this.parameter.getReportPageInfo();
        //启用分页信息
        if (pageInfo != null && pageInfo.isEnablePage()) {
            //总记录数为null ， 查询分页信息
            if (pageInfo.getTotalRows() == null) {
                queryCount(sqlText);
            }

            StringBuilder pageSqlSb = new StringBuilder();
            pageSqlSb.append("SELECT * FROM ( ");
            pageSqlSb.append(sqlText);
            pageSqlSb.append(" ) AS PAGE_TABLE_QUERY  LIMIT ");
            pageSqlSb.append(pageInfo.getPageSize() + " OFFSET " + (pageInfo.getPageIndex() - 1) * pageInfo.getPageSize());

            sqlText = pageSqlSb.toString();
        }


        try {
            List<Map<String, Object>> list = queryDataList(sqlText);
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
