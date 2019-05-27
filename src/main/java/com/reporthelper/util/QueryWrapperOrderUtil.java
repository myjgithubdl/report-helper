package com.reporthelper.util;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.commons.lang3.StringUtils;

public class QueryWrapperOrderUtil {

    /**
     * 设置查询字段
     * @param queryWrapper
     * @param dataGridPager
     * @return
     */
    public static QueryWrapper setOrderBy(QueryWrapper queryWrapper, DataGridPager dataGridPager) {
        if (StringUtils.isNoneEmpty(dataGridPager.getSort())) {
            if ("asc".equals(dataGridPager.getOrder())) {
                queryWrapper.orderByAsc(dataGridPager.getSort());
            } else if ("desc".equals(dataGridPager.getOrder())) {
                queryWrapper.orderByDesc(dataGridPager.getSort());
            } else {
                queryWrapper.orderByAsc(dataGridPager.getSort());
            }
        }
        return queryWrapper;
    }


}
