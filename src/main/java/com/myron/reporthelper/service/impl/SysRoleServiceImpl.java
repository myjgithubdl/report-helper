package com.myron.reporthelper.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.EmptyWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.myron.reporthelper.entity.SysRole;
import com.myron.reporthelper.entity.User;
import com.myron.reporthelper.mapper.SysRoleMapper;
import com.myron.reporthelper.service.SysRoleService;
import com.myron.reporthelper.util.PageInfo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 缪应江
 * @since 2018-12-27
 */
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    @Autowired
    SysRoleMapper sysRoleMapper;


    @Override
    public List<Map<String, Object>> getByPage(final PageInfo pageInfo, final User currentUser, final String fieldName,
                                               final String keyword) {


        if (this.isSuperAdminRole(currentUser.getSysRoles())) {
            return this.getByPage(pageInfo);
        }

        Page<Map> plusPage = new Page<Map>(pageInfo.getNowpage(), pageInfo.getSize());
        final EmptyWrapper<SysRole> example = new EmptyWrapper<SysRole>();
        example.or().eq("create_user", currentUser.getId())
                .like(fieldName, keyword);
        return this.sysRoleMapper.selectByPager(plusPage, pageInfo.getCondition());
    }


    @Override
    public List<Map<String, Object>> getByPage(final PageInfo pageInfo) {
        Page<Map> plusPage = new Page<Map>(pageInfo.getNowpage(), pageInfo.getSize());
        return this.sysRoleMapper.selectByPager(plusPage, pageInfo.getCondition());
    }


    @Override
    public boolean isSuperAdminRole(final String roleIds) {
        final List<SysRole> list = this.getIn(roleIds);
        if (CollectionUtils.isEmpty(list)) {
            return false;
        }
        return list.stream().anyMatch(x -> "superAdmin".equals(x.getCode()));
    }

    @Override
    public String getNames(final String roleIds) {
        final List<SysRole> list = this.getIn(roleIds);
        if (CollectionUtils.isEmpty(list)) {
            return StringUtils.EMPTY;
        }
        final List<String> namesList = new ArrayList<>(list.size());
        namesList.addAll(list.stream().map(SysRole::getName).collect(Collectors.toList()));
        return StringUtils.join(namesList, ',');
    }

    @Override
    public String getSysMenuIds(final String roleIds) {
        return this.getIds(roleIds, SysRole::getMenuIds);
    }

    @Override
    public String getPermissionIds(final String roleIds) {
        return this.getIds(roleIds, SysRole::getPermissionIds);
    }

    @Override
    public String getRoleIdsBy(final String createUser) {
        final List<SysRole> list = this.getByCreateUser(createUser);
        if (CollectionUtils.isEmpty(list)) {
            return StringUtils.EMPTY;
        }
        final List<Integer> roleIdList = new ArrayList<>(list.size());
        roleIdList.addAll(list.stream().map(SysRole::getId).collect(Collectors.toList()));
        return StringUtils.join(roleIdList, ',');
    }

    @Override
    public List<SysRole> getRolesList(final User logingUser) {
        if (this.isSuperAdminRole(logingUser.getSysRoles())) {
            return this.list();
        }
        return this.getByCreateUser(logingUser.getAccount());
    }

    @Override
    public Map<String, String[]> getRoleSysMenusAndPermissions(final Integer roleId) {
        final SysRole role = this.getById(roleId);
        if (role == null) {
            return null;
        }

        final Map<String, String[]> map = new HashMap<>(2);
        final String distinctMenus = this.distinct(StringUtils.split(role.getMenuIds(), ','));
        final String distinctPerms = this.distinct(StringUtils.split(role.getPermissionIds(), ','));
        map.put("modules", StringUtils.split(distinctMenus, ','));
        map.put("permissions", StringUtils.split(distinctPerms, ','));
        return map;
    }

    private String getIds(final String roleIds, final Function<? super SysRole, ? extends String> mapper) {
        final List<SysRole> list = this.getIn(roleIds);
        if (CollectionUtils.isEmpty(list)) {
            return StringUtils.EMPTY;
        }
        final List<String> idList = new ArrayList<>(list.size());
        idList.addAll(list.stream().map(mapper).collect(Collectors.toList()));
        final String joinIds = StringUtils.join(idList, ',');
        final String[] ids = StringUtils.split(joinIds, ',');
        return this.distinct(ids);
    }

    private String distinct(final String[] ids) {
        if (ArrayUtils.isEmpty(ids)) {
            return StringUtils.EMPTY;
        }
        final Set<String> idSet = new HashSet<>(ids.length);
        Collections.addAll(idSet, ids);
        return StringUtils.join(idSet, ',');
    }

    private List<SysRole> getIn(final String roleIds) {
        if (StringUtils.isBlank(roleIds)) {
            return new ArrayList<>(0);
        }
        final String[] ids = StringUtils.split(roleIds, ',');
        final List<SysRole> list = new ArrayList<>(10);
        for (final String id : ids) {
            list.add(SysRole.builder().id(Integer.valueOf(id)).build());
        }

        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.in("id", ids);
        return this.list(queryWrapper);
    }

    private List<SysRole> getByCreateUser(final String createUser) {
        final EmptyWrapper emptyWrapper = new EmptyWrapper();
        SysRole role = SysRole.builder().createUser(Integer.parseInt(createUser)).build();
        emptyWrapper.setEntity(role);
        return this.list(emptyWrapper);
    }


}
