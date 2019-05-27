package com.reporthelper.controller.member;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reporthelper.annotation.OpLog;
import com.reporthelper.entity.SysEvent;
import com.reporthelper.resp.ResponseResult;
import com.reporthelper.service.SysEventService;
import com.reporthelper.util.DataGridPager;
import com.reporthelper.util.QueryWrapperOrderUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Myron
 * @date 2019-03-25
 */
@RestController
@RequestMapping(value = "/rest/member/event")
public class SysEventController {

    @Autowired
    SysEventService service;

    @GetMapping(value = "/list")
    @OpLog(name = "分页获取系统日志列表")
    @RequiresPermissions("membership.event:view")
    public Map<String, Object> list(final DataGridPager dataGridPager, final String keyword) {
        final Map<String, Object> modelMap = new HashMap<>(2);
        Page<SysEvent> page = new Page<>(dataGridPager.getPage(), dataGridPager.getRows());

        QueryWrapper queryWrapper = new QueryWrapper();

        if(StringUtils.isNoneEmpty(keyword)){
            queryWrapper.like("source",keyword);
            queryWrapper.or();
            queryWrapper.like("user_id",keyword);
            queryWrapper.or();
            queryWrapper.like("account",keyword);
            queryWrapper.or();
            queryWrapper.like("level",keyword);
            queryWrapper.or();
            queryWrapper.like("message",keyword);
            queryWrapper.or();
            queryWrapper.like("url",keyword);
        }

        QueryWrapperOrderUtil.setOrderBy(queryWrapper,dataGridPager);


        service.page(page, queryWrapper);
        modelMap.put("total", page.getTotal());
        modelMap.put("rows", page.getRecords());
        return modelMap;
    }

    @PostMapping(value = "/remove")
    @OpLog(name = "删除系统日志")
    @RequiresPermissions("membership.event:remove")
    public ResponseResult remove(final Integer id) {
        this.service.removeById(id);
        return ResponseResult.success("删除成功");
    }

    @GetMapping(value = "/clear")
    @OpLog(name = "清除系统日志")
    @RequiresPermissions("membership.event:clear")
    public ResponseResult clear() {
        this.service.remove(null);
        return ResponseResult.success("清楚成功");
    }
}