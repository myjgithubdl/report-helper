package com.reporthelper.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.Date;

import lombok.*;
import lombok.experimental.Accessors;

/**
 * <p>
 * 报表修改历史表
 * </p>
 *
 * @author 缪应江
 * @since 2018-12-27
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("rh_report_history")
public class ReportHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 报表历史记录id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 报表ID
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
     * 报表列集合元数据(JSON格式)
     */
    private String metaColumns;

    /**
     * 查询条件列属性集合(JSON格式)
     */
    private String queryParams;

    /**
     * 报表配置选项(JSON格式)
     */
    private String options;

    /**
     * 报表注释,支持静态或SQL注释(JSON格式)
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


}
