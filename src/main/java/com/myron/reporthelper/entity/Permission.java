package com.myron.reporthelper.entity;

import com.baomidou.mybatisplus.annotation.TableField;
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
 * 
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
@TableName("rh_permission")
public class Permission implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 系统操作标识
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 系统菜单标识
     */
    private Integer menuId;

    /**
     * 系统操作名称
     */
    private String name;

    /**
     * 系统操作唯一代号
     */
    private String code;

    /**
     * 系统操作的排序顺序
     */
    private Integer sequence;

    /**
     * 系统操作备注
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
     * 系统操作所属模块树路径
     */
    @TableField(exist = false)
    private String path;


}
