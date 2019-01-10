package com.myron.reporthelper.db.pool;


import com.myron.reporthelper.bo.ReportDataSource;

import javax.sql.DataSource;

/**
 * 数据源连接包装器
 *
 */
public interface DataSourcePoolWrapper {
    DataSource wrap(ReportDataSource rptDs);
}
