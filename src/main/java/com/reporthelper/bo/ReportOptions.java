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
     * 是否增加统计行
     */
    private boolean isStatistics;


    /**
     * 合计行位置
     * bottom、top
     */
    private String statisticsRowPosition;

    /**
     * left:左浮动  right:右浮动
     */
    private String floatCss;

    /**
     * 报表宽度
     */
    private Integer reportWidth;

    /**
     * 宽度单位
     */
    private String widthUnit;

    /**
     * 报表高度
     */
    private Integer reportHeight;

    /**
     * 高度单位
     */
    private String heightUnit;

    /**
     * 当showContent是外部资源时的内容显示地址
     */
    private String href;

    /**
     * 外边框值与单位
     */
    private Integer marginTop;
    private Integer marginRight;
    private Integer marginBottom;
    private Integer marginLeft;
    private String marginUnit;

    /**
     * 外边框值与单位
     */
    private Integer paddingTop;
    private Integer paddingRight;
    private Integer paddingBottom;
    private Integer paddingLeft;
    private String paddingUnit;

}
