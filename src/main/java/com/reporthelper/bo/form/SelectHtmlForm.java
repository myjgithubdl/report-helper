package com.reporthelper.bo.form;

import com.reporthelper.bo.pair.TextValuePair;
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
        this.optionList = optionList;
        this.setTriggerParamName(triggerParamName);
    }
}
