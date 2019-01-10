package com.myron.reporthelper.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.myron.reporthelper.entity.SysRole;

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
public interface SysRoleMapper extends BaseMapper<SysRole> {


    List<Map<String,Object>> selectByPager(Page page , Map<String,Object> params);

}
