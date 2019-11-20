package com.reporthelper.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

/**
 *
 */
@TableName("rh_report_compose")
public class ReportCompose extends Report implements Serializable {

    private static final long serialVersionUID = 9434933287321L;

    /**
     * Report的id
     */
    private Integer reportId;

    @TableField(exist = false)
    private Integer categoryId;

    @TableField(exist = false)
    private Integer paramShare;

    @TableField(exist = false)
    private Integer createUser;

    @TableField(exist = false)
    private Date createDate;

    @TableField(exist = false)
    private Integer updateUser;

    /**
     * 记录修改时间
     */
    @TableField(exist = false)
    private Date updateDate;

    public Integer getReportId() {
        return reportId;
    }

    public void setReportId(Integer reportId) {
        this.reportId = reportId;
    }
}
