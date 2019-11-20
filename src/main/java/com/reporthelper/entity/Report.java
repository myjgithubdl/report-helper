package com.reporthelper.entity;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.easydata.head.TheadColumn;
import com.easydata.head.TheadColumnTree;
import com.reporthelper.bo.CompositionReport;
import com.reporthelper.bo.ReportExplain;
import com.reporthelper.bo.ReportOptions;
import com.reporthelper.bo.ReportQueryParameter;
import lombok.*;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 报表配置表
 * </p>
 *
 * @author Myron
 * @since 2018-12-27
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("rh_report")
public class Report implements Serializable {

    private static final long serialVersionUID = 943493487321L;

    /**
     * 报表ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

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
     * 是否公用查询参数
     * 1：是、0：否
     */
    private Integer paramShare;

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

    /**
     * 记录创建用户ID
     */
    private Integer createUser;

    /**
     * 记录创建时间
     */
    private Date createDate;

    /**
     * 记录修改用户ID
     */
    private Integer updateUser;

    /**
     * 记录修改时间
     */
    private Date updateDate;


    /**
     * 该报表的的组成部分
     */
    @TableField(exist = false)
    private List<ReportCompose> reportComposeList;


    /**
     * 报表分类名称
     */
    @TableField(exist = false)
    private String categoryName;

    /**
     * 报表数据源名称
     */
    @TableField(exist = false)
    private String dsName;

    /**
     * 解析json格式的报表查询参数为QueryParameter对象集合
     *
     * @return
     */
    public List<ReportQueryParameter> parseQueryParams() {
        if (StringUtils.isBlank(this.queryParams)) {
            return new ArrayList<>(0);
        }
        return JSON.parseArray(this.queryParams, ReportQueryParameter.class);
    }

    /**
     * 解析json格式的报表选项ReportOptions
     *
     * @return
     */
    public ReportOptions parseOptions() {
        if (StringUtils.isBlank(this.options)) {
            return null;
        }
        return JSON.parseObject(this.options, ReportOptions.class);
    }


    /**
     * 解析json格式的报表元数据列为TheadColumn对象集合
     *
     * @return
     */
    public List<TheadColumn> parseMetaColumns() {
        if (StringUtils.isBlank(this.metaColumns)) {
            return new ArrayList<>(0);
        }
        return JSON.parseArray(this.metaColumns, TheadColumn.class);
    }

    public List<TheadColumnTree> parseMetaColumnTrees() {
        if (StringUtils.isBlank(this.metaColumns)) {
            return new ArrayList<>(0);
        }
        return JSON.parseArray(this.metaColumns, TheadColumnTree.class);
    }


    /**
     * 解析报表的说明内容
     *
     * @return
     */
    public ReportExplain parseReportExplain() {
        return JSON.parseObject(this.reportExplain, ReportExplain.class);
    }

    /**
     * 解析json格式的报表元数据列为 CompositionReport  对象集合
     *
     * @return
     */
    public List<CompositionReport> parseCompositionReport() {
        if (StringUtils.isBlank(this.metaColumns)) {
            return new ArrayList<>(0);
        }
        return JSON.parseArray(this.metaColumns, CompositionReport.class);
    }

}
