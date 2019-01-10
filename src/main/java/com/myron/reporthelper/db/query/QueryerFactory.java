package com.myron.reporthelper.db.query;


import com.myron.reporthelper.bo.ReportDataSource;
import com.myron.reporthelper.bo.ReportParameter;

import java.lang.reflect.Constructor;

/**
 * 报表查询器工厂方法类
 *
 * @author 缪应江
 */
public class QueryerFactory {
    public static Queryer create(final ReportDataSource dataSource) {
        return create(dataSource, null);
    }

    public static Queryer create(final ReportDataSource dataSource, final ReportParameter parameter) {
        if (dataSource != null) {
            try {
                final Class<?> clazz = Class.forName(dataSource.getQueryerClass());
                final Constructor<?> constructor = clazz.getConstructor(ReportDataSource.class, ReportParameter.class);
                return (Queryer) constructor.newInstance(dataSource, parameter);
            } catch (final Exception ex) {
                throw new RuntimeException("create report engine queryer error", ex);
            }
        }
        return null;
    }
}
