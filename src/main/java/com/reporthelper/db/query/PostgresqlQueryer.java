package com.reporthelper.db.query;

import com.reporthelper.bo.ReportDataSource;
import com.reporthelper.bo.ReportPageInfo;
import com.reporthelper.bo.ReportParameter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * Postgresql数据库查询器类。
 * 在使用该查询器时,请先参考:https://jdbc.postgresql.org/download.html
 * 获取与相应版本的Postgresql jdbc driver,然后把相关jdbc driver的jar包加入该系统的类路径下(如WEB-INF/lib)
 *
 * @author 缪应江
 */

@Slf4j
public class PostgresqlQueryer extends AbstractQueryer implements Queryer {

    public PostgresqlQueryer(final ReportDataSource dataSource, final ReportParameter parameter) {
        super(dataSource, parameter);
    }

    @Override
    public String getCountSql(String sqlText) {
        if (StringUtils.stripToNull(sqlText) == null)
            return null;

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT COUNT(1) as count_num FROM ( ");
        sb.append(sqlText);
        sb.append(" ) AS t");

        return sb.toString();
    }

    @Override
    public String getPageSql(String sqlText) {
        ReportPageInfo pageInfo = this.parameter.getReportPageInfo();
        //启用分页信息
        if (pageInfo != null && pageInfo.isEnablePage()) {
            StringBuilder pageSqlSb = new StringBuilder();
            pageSqlSb.append("SELECT * FROM ( ");
            pageSqlSb.append(sqlText);
            pageSqlSb.append(" ) AS PAGE_TABLE_QUERY  LIMIT ");
            pageSqlSb.append(pageInfo.getPageSize() + " OFFSET " + (pageInfo.getPageIndex() - 1) * pageInfo.getPageSize());

            sqlText = pageSqlSb.toString();
        }

        return sqlText;
    }


}
