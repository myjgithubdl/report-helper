package com.myron.reporthelper.db.query;


import com.myron.reporthelper.bo.ReportDataSource;
import com.myron.reporthelper.bo.ReportPageInfo;
import com.myron.reporthelper.bo.ReportParameter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Oracle数据库查询器类。
 * 在使用该查询器时,请先参考:http://www.oracle.com/technetwork/database/enterprise-edition/jdbc-112010-090769.html
 * 获取与相应版本的Oracle jdbc driver,然后把相关jdbc driver的jar包加入该系统的类路径下(如WEB-INF/lib)
 *
 * @author 缪应江
 */
@Slf4j
public class OracleQueryer extends AbstractQueryer implements Queryer {
    public OracleQueryer(final ReportDataSource dataSource, final ReportParameter parameter) {
        super(dataSource, parameter);
    }

    @Override
    protected String processParseMetaDataColumnsSql(String sqlText) {
        if (StringUtils.stripToNull(sqlText) == null)
            return null;

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT COUNT(1) FROM (  ");
        sb.append(sqlText);
        sb.append("  )   RECORD_SIZE_TABLE");


        String newSqlText = "SELECT COUNT(1) FROM (  " + this.parameter.getSqlText() + "  )   RECORD_SIZE_TABLE";


        return newSqlText;
    }

    @Override
    public int queryCount(String sqlText) {

        if (StringUtils.stripToNull(sqlText) == null)
            return 0;

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT COUNT(1) FROM (  ");
        sb.append(sqlText);
        sb.append("  ) ");

        int totalRows = queryCountAndSetPageInfo(sb.toString());
        log.info("查询数量SQL:"+sb.toString());
        return totalRows;
    }

    @Override
    public List<Map<String, Object>> queryForList(String sqlText) {


        ReportPageInfo pageInfo = this.parameter.getReportPageInfo();

        //启用分页信息
        if (pageInfo != null && pageInfo.isEnablePage()) {
            //总记录数为null ， 查询分页信息
            if (pageInfo.getTotalRows() == null || pageInfo.getTotalRows() < 0) {
                queryCount(sqlText);
            }


            StringBuilder pageSqlSb = new StringBuilder();
            pageSqlSb.append(" SELECT * FROM (  ");
            pageSqlSb.append(" SELECT ROWNUM PAGE_ROWNUM , PAGE_TABLE_QUERY1.* FROM ( ");
            pageSqlSb.append(sqlText);
            pageSqlSb.append(" )  PAGE_TABLE_QUERY1 ");
            pageSqlSb.append("WHERE PAGE_ROWNUM>=" + ((pageInfo.getPageIndex() - 1) * pageInfo.getPageSize() + 1) + " AND PAGE_ROWNUM<=" + (pageInfo.getPageIndex() * pageInfo.getPageSize()));

            sqlText = pageSqlSb.toString();
        }
        log.info("查询数据SQL:"+sqlText);
        try {
            List<Map<String, Object>> list = queryDataList(sqlText);
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
