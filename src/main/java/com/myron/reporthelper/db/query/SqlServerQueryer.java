package com.myron.reporthelper.db.query;


import com.myron.reporthelper.bo.ReportDataSource;
import com.myron.reporthelper.bo.ReportParameter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * MS SQLServer 数据库查询器类。
 * 在使用该查询器时,请先参考:https://msdn.microsoft.com/library/mt484311.aspx
 * 获取sqlserver jdbc driver,然后把相关jdbc driver的jar包加入该系统的类路径下(如WEB-INF/lib)
 *
 * @author 缪应江
 */
@Slf4j
public class SqlServerQueryer extends AbstractQueryer implements Queryer {
    public SqlServerQueryer(final ReportDataSource dataSource, final ReportParameter parameter) {
        super(dataSource, parameter);
    }

    @Override
    public String getCountSql(String sqlText) {
        if (StringUtils.stripToNull(sqlText) == null)
            return null;

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT COUNT(1) AS count_num FROM ( ");
        sb.append(sqlText);
        sb.append(" )  AS  t");
        return sb.toString();
    }

    @Override
    public String getPageSql(String sql) {
        return null;
    }

}
