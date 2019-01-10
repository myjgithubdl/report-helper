package com.myron.reporthelper.controller.member;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.myron.reporthelper.annotation.CurrentUser;
import com.myron.reporthelper.annotation.OpLog;
import com.myron.reporthelper.entity.User;
import com.myron.reporthelper.resp.ResponseResult;
import com.myron.reporthelper.service.PasswordService;
import com.myron.reporthelper.service.UserService;
import com.myron.reporthelper.util.DataGridPager;
import com.myron.reporthelper.util.PageInfo;
import com.myron.reporthelper.util.QueryWrapperOrderUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author 缪应江
 * @since 2018-12-27
 */
@Validated
@RestController
@RequestMapping(value = "/rest/member/user")
public class UserController {

    @Autowired
    UserService userService;

    @Resource
    private PasswordService passwordService;

    @GetMapping(value = "/list")
    @OpLog(name = "分页获取用户列表")
    @RequiresPermissions("membership.user:view")
    public Map<String, Object> list(@CurrentUser final User loginUser, final DataGridPager dataGridPager,
                                    final String keyword) {
        final Map<String, Object> modelMap = new HashMap<>(2);
        Page<User> page = new Page<>(dataGridPager.getPage(), dataGridPager.getRows());

        QueryWrapper queryWrapper = new QueryWrapper();

        if (StringUtils.isNotEmpty(keyword)) {
            queryWrapper.like("id", keyword);
            queryWrapper.or();
            queryWrapper.like("account", keyword);
            queryWrapper.or();
            queryWrapper.like("name", keyword);
        }

        QueryWrapperOrderUtil.setOrderBy(queryWrapper,dataGridPager);

        userService.page(page,queryWrapper);
        modelMap.put("total", page.getTotal());
        modelMap.put("rows", page.getRecords());
        return modelMap;
    }

    @RequestMapping(value = "/add")
    @OpLog(name = "新增用户")
    @RequiresPermissions("membership.user:add")
    public ResponseResult add(@Valid final User user) {
        user.setCreateDate(new Date());
        user.setUpdateDate(new Date());
        this.userService.encryptPassword(user);
        this.userService.save(user);
        return ResponseResult.success("保存成功");
    }

    @RequestMapping(value = "/edit")
    @OpLog(name = "修改用户")
    @RequiresPermissions("membership.user:edit")
    public ResponseResult edit(@Valid final User po) {
        //this.userService.encryptPassword(po);
        //密码不变
        User dbUser = this.userService.getById(po.getId());
        po.setEncryptPassword(dbUser.getEncryptPassword());
        po.setSalt(dbUser.getSalt());
        this.userService.updateById(po);
        return ResponseResult.success("修改成功");
    }

    @RequestMapping(value = "/remove")
    @OpLog(name = "删除用户")
    @RequiresPermissions("membership.user:remove")
    public ResponseResult remove(final Integer id) {
        this.userService.removeById(id);
        return ResponseResult.success("删除成功");
    }

    @RequestMapping(value = "/editPassword")
    @OpLog(name = "修改用户登录密码")
    @RequiresPermissions("membership.user:editPassword")
    public ResponseResult editPassword(final Integer userId, @Length(min = 6) final String password) {
        final User user = this.userService.getById(userId);
        user.setPassword(password);
        this.userService.encryptPassword(user);
        this.userService.updateById(user);
        return ResponseResult.success("修改密码成功");
    }

    @RequestMapping(value = "/changeMyPassword")
    @OpLog(name = "修改个人登录密码")
    @RequiresPermissions("membership.user:changeMyPassword")
    public ResponseResult changeMyPassword(@CurrentUser final User loginUser, final String password,
                                           final String oldPassword) {
        if (!this.passwordService.matches(oldPassword,
                loginUser.getPassword(), loginUser.getCredentialsSalt())) {
            return ResponseResult.error("原账户密码错误！");
        }
        loginUser.setPassword(password);
        this.userService.encryptPassword(loginUser);
        this.userService.updateById(loginUser);
        return ResponseResult.success("修改密码成功");
    }
}
