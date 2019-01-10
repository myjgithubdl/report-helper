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
 * 数据源配置信息表
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
@TableName("rh_datasource")
public class Datasource implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 数据源唯一ID,UUID
     */
    private String uid;

    /**
     * 名称
     */
    private String name;

    /**
     * 驱动类
     */
    private String driverClass;

    /**
     * 数据源连接字符串(JDBC)
     */
    private String jdbcUrl;

    /**
     * 数据源登录用户名(数据库用户名)
     */
    private String user;

    /**
     * 数据源登录密码(数据库用户密码)
     */
    private String password;

    /**
     * 获取报表引擎查询器类名
     */
    private String queryerClass;

    /**
     * 报表引擎查询器使用的数据源连接池类名
     */
    private String poolClass;

    /**
     * 数据源配置选项(JSON格式）
     */
    private String options;

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
