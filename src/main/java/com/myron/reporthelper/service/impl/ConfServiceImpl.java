package com.myron.reporthelper.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.myron.reporthelper.entity.Conf;
import com.myron.reporthelper.mapper.ConfMapper;
import com.myron.reporthelper.service.ConfService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Slf4j
@Service
public class ConfServiceImpl extends ServiceImpl<ConfMapper, Conf> implements ConfService {

    @Autowired
    ConfMapper confMapper;

    @Override
    public List<Map<String, Object>> getReportList(Map<String, Object> params) {
        return this.confMapper.getReportList(params);
    }

    @Override
    public int getReportCount(Map<String, Object> params) {
        return this.confMapper.getReportCount(params);
    }


    @Override
    public List<Conf> getByParentId(final Integer parentId) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("pid", parentId);
        queryWrapper.select(("id,pid,name,`key`,value,sequence,comment,create_user,create_date,update_user,update_date").replaceAll(" ","").split(","));
        return this.list(queryWrapper);
    }

    @Override
    public List<Conf> selectByParentKey(String key) {
        return confMapper.selectByParentKey(key);
    }


}
