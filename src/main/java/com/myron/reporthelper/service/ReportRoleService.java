package com.myron.reporthelper.service;

import com.myron.reporthelper.entity.ReportRole;
import com.baomidou.mybatisplus.extension.service.IService;
import com.myron.reporthelper.entity.SysRole;
import com.myron.reporthelper.entity.User;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author 缪应江
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
