package com.reporthelper.util;


import com.easydata.data.DataColumnTree;
import com.easydata.data.DataColumnTreeUtil;
import com.easydata.head.TheadColumn;
import com.easydata.head.TheadColumnTree;
import com.easydata.head.TheadColumnTreeUtil;
import com.easydata.utils.ParamsUtil;

import java.util.List;
import java.util.Map;

/**
 * 将数据转化为HTML的table标签(Laiui 版本)
 */
public class LayuiHtmlTableUtil {

    /**
     * 根据表头和数据转化为html table 表格
     *
     * @param theadColumnList
     * @param dataList
     * @return
     */
    public static String getHtmlTable(List<TheadColumn> theadColumnList, List<Map<String, Object>> dataList) {
        if (theadColumnList == null || theadColumnList.size() == 0) {
            return null;
        }

        StringBuilder htmlSb = new StringBuilder();
        htmlSb.append("<table lay-filter=\"static-table\"> ");
        //将表头列转化为Excel导出需要的的表头
        List<List<TheadColumnTree>> theadColumnLists = TheadColumnTreeUtil.getFromtTheadColumnTreeList(theadColumnList, false);
        List<TheadColumnTree> lastRowTheadColumns = TheadColumnTreeUtil.getLeafNodes(theadColumnLists);


        List<List<DataColumnTree>> dataColumnTreeLists = DataColumnTreeUtil.getExportFormatDataList(lastRowTheadColumns, dataList);

        String head = createHtmlTableThead(theadColumnLists);
        if (head != null) {
            htmlSb.append(head);
        }

        String body = createHtmlTableTbody(lastRowTheadColumns, dataList, dataColumnTreeLists);
        if (body != null) {
            htmlSb.append(body);

        }
        htmlSb.append("</table>");

        return htmlSb.toString();
    }


    /**
     * 创建HTML的
     *
     * @param theadColumnLists
     * @return
     */
    public static String createHtmlTableThead(List<List<TheadColumnTree>> theadColumnLists) {
        if (theadColumnLists == null) {
            return null;
        }
        StringBuilder htmlSbTableThead = new StringBuilder();
        //<thead>
        htmlSbTableThead.append("<thead>");

        /*************  写表头   start  *************/
        if (theadColumnLists != null && theadColumnLists.size() > 0) {
            for (int i = 0, j = theadColumnLists.size(); i < j; i++) {
                List<TheadColumnTree> theadColumnTreeList = theadColumnLists.get(i);

                //行
                StringBuilder htmlSbTheadTr = new StringBuilder();
                htmlSbTheadTr.append("<tr>");

                for (int m = 0, n = theadColumnTreeList.size(); m < n; m++) {
                    TheadColumnTree theadColumnTree = theadColumnTreeList.get(m);
                    //列
                    StringBuilder htmlSbTheadTh = new StringBuilder();
                    htmlSbTheadTh.append("<th ");

                    StringBuilder htmlSbTheadThStyle = new StringBuilder();
                    StringBuilder layData = new StringBuilder();
                    //layData.append("{field:'" + theadColumnTree.getName() + "',minWidth:60");
                    //防止列名相同，layui解析key相同，导致同名列不同值（比如一列是跳转地址，一列不止）的单元格值相同
                    layData.append("{field:'" + theadColumnTree.getName() + "_" + theadColumnTree.getCelIndex() + "',minWidth:60");


                    int colspan = theadColumnTree.getColspan();
                    int rowspan = theadColumnTree.getRowspan();
                    String text = theadColumnTree.getText();

                    //设置对齐方式  align  left,right,center
                    if (theadColumnTree.getTheadTextAlign() != null
                            && theadColumnTree.getTheadTextAlign().length() > 0) {
                        if ("center".equals(theadColumnTree.getTheadTextAlign())) {
                            htmlSbTheadTh.append("align=\"center\" ");
                            layData.append(",align:'center'");
                        } else if ("left".equals(theadColumnTree.getTheadTextAlign())) {
                            htmlSbTheadTh.append("align=\"left\" ");
                            layData.append(",align:'left'");
                        } else if ("right".equals(theadColumnTree.getTheadTextAlign())) {
                            htmlSbTheadTh.append("align=\"right\" ");
                            layData.append(",align:'right'");
                        }
                    }

                    //设置跨行
                    if (rowspan > 1) { //合并行
                        htmlSbTheadTh.append("rowspan=\"" + rowspan + "\" ");
                    }

                    //设置合并列
                    if (colspan > 1) { //合并列
                        htmlSbTheadTh.append("colspan=\"" + colspan + "\" ");
                    }

                    //设置列宽
                    String columnWidth = getColumnWidth(theadColumnTree);
                    if (columnWidth != null && columnWidth.length() > 0) {
                        htmlSbTheadThStyle.append("width:" + columnWidth + ";");
                        layData.append(",width:" + columnWidth.replaceAll("px", ""));
                    }

                    if (htmlSbTheadThStyle.toString().length() > 0) {
                        htmlSbTheadTh.append(" style=\"" + htmlSbTheadThStyle.toString() + "\"");

                    }
                    //排序
                    if (theadColumnTree.isSort()) {
                        layData.append(",sort:true");
                    }

                    layData.append("}");

                    htmlSbTheadTh.append(" lay-data=\"" + layData + "\"");

                    htmlSbTheadTh.append(">");
                    htmlSbTheadTh.append(text);
                    htmlSbTheadTh.append("</th>");

                    htmlSbTheadTr.append(htmlSbTheadTh);
                }

                htmlSbTheadTr.append("</tr>");

                htmlSbTableThead.append(htmlSbTheadTr.toString());
            }
        }
        htmlSbTableThead.append("</thead>");

        return htmlSbTableThead.toString();
    }


    /**
     * 创建HTML的
     *
     * @param dataColumnTreeLists
     * @return
     */
    public static String createHtmlTableTbody(List<TheadColumnTree> lastRowTheadColumns, List<Map<String, Object>> dataLists, List<List<DataColumnTree>> dataColumnTreeLists) {
        if (dataColumnTreeLists == null) {
            return null;
        }
        StringBuilder htmlSbTableThead = new StringBuilder();
        //<thead>
        htmlSbTableThead.append("<tbody>");

        /*************  写表头   start  *************/
        if (dataColumnTreeLists != null && dataColumnTreeLists.size() > 0) {
            for (int i = 0, j = dataColumnTreeLists.size(); i < j; i++) {
                List<DataColumnTree> dataColumnTrees = dataColumnTreeLists.get(i);

                //行
                StringBuilder htmlSbTheadTr = new StringBuilder();
                htmlSbTheadTr.append("<tr>");

                for (int m = 0, n = dataColumnTrees.size(); m < n; m++) {
                    TheadColumnTree theadColumnTree = lastRowTheadColumns.get(m);
                    //列
                    StringBuilder htmlSbTheadTh = new StringBuilder();
                    //htmlSbTheadTh.append("<td data-field='" + theadColumnTree.getName() + "'");
                    //防止列名相同，layui解析key相同，导致同名列不同值（比如一列是跳转地址，一列不止）的单元格值相同
                    htmlSbTheadTh.append("<td data-field='" + theadColumnTree.getName() + "_" + theadColumnTree.getCelIndex() + "'");

                    StringBuilder htmlSbTbodyThStyle = new StringBuilder();


                    DataColumnTree dataColumnTree = dataColumnTrees.get(m);
                    int colspan = dataColumnTree.getColspan();
                    int rowspan = dataColumnTree.getRowspan();
                    boolean isHidden = dataColumnTree.isHidden();
                    if (isHidden) {
                        continue;
                    }

                    Object text = dataColumnTree.getValue() == null ? "" : dataColumnTree.getValue();

                    //设置跨行
                    if (rowspan > 1) { //合并行
                        htmlSbTheadTh.append(" rowspan=\"" + rowspan + "\" ");
                    }

                    //设置合并列
                    if (colspan > 1) { //合并列
                        htmlSbTheadTh.append(" colspan=\"" + colspan + "\" ");
                    }


                    if (htmlSbTbodyThStyle.toString().length() > 0) {
                        htmlSbTheadTh.append(" style=\"" + htmlSbTbodyThStyle.toString() + "\"");

                    }

                    htmlSbTheadTh.append(">");

                    //检查是不是连接，如果是设置<a>标签
                    if (theadColumnTree.getHref() != null && theadColumnTree.getHref().length() > 0) {
                        //<a href="http://www.w3school.com.cn">W3School</a>
                        StringBuilder htmlSba = new StringBuilder();
                        htmlSba.append("<a ");
                        if (theadColumnTree.getHrefTarget() != null && theadColumnTree.getHrefTarget().length() > 0) {
                            htmlSba.append(" target=\"" + theadColumnTree.getHrefTarget() + "\"");
                        }
                        htmlSba.append(" href=\"" + ParamsUtil.replaceAllParams(theadColumnTree.getHref(), dataLists.get(i)) + "\"");
                        htmlSba.append(">" + text);
                        htmlSba.append("</a>");
                        text = htmlSba.toString();
                    }

                    htmlSbTheadTh.append(text);
                    htmlSbTheadTh.append("</td>");

                    htmlSbTheadTr.append(htmlSbTheadTh);
                }

                htmlSbTheadTr.append("</tr>");

                htmlSbTableThead.append(htmlSbTheadTr.toString());
            }
        }
        htmlSbTableThead.append("</tbody>");

        return htmlSbTableThead.toString();
    }

    /**
     * 计算列的宽度
     *
     * @param theadColumnTree
     * @return
     */
    public static String getColumnWidth(TheadColumnTree theadColumnTree) {
        String columnWidth = "";
        if (theadColumnTree.getColumnWidth() != null && theadColumnTree.getColumnWidth() > 0) {
            columnWidth = theadColumnTree.getColumnWidth() + "px";
        }
        //其实，这个参数的单位是1/256个字符宽度，也就是说，这里是把B列的宽度设置为了columnWidth个字符。
        return columnWidth;
    }

}
