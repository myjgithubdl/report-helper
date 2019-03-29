package com.myron.reporthelper.bo;

import com.easydata.head.TheadColumn;
import com.myron.reporthelper.entity.Report;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 报表查询时的参数类
 *
 * @author 缪应江
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportParameter {
    /**
     * 报表
     */
    private Report report;

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
