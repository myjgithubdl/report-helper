package com.myron.reporthelper.mapper;

import com.myron.reporthelper.entity.Permission;
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


    List<Permission> selectAllWithMenuPath();

}
