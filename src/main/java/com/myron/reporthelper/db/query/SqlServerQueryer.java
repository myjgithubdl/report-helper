package com.myron.reporthelper.db.query;


import com.myron.reporthelper.bo.ReportDataSource;
import com.myron.reporthelper.bo.ReportParameter;

import java.util.List;
import java.util.Map;

/**
 * MS SQLServer 数据库查询器类。
 * 在使用该查询器时,请先参考:https://msdn.microsoft.com/library/mt484311.aspx
 * 获取sqlserver jdbc driver,然后把相关jdbc driver的jar包加入该系统的类路径下(如WEB-INF/lib)
 *
 * @author 缪应江
 */
public class SqlServerQueryer extends AbstractQueryer implements Queryer {
    public SqlServerQueryer(final ReportDataSource dataSource, final ReportParameter parameter) {
        super(dataSource, parameter);
    }

    @Override
    protected String processParseMetaDataColumnsSql(String sqlText) {
        return null;
    }

    @Override
    public int queryCount(String sqlText) {
        return 0;
    }

    @Override
    public List<Map<String, Object>> queryForList(String sqlText) {
        return null;
    }
}
