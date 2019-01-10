package com.myron.reporthelper.bo;

import com.easydata.head.TheadColumn;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * 报表查询时的参数类
 *
 * @author 缪应江
 */
@Data
@AllArgsConstructor
public class ReportParameter {
    /**
     * 报表唯一id
     */
    private String id;
    /**
     * 报表名称
     */
    private String name;

    /**
     * 查询SQL
     */
    private String sqlText;


    /**
     * 报表的元数据列
     */
    private List<TheadColumn> metaColumns;


    /**
     * 分页信息
     */
    private ReportPageInfo reportPageInfo ;

}
