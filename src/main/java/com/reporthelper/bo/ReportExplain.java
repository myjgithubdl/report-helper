package com.reporthelper.bo;

import lombok.Data;

/**
 * 报表的说明
 */
@Data
public class ReportExplain {
    /**
     * 说明位置
     * top：顶部
     * bottom：底部
     */
    private String position;

    /**
     * 说明的HTML
     * 支持将sqlText查询的内容引入到html ，${reportTipContent}
     */
    private String explainHtml;

    /**
     * 说明的SQL
     */
    private String explainSqlText;

    /**
     * 在导出报表数据时是否导出注释
     * 1：允许导出
     * 2：禁止导出注释
     */
    private Integer isAllowExport;


}
