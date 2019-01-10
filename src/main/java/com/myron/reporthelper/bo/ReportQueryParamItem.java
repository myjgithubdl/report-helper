package com.myron.reporthelper.bo;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 选择框  的value 和text
 */
@Data
@AllArgsConstructor
public class ReportQueryParamItem {

    /**
     * 选择框option的value
     * select  option value
     */
    private String value;

    /**
     * 选择框option的 text
     * select  option text
     */
    private String text;

    /**
     * 是否选中
     */
    private boolean selected;

    public ReportQueryParamItem() {
    }

    public ReportQueryParamItem(String value, String text) {
        this.value = value;
        this.text = text;
    }


}
