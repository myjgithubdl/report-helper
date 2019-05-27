package com.reporthelper.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 报表分页信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("serial")
public class ReportPageInfo implements Serializable {
    /**
     * 是否启用分页.0  禁用;1  启用
     */
    private boolean isEnablePage;

    /**
     * 每一页显示的记录条数
     */
    private int pageSize = 10;

    /**
     * 当页数
     */
    private int pageIndex = 1;

    /**
     * 总页数
     */
    private Integer totalPage;

    /**
     * 总记录数(如果只为null会出发查询记录数SQL)
     */
    private Integer totalRows;

    /**
     * 当前页的开始行指标
     */
    private Integer startRow;

    /**
     * 当前页的结束行指标
     */
    private Integer endRow;

    private List rows;


    /**
     * 根据记录数设置分页数量
     *
     * @param totalRows
     */
    public void setPageInfo(int totalRows) {
        this.setTotalRows(totalRows);
        int pageSize = this.getPageSize() <= 0 ? 50 : this.getPageSize();
        if (totalRows <= 0) {
            this.setTotalPage(0);
        } else {
            if (totalRows % pageSize == 0) {
                this.setTotalPage(totalRows / pageSize);
            } else {
                this.setTotalPage((totalRows / pageSize) + 1);
            }
            if (this.pageIndex < 1) {
                this.pageIndex = 1;
            }

            int startRow = (this.pageIndex - 1) * this.pageSize + 1;
            int endRow = this.pageIndex * this.pageSize;
            if (this.pageIndex == this.totalPage) {
                endRow = this.totalRows;
            }
            this.startRow = startRow;
            this.endRow = endRow;
        }
    }
}
