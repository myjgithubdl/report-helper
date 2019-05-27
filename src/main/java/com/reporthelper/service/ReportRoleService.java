package com.reporthelper.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.reporthelper.entity.ReportRole;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author Myron
 * @since 2019-01-05
 */
public interface ReportRoleService extends IService<ReportRole> {


    /**
     * 查询列表
     *
     * @param params
     * @return
     */
    int getReportRoleCount(Map<String, Object> params);


    /**
     * 查询列表
     *
     * @param params
     * @return
     */
    List<Map<String, Object>> getReportRoleList(Map<String, Object> params);


    /**
     * 根据报表的角色id值获取报表角色
     *
     * @param reportRoles
     * @return
     */
    List<ReportRole> getReportListByReportRoles(String reportRoles);
}
