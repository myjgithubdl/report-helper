package com.reporthelper.bo;


import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.models.auth.In;

import java.io.Serializable;

/**
 * 报表查询参数类
 *
 * @author 缪应江
 */
public class ReportQueryParameter implements Serializable {
    private static final long serialVersionUID = 7975880105664108114L;

    /**
     * 参数名称
     */
    private String name;

    /**
     * 参数的标题内容
     */
    private String text;

    /**
     * 获取报表查询参数对应的html表单input元素，html表单input元素(select, text等)
     * 参数表单类型  下拉单选、下拉多选、文本框、日期
     *  下拉单选(select)、下拉多选(selectMul)、文本框(text)、日期(date)
     */
    private String formElement;

    /**
     * 参数值的内容来源、可以是SQL语句、文本字符串、无内容 ,如果是SQL语句、文本字符串，需要输入content
     */
    private String dataSource;

    /**
     * 获取报表查询参数对应的内容（sql语句|文本字符|空)
     * 参数值的内容来源、可以是SQL语句、文本字符串、无内容
     * <p>
     * 选择框的内容来源可以是SQL语句、文本字符串
     */
    private String content;

    /**
     * 参数的默认值
     */
    private String defaultValue;


    /**
     * 参数的默认标题
     */
    private String defaultText;


    /**
     * 参数的数据类型，字符串 string、浮点数 float、整数 integer、日期
     */
    private String dataType = "string";

    /**
     * 数据长度
     */
    private String dataLength;

    /**
     * 报表查询参数备注
     */
    private String comment;


    /**
     * 如果参数是日期  则为日期的格式YYYY-MM-DD
     */
    private String dateFormat;

    /**
     * 如果是日期字段，则是日期范围 如果是正数则输入框的默认值为当前Date加天数
     * 如果是日期字段，则是日期范围 如果是正数则输入框的默认值为当前Date减天数
     * 如果是日期字段，0标识当天，-0会null标识日期选择框是空
     */
    private Integer dateRange;

    /**
     * 获取报表查询参数的是否自动提示(主要用于下拉列表控件中)
     */
    private boolean isAutoComplete;

    /**
     * 触发参数名称
     * <p>
     * 表单控件是选择框，切内容来源是SQL时有效
     */
    private String triggerParamName;


    /**
     * 是否必填
     */
    private boolean isRequired;

    /**
     * 标题宽度
     */
    private int textWidth;//标题宽度

    /**
     * 文本框的宽度
     */
    private int nameWidth;//输入框宽度

    /**
     * 输入框高度
     */
    private int height = 25;


    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    /**
     * 获取报表查询参数名称
     *
     * @return 报表查询参数名称
     */
    public String getName() {
        return this.name;
    }

    /**
     * 设置报表查询参数名称
     *
     * @param name 参数名称
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * 获取报表查询参数对应的标题(中文名)
     *
     * @return 报表查询参数对应的标题(中文名)
     */
    public String getText() {
        if (this.text == null || this.text.trim().length() == 0) {
            return "NoTitle";
        }
        return this.text.trim();
    }

    /**
     * 设置报表查询参数对应的标题(中文名)
     *
     * @param text 报表查询参数名称对应的标题(中文名)
     */
    public void setText(final String text) {
        this.text = text;
    }

    /**
     * 获取报表查询参数对应的html表单input元素，html表单input元素(select, text等)
     *下拉单选(select)、下拉多选(selectMul)、文本框(text)、日期(date)
     * @return html表单input元素(select, text等)
     */
    public String getFormElement() {
        return this.formElement == null ? "" : this.formElement.trim();
    }

    /**
     * 设置报表查询参数对应的html表单input元素
     *
     * @param formElement html表单input元素(select,text等)
     */
    public void setFormElement(String formElement) {
        this.formElement = formElement;
    }

    /**
     * 获取报表查询参数对应的内容（sql语句|文本字符|空)
     *
     * @return 报表查询参数对应的内容（sql语句|文本字符|空)
     */
    public String getContent() {
        return this.content == null ? "" : this.content.trim();
    }

    /**
     * 设置报表查询参数对应的内容（sql语句|文本字符|空)
     *
     * @param content t报表查询参数对应的内容（sql语句|文本字符|空)
     */
    public void setContent(final String content) {
        this.content = content;
    }

    /**
     * 获取报表查询参数对应的默认值
     *
     * @return 报表查询参数对应的默认值
     */
    public String getDefaultValue() {
        return this.defaultValue;
    }

    /**
     * 设置报表查询参数对应的默认值
     *
     * @param defaultValue 报表查询参数对应的默认值
     */
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    /**
     * 获取报表查询参数的默认值对应的标题
     *
     * @return 报表查询参数的默认值对应的标题
     */
    public String getDefaultText() {
        return this.defaultText;
    }

    /**
     * 设置报表查询参数的默认值对应的标题
     *
     * @param defaultText 报表查询参数的默认值对应的标题
     */
    public void setDefaultText(String defaultText) {
        this.defaultText = defaultText;
    }

    /**
     * 获取报表查询参数的数据来源
     * 取值 为[sql:根据sql语句查询得出,text:从文本内容得出,none:无数据来源]
     *
     * @return sql|text|none
     */
    public String getDataSource() {
        return this.dataSource;
    }

    /**
     * 设置报表查询参数数据来源
     *
     * @param dataSource 取值 为[sql:根据sql语句查询得出,text:从文本内容得出,none:无数据来源]
     */
    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * 获取表查询参数的数据类型(string|float|integer|date)，默认是string
     *
     * @return
     */
    public String getDataType() {
        return (this.dataType == null || this.dataType.trim().length() == 0) ? "string" : this.dataType;
    }

    /**
     * 获取报表查询参数的数据类型(string|float|integer|date)，默认是string
     *
     * @param dataType(string|float|integer|date)
     */
    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    /**
     * 获取报表查询参数备注
     *
     * @return
     */
    public String getComment() {
        return this.comment == null ? "" : this.comment;
    }

    /**
     * 设置报表查询参数备注
     *
     * @param comment
     */
    public void setComment(String comment) {
        this.comment = comment;
    }


    /**
     * 获取报表查询参数表单控件的高度(单位：像素)
     *
     * @return 高度的像素值, 默认为25
     */
    public int getHeight() {
        return this.height;
    }

    /**
     * 设置报表查询参数表单控件的高度(单位：像素)
     *
     * @param height 高度的像素
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * 获取报表查询参数的是否必选
     *
     * @return
     */
    public boolean isRequired() {
        return this.isRequired;
    }

    /**
     * 设置报表查询参数是否必选
     *
     * @param isRequired true|false
     */
    public void setRequired(boolean isRequired) {
        this.isRequired = isRequired;
    }

    /**
     * 获取报表查询参数的是否自动提示(主要用于下拉列表控件中)
     *
     * @return true|false
     */
    public boolean isAutoComplete() {
        return this.isAutoComplete;
    }


    /**
     * 设置报表查询参数的是否自动提示(主要用于下拉列表控件中)
     *
     * @param isAutoComplete true|false
     */
    public void setAutoComplete(boolean isAutoComplete) {
        this.isAutoComplete = isAutoComplete;
    }


    /**
     * 获得假如查询参数对应的html表单input元素的类型为日期表单的时间范围
     * 假如时间为 2016-12-22 dataRange=1   表单元素默认值为2016-12-23
     * dataRange=-1   表单元素默认值为2016-12-21
     * 0 是当天   不填则为空
     *
     * @return
     */
    public Integer getDateRange() {
        return dateRange;
    }

    /**
     * 设置假如查询参数对应的html表单input元素的类型为日期表单的时间范围
     *
     * @return
     */
    public void setDateRange(Integer dataRange) {
        this.dateRange = dataRange;
    }

    /**
     * 如果表单控件是选择框，在设置了triggerParamName的值后，该值对应的表单控件如果是选择框，内容来源类型是SQL语句则将触发triggerParamName对应参数重新加载select的选项值
     *
     * @return
     */
    public String getTriggerParamName() {
        return triggerParamName;
    }

    /**
     * 设置需要触发选择框对应的参数名
     *
     * @param triggerParamName
     */
    public void setTriggerParamName(String triggerParamName) {
        this.triggerParamName = triggerParamName;
    }

    public int getTextWidth() {
        if (textWidth <= 0) {
            return 80;
        } else {
            return textWidth;
        }
    }

    public void setTextWidth(int textWidth) {
        this.textWidth = textWidth;
    }

    public int getNameWidth() {
        if (nameWidth <= 0) {
            return 120;
        } else {
            return nameWidth;
        }
    }

    public void setNameWidth(int nameWidth) {
        this.nameWidth = nameWidth;
    }

    /**
     * 获取当前查询参数是否设置了默认值
     *
     * @return true|false
     */
    @JsonIgnore
    public boolean hasDefaultValue() {
        return !this.getDefaultValue().equals("noDefaultValue");
    }

    /**
     * 获取当前查询参数是否设置了默认标题
     *
     * @return true|false
     */
    @JsonIgnore
    public boolean hasDefaultText() {
        return !this.getDefaultText().equals("noDefaultText");
    }

    /**
     * 获取当前查询参数的真实默认标题
     *
     * @return
     */
    @JsonIgnore
    public String getRealDefaultText() {
        return this.hasDefaultText() ? this.getDefaultText() : "";
    }

    /**
     * 获取当前查询参数的真实默认值
     *
     * @return
     */
    @JsonIgnore
    public String getRealDefaultValue() {
        return this.hasDefaultValue() ? this.getDefaultValue() : "";
    }




}
