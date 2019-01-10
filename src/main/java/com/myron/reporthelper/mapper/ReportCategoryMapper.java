package com.myron.reporthelper.mapper;

import com.myron.reporthelper.entity.ReportCategory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 报表类别表 Mapper 接口
 * </p>
 *
 * @author 缪应江
 * @since 2018-12-27
 */
public interface ReportCategoryMapper extends BaseMapper<ReportCategory> {
    /**
     * @param oldPath
     * @param newPath
     * @return
     */
    int updatePath(@Param("oldPath") String oldPath, @Param("newPath") String newPath);
}
