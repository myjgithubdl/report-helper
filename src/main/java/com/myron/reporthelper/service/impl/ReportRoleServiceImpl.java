package com.myron.reporthelper.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.EmptyWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.myron.reporthelper.entity.ReportRole;
import com.myron.reporthelper.entity.SysRole;
import com.myron.reporthelper.entity.User;
import com.myron.reporthelper.mapper.ReportRoleMapper;
import com.myron.reporthelper.service.ReportRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.myron.reporthelper.service.SysRoleService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 缪应江
 * @since 2019-01-05
 */
@Service
public class ReportRoleServiceImpl extends ServiceImpl<ReportRoleMapper, ReportRole> implements ReportRoleService {

    @Autowired
    ReportRoleMapper reportRoleMapper;


    @Override
    public int getReportRoleCount(Map<String, Object> params) {
        return this.reportRoleMapper.getReportRoleCount(params);
    }

    @Override
    public List<Map<String, Object>> getReportRoleList(Map<String, Object> params) {
        return this.reportRoleMapper.getReportRoleList(params);
    }


    @Override
    public List<ReportRole> getReportListByReportRoles(String reportRoles) {
        if(StringUtils.isNotEmpty(reportRoles)){
            QueryWrapper<ReportRole> wrapper=new QueryWrapper<>();
            wrapper.in("id", reportRoles.split(","));
            return this.list(wrapper);
        }

        return null;
    }
}
