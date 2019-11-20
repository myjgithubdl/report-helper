package com.reporthelper.bo;

import com.easydata.head.TheadColumn;
import lombok.*;

import java.util.List;

/**
 * 报表查询的数据
 *
 * @author Myron
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReportQueryData {

    /**
     * 查询数量
     */
    private Integer count;

    /**
     * 分页大小
     */
    private Integer pageSize;
    /**
     * 数据列表
     */
    private List data;
    /**
     * 报表说明
     */
    private String explain;

    /**
     * 数据列表转化出的HTML表格
     */
    private String tableHtml;

    /**
     * 元数据列
     */
    List<TheadColumn> theadColumn;

}
