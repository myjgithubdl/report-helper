package com.reporthelper.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * @author Myron
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("rh_report_compose_history")
public class ReportComposeHistory {

    /**
     * 报表ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * ReportHistory表的ID
     */
    private Integer reportHisId;

    /**
     * Report的id
     */
    private Integer reportId;

    /**
     * 报表唯一ID,由接口调用方传入
     */
    private String uid;

    /**
     * 报表分类id
     */
    private Integer categoryId;

    /**
     * 数据源ID
     */
    private Integer dsId;

    /**
     * 报表名称
     */
    private String name;

    /**
     * 报表SQL语句
     */
    private String sqlText;


    /**
     * 报表配置选项(JSON格式)
     *
     * @see com.reporthelper.bo.ReportOptions
     */
    private String options;

    /**
     * 报表列集合元数据(JSON格式)
     *
     * @see com.easydata.head.TheadColumn
     */
    private String metaColumns;


    /**
     * 查询条件列属性集合(JSON格式)
     *
     * @see com.reporthelper.bo.ReportQueryParameter
     */
    private String queryParams;

    /**
     * 报表说明,支持静态或SQL注释(JSON格式)
     * 在静态文档中支持使用${reportExplain}将SQL查询的结果引入
     *
     * @see com.reporthelper.bo.ReportExplain
     */
    private String reportExplain;

    /**
     * 报表状态（1表示锁定，0表示编辑)
     */
    private Integer status;

    /**
     * 排序
     */
    private Integer sequence;

    /**
     * 说明备注
     */
    private String comment;


}
