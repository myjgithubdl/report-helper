package com.reporthelper.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.io.Serializable;
import java.util.Date;

/**
 * 报表配置项(_rpt_conf表)持久化类
 *
 * @author Myron
 * @date 2019-03-25
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@TableName("rh_conf")
public class Conf implements Serializable {
    /**
     * 数据源ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;


    /**
     * 父ID
     */
    private Integer pid;
    /**
     * 名称
     */
    private String name;

    /**
     * 配置key
     */
    @TableField("`key`")
    private String key;
    /**
     * 配置值
     */
    private String value;
    /**
     * 显示顺序
     */
    private Integer sequence;
    /**
     * 配置说明
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
     * 是否有子配置项
     */
    @TableField(exist = false)
    private boolean hasChild;
}
