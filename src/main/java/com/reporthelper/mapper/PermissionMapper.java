package com.reporthelper.mapper;

import com.reporthelper.entity.Permission;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 缪应江
 * @since 2018-12-27
 */
public interface PermissionMapper extends BaseMapper<Permission> {

    /**
     * 查询列表
     *
     * @param params
     * @return
     */
    List<Map<String, Object>> getReportList(Map<String, Object> params);


    /**
     * 查询数量
     * @param params
     * @return
     */
    int getReportCount(Map<String, Object> params);

    List<Permission> selectAllWithMenuPath();

}
