package com.reporthelper.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 报表的访问记录
 * @author Myron
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("rh_report_use_record")
public class ReportUseRecord implements Serializable {

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 用户ID
     * @see User id
     */
    private Integer userId;

    /**
     * 报表ID
     * @see Report id
     */
    private Integer reportId;

    /**
     * @see ReportCompose uid
     */
    private String composeUid;

    /**
     * 请求参数
     */
    private String parameter;

    /**
     * 访问时间时间
     */
    private Date createDate;


}
