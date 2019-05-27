package com.reporthelper.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.reporthelper.entity.SysRole;
import com.reporthelper.entity.User;
import com.reporthelper.util.PageInfo;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  系统角色服务类
 * </p>
 *
 * @author 缪应江
 * @since 2018-12-27
 */
public interface SysRoleService extends IService<SysRole> {
    /**
     * 是否包含有超级管理员权限
     * @param roleIds
     * @return
     */
    boolean isSuperAdminRole(String roleIds);

    /**
     * @param roleIds
     * @return
     */
    String getNames(String roleIds);

    /**
     * @param roleIds
     * @return
     */
    String getSysMenuIds(String roleIds);

    /**
     * @param roleIds
     * @return
     */
    String getPermissionIds(String roleIds);

    /**
     * @param page
     * @param currentUser
     * @param fieldName
     * @param keyword
     * @return
     */
    List<Map<String,Object>> getByPage(PageInfo page, User currentUser, String fieldName, String keyword);


    /**
     * 分页查询
     *
     * @param pageInfo  分页参数
     * @return 分页记录列表
     */
    List<Map<String,Object>> getByPage(PageInfo pageInfo);

    /**
     * @param createUser
     * @return
     */
    String getRoleIdsBy(String createUser);

    /**
     * @param currentUser
     * @return
     */
    List<SysRole> getRolesList(User currentUser);

    /**
     * @param roleId
     * @return
     */
    Map<String, String[]> getRoleSysMenusAndPermissions(Integer roleId);


}
