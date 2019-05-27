package com.reporthelper.bo;

import lombok.*;

import java.io.Serializable;

/**
 * 报表配置选项(rh_report表的options字段)
 *
 * @author Myron
 */
@SuppressWarnings("serial")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ReportOptions implements Serializable {



    /**
     * 是否启用分页.0  禁用;1  启用
     * 启用分页则分页信息是
     * @see ReportPageInfo
     */
    private Integer enablePage;

    /**
     *  如果启用分页，每一页显示的记录条数
     */
    private int pageSize;

    /**
     * 1    报表仅显示数据表格
     * 2    显示图表  折线图
     * 3    显示图表  柱状图
     * 4    显示图表  饼图
     * 5    显示图表  漏斗图
     * 6    显示图表  散点图
     * 10   透视表
     * 11   组合报表  CompositionReport
     * @see com.reporthelper.bo.CompositionReport
     */
    private int showContent;


    /**
     * 是否增加统计航
     */
    private boolean isStatistics;


    /**
     * 合计行位置
     * bottom、top
     */
    private String statisticsRowPosition;



}
