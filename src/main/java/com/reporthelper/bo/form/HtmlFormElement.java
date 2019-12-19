package com.reporthelper.bo.form;

import lombok.Data;

/**
 * 报表查询表单
 */
@Data
public abstract class HtmlFormElement {

    /**
     * 参数名称
     */
    protected String name;

    /**
     * 参数的标题内容
     */
    protected String text;

    /**
     * 参数的默认标题
     */
    protected String defaultText;

    /**
     * 参数的默认值
     */
    protected String defaultValue;

    /**
     * 标题宽度
     */
    protected int textWidth;//标题宽度

    /**
     * 文本框的宽度
     */
    protected int nameWidth;//输入框宽度

    /**
     * 输入框高度
     */
    protected int height;

    /**
     * 获取报表查询参数对应的html表单input元素，html表单input元素(select, text等)
     * 参数表单类型  下拉单选(select)、下拉多选(selectMul)、文本框(text)、日期(date)
     */
    protected String formElement;

    /**
     * 参数的数据类型，字符串、浮点数、整数、日期
     */
    protected String dataType = "string";

    /**
     * 报表查询参数备注
     */
    protected String comment;


    /**
     * 是否必填
     */
    protected boolean isRequired;

    /**
     * 触发参数名称
     * <p>
     * 表单控件是选择框，切内容来源是SQL时有效
     */
    private String triggerParamName;

    public HtmlFormElement() {
    }


    public HtmlFormElement(String name, String text) {
        this.name = name;
        this.text = text;
    }


    public HtmlFormElement(String name, String text, String defaultText, String defaultValue, int textWidth, int nameWidth, int height, String formElement, String dataType, String comment, boolean isRequired) {
        this.name = name;
        this.text = text;
        this.defaultText = defaultText;
        this.defaultValue = defaultValue;
        this.textWidth = textWidth;
        this.nameWidth = nameWidth;
        this.height = height;
        this.formElement = formElement;
        this.dataType = dataType;
        this.comment = comment;
        this.isRequired = isRequired;
    }

    public void setHtmlFormElementValue(String name, String text, String defaultText, String defaultValue,
                                        int textWidth, int nameWidth, int height, String formElement, String dataType,
                                        String comment, boolean isRequired) {
        this.name = name;
        this.text = text;
        this.defaultText = defaultText;
        this.defaultValue = defaultValue;
        this.textWidth = textWidth;
        this.nameWidth = nameWidth;
        this.height = height;
        this.formElement = formElement;
        this.dataType = dataType;
        this.comment = comment;
        this.isRequired = isRequired;
    }
}
