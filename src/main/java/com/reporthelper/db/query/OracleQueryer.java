package com.reporthelper.db.query;


import com.reporthelper.bo.ReportDataSource;
import com.reporthelper.bo.ReportPageInfo;
import com.reporthelper.bo.ReportParameter;
import lombok.extern.slf4j.Slf4j;

/**
 * Oracle数据库查询器类。
 * 在使用该查询器时,请先参考:http://www.oracle.com/technetwork/database/enterprise-edition/jdbc-112010-090769.html
 * 获取与相应版本的Oracle jdbc driver,然后把相关jdbc driver的jar包加入该系统的类路径下(如WEB-INF/lib)
 *
 * @author Myron
 */
@Slf4j
public class OracleQueryer extends AbstractQueryer implements Queryer {

    public OracleQueryer(final ReportDataSource dataSource, final ReportParameter parameter) {
        super(dataSource, parameter);
    }

    @Override
    public String getCountSql(String sqlText) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT COUNT(1) FROM (  ");
        sb.append(sqlText);
        sb.append("  ) ");
        return sqlText;
    }

    @Override
    public String getPageSql(String sqlText) {
        ReportPageInfo pageInfo = this.parameter.getReportPageInfo();

        //启用分页信息
        if (pageInfo != null && pageInfo.isEnablePage()) {
            StringBuilder pageSqlSb = new StringBuilder();
            pageSqlSb.append(" SELECT * FROM (  ");
            pageSqlSb.append(" SELECT ROWNUM PAGE_ROWNUM , PAGE_TABLE_QUERY1.* FROM ( ");
            pageSqlSb.append(sqlText);
            pageSqlSb.append(" )  PAGE_TABLE_QUERY1 ");
            pageSqlSb.append("WHERE PAGE_ROWNUM>=" + ((pageInfo.getPageIndex() - 1) * pageInfo.getPageSize() + 1) + " AND PAGE_ROWNUM<=" + (pageInfo.getPageIndex() * pageInfo.getPageSize()));

            sqlText = pageSqlSb.toString();
        }
        return sqlText;
    }

}
