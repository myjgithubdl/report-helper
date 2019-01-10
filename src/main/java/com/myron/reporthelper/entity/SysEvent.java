package com.myron.reporthelper.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

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
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("rh_sys_event")
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SysEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 日志标识
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 日志来源
     */
    private String source;

    /**
     * 操作用户id
     */
    private Integer userId;

    /**
     * 操作用户账号
     */
    private String account;

    /**
     * 日志级别
     */
    private String level;

    /**
     * 日志信息
     */
    private String message;

    /**
     * url
     */
    private String url;

    /**
     * 记录创建时间
     */
    private Date createDate;


}
