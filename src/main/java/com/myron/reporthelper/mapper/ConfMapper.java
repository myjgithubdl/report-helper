package com.myron.reporthelper.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.myron.reporthelper.entity.Conf;

import java.util.List;

/**
 * <p>
 * 配置表 Mapper 接口
 * </p>
 *
 * @author Myron Miao
 * @since 2018-12-27
 */
public interface ConfMapper extends BaseMapper<Conf> {

    /**
     * 根据可以查询所有的孩子
     * @param key
     * @return
     */
    List<Conf> selectByParentKey(String key);

}
