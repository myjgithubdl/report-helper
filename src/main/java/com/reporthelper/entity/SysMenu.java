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
 * 
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
@TableName("rh_sys_menu")
public class SysMenu implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 系统模块标识
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 系统模块父标识
     */
    private Integer pid=0;

    /**
     * 系统模块父标识
     */
    private String name;

    /**
     * 系统模块代号
     */
    private String code;

    /**
     * 系统模块显示图标
     */
    private String icon;

    /**
     * 系统模块对应的页面地址
     */
    private String url;

    /**
     * 从根模块到当前子模块的id路径，id之间用逗号分隔
     */
    private String path;

    /**
     * 是否存在子模块,0否,1 是
     */
    private Integer hasChild;

    /**
     * URL链接类型(0表示系统内部，1表示外部链接，默认 0)
     */
    private Integer linkType;

    /**
     * 菜单打开方式，目前有none：无，iframe：tab嵌入内容，blank：在浏览器新窗口中打开
     * URL链接的target(_blank,_top等)
     */
    private String target;




    /**
     * URL链接参数
     */
    private String params;

    /**
     * 系统模块在当前父模块下的排序顺序
     */
    private Integer sequence;

    /**
     * 系统模块的状态,1表示启用,0表示禁用,默认为1,其他保留
     */
    private Integer status;

    /**
     * 系统模块备注
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
