package com.myron.reporthelper.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.myron.reporthelper.entity.SysMenu;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 缪应江
 * @since 2018-12-27
 */
public interface SysMenuMapper extends BaseMapper<SysMenu> {



    int updatePath(@Param("oldPath") String oldPath, @Param("newPath") String newPath);
}
