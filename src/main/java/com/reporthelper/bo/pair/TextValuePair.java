package com.reporthelper.bo.pair;

/**
 * @author tomdeng
 */
public class TextValuePair {
    private String text;
    private String value;
    private boolean selected;

    public TextValuePair() {
    }

    public TextValuePair(String value,String text) {
        this.text = text;
        this.value = value;
    }

    public TextValuePair(String value, String text, boolean selected) {
        this.text = text;
        this.value = value;
        this.selected = selected;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isSelected() {
        return this.selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
