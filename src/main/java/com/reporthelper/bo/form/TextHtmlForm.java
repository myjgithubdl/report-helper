package com.reporthelper.bo.form;

import lombok.Builder;
import lombok.Data;

/**
 * 输入框查询表单
 */
@Data
public class TextHtmlForm extends HtmlFormElement {

    /**
     * 数据长度
     */
    private Integer dataLength;

    public TextHtmlForm() {
    }

    public TextHtmlForm(String name, String text) {
        super(name, text);
    }

    public TextHtmlForm(Integer dataLength) {
        this.dataLength=dataLength;
    }




}
