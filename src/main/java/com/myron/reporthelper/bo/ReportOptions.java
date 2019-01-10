package com.myron.reporthelper.bo;

import lombok.*;

import java.io.Serializable;

/**
 * 报表配置选项(rh_report表的options字段)
 *
 * @author 缪应江
 */
@SuppressWarnings("serial")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ReportOptions implements Serializable {



    /**
     * 是否启用分页.0  禁用;1  启用
     */
    private Integer enablePage;

    /**
     *  如果启用分页，每一页显示的记录条数
     */
    private int pageSize;

    /**
     * 1    报表仅显示数据表格
     * 2    报表仅显示图表
     * 10   透视表
     * 11   组合报表  CompositionReport
     * @see com.myron.reporthelper.bo.CompositionReport
     */
    private Integer showContent;


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
