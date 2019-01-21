package com.myron.reporthelper.bo.form;

import lombok.Builder;
import lombok.Data;

@Data
public class DateHtmlForm extends HtmlFormElement {


    /**
     * 如果参数是日期  则为日期的格式YYYY-MM-DD
     */
    private String dateFormat;

    /**
     * 如果是日期字段，则是日期范围 如果是正数则输入框的默认值为当前Date加天数
     * 如果是日期字段，则是日期范围 如果是正数则输入框的默认值为当前Date减天数
     * 如果是日期字段，0标识当天，-0或空标识日期选择框是空
     */
    private Integer dateRange;


    public DateHtmlForm() {
    }

    public DateHtmlForm( String dateFormat, Integer dateRange) {
        this.dateFormat = dateFormat;
        this.dateRange = dateRange;
    }
}
