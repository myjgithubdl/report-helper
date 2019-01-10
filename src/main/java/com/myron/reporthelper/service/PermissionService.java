package com.myron.reporthelper.service;

import com.myron.reporthelper.entity.Permission;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 缪应江
 * @since 2018-12-27
 */
public interface PermissionService extends IService<Permission> {

    void reloadCache();



    /**
     * @return
     */
    Map<String, String> getIdCodeMap();

    String getPermissionIds(String[] codes);

    String getSysMenuIds(String permissionIds);
}
