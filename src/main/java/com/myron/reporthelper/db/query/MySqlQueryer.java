package com.myron.reporthelper.db.query;

import com.myron.reporthelper.bo.ReportDataSource;
import com.myron.reporthelper.bo.ReportPageInfo;
import com.myron.reporthelper.bo.ReportParameter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 *
 */
@Slf4j
public class MySqlQueryer extends AbstractQueryer implements Queryer {


    public MySqlQueryer(final ReportDataSource dataSource, final ReportParameter parameter) {
        super(dataSource, parameter);
    }

    @Override
    public String getCountSql(String sqlText) {
        if (StringUtils.stripToNull(sqlText) == null)
            return null;

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT COUNT(1) as count_num FROM ( ");
        sb.append(sqlText);
        sb.append(" ) t");

        return sb.toString();
    }

    @Override
    public String getPageSql(String sqlText) {
        if (StringUtils.stripToNull(sqlText) == null)
            return null;

        ReportPageInfo pageInfo = this.parameter.getReportPageInfo();
        //启用分页信息
        if (pageInfo != null && pageInfo.isEnablePage()) {
            int offset = (pageInfo.getPageIndex() - 1) * pageInfo.getPageSize();
            offset = offset < 0 ? 0 : offset;

            StringBuilder pageSqlSb = new StringBuilder();
            pageSqlSb.append(" SELECT * FROM ( ");
            pageSqlSb.append(sqlText);
            pageSqlSb.append(" ) AS PAGE_TABLE_QUERY  LIMIT ");
            pageSqlSb.append(offset + " , " + pageInfo.getPageSize());

            sqlText = pageSqlSb.toString();
        }
        return sqlText;
    }

}
