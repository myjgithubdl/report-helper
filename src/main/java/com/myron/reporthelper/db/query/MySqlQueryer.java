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
 *
 */
@Slf4j
public class MySqlQueryer extends AbstractQueryer implements Queryer {


    public MySqlQueryer(final ReportDataSource dataSource, final ReportParameter parameter) {
        super(dataSource, parameter);
    }



    @Override
    protected String processParseMetaDataColumnsSql(String sqlText) {
        if (StringUtils.stripToNull(sqlText) == null)
            return null;

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM ( ");
        sb.append(sqlText);
        sb.append(" ) t limit 1");

        return sb.toString();
    }


    @Override
    public int queryCount(String sqlText) {
        if (StringUtils.stripToNull(sqlText) == null)
            return 0;

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT COUNT(1) as count_num FROM ( ");
        sb.append(sqlText);
        sb.append(" ) t");
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

            int offset=(pageInfo.getPageIndex() - 1) * pageInfo.getPageSize();
            offset=offset<0 ? 0 : offset;

            StringBuilder pageSqlSb = new StringBuilder();
            pageSqlSb.append(" SELECT * FROM ( ");
            pageSqlSb.append(sqlText);
            pageSqlSb.append(" ) AS PAGE_TABLE_QUERY  LIMIT ");
            pageSqlSb.append(offset + " , " + pageInfo.getPageSize());

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
