package com.myron.reporthelper.bo.form;

import com.myron.reporthelper.bo.pair.TextValuePair;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 选择框
 */
@Data
public class SelectHtmlForm extends HtmlFormElement {

    /**
     * 是否是多选
     */
    private boolean isMultiple;

    /**
     * 触发参数名称
     * <p>
     * 表单控件是选择框，切内容来源是SQL时有效
     */
    private String triggerParamName;


    /**
     * 选择框的选项内容
     */
    private List<TextValuePair> optionList;


    public SelectHtmlForm() {
    }

    public SelectHtmlForm(String name, String text) {
        super(name, text);
    }


    public SelectHtmlForm(boolean isMultiple, String triggerParamName, List<TextValuePair> optionList) {
        this.isMultiple = isMultiple;
        this.triggerParamName = triggerParamName;
        this.optionList = optionList;
    }
}
