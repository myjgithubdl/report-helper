package com.myron.reporthelper.entity;

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
 * 系统角色
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
@TableName("rh_sys_role")
public class SysRole implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 系统角色标识
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 系统角色所拥有的菜单集合(rh_menu表的id以英文逗号分隔)
     */
    private String menuIds;

    /**
     * 系统角色所拥有的操作集合(rh_permission表的id以英文逗号分隔)
     */
    private String permissionIds;

    /**
     * 系统角色名称
     */
    private String name;

    /**
     * 系统角色英语名
     */
    private String code;

    /**
     * 是否为系统角色,1表示是，0表示否,默认为0
     */
    private Integer isSystem;

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
