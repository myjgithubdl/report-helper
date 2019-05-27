package com.reporthelper.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;

import lombok.*;
import lombok.experimental.Accessors;

/**
 * <p>
 * 报表角色
 * </p>
 *
 * @author Myron
 * @since 2019-01-05
 */
@Data
@Builder
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("rh_report_role")
@AllArgsConstructor
@NoArgsConstructor
public class ReportRole implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 报表角色标识
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 系统角色名称
     */
    private String name;

    /**
     * 报表id集合(rh_report表的id以英文逗号分隔)
     */
    private String reportIds;

    /**
     * 系统角色的状态,1表示启用,0表示禁用,默认为1,其他保留
     */
    private Integer status;

    /**
     * 系统角色的排序顺序
     */
    private Integer sequence;

    /**
     * 系统角色备注
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
