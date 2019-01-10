package com.myron.reporthelper.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 系统用户表
 * </p>
 *
 * @author 缪应江
 * @since 2018-12-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Builder
@TableName("rh_user")
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 系统用户标识
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 系统用户账号
     */
    private String account;

    /**
     * 原始密码
     */


    @TableField(exist = false)
    private String password;


    /**
     * 加密密码
     */
    @TableField("password")
    private String encryptPassword;

    /**
     * 加盐
     */
    private String salt;

    /**
     * 系统用户姓名
     */
    private String name;

    /**
     * 系统用户电子邮箱
     */
    private String email;

    /**
     * 系统用户用户电话号码,多个用英文逗号分开
     */
    private String mobile;

    /**
     * 系统用户的状态,1表示启用,0表示禁用,默认为1,其他保留
     */
    private Integer status;

    /**
     * 系统用户备注
     */
    private String comment;

    private Integer createUser;

    private Date createDate;

    private Integer updateUser;

    private Date updateDate;


    /**
     * 系统用户所属角色集合(role_id以英文逗号分隔)
     */
    private String roles;

    /**
     * 获取系统用户密码的凭证盐(account+salt)
     *
     * @return account+salt
     */
    public String getCredentialsSalt() {
        return this.account + this.salt;
    }


}
