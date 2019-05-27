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
 * 报表类别表
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
@TableName("rh_report_category")
public class ReportCategory implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 分类ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 父分类
     */
    private Integer pid;

    /**
     * 名称
     */
    private String name;

    /**
     * 树型结构路径从根id到当前id的路径
     */
    private String path;

    /**
     * 是否为子类别1为是，0为否
     */
    private Boolean hasChild;

    /**
     * 状态（1表示启用，0表示禁用，默认为0)
     */
    private Integer status;

    /**
     * 节点在其父节点中的顺序
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
