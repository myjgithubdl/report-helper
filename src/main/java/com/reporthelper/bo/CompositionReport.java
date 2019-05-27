package com.reporthelper.bo;

import lombok.Data;

/**
 * 组合报表
 */
@Data
public class CompositionReport {

    private String name;//构成报表的标题
    private String href;//构成报表的url
    private int width;//构成报表的宽度（百分比）
    private int height;//构成报表的高度（百分比）


    private String cssStyle;//css 属性，该值有其他css属性转换而成；

}
