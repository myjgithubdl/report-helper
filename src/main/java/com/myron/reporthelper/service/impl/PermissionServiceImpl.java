package com.myron.reporthelper.service.impl;

import com.myron.reporthelper.entity.Permission;
import com.myron.reporthelper.mapper.PermissionMapper;
import com.myron.reporthelper.service.PermissionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 缪应江
 * @since 2018-12-27
 */
@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements PermissionService {

    @Autowired
    PermissionMapper permissionMapper;

    private static final byte[] lock = new byte[0];
    private static Map<String, Permission> cache;

    public PermissionServiceImpl() {
    }

    @PostConstruct
    private void loadCache() {
        synchronized (lock) {
            if (MapUtils.isEmpty(cache)) {
                final List<Permission> list = this.permissionMapper.selectAllWithMenuPath();
                cache = new HashMap<>(list.size());
                for (final Permission permission : list) {
                    cache.put(permission.getCode(), permission);
                }
            }
        }
    }


    @Override
    public void reloadCache() {
        if (cache != null) {
            cache.clear();
        }
        this.loadCache();
    }


    @Override
    public Map<String, String> getIdCodeMap() {
        final Map<String, String> idCodeMap = new HashMap<>(cache.size());
        for (final Map.Entry<String, Permission> es : cache.entrySet()) {
            idCodeMap.put(es.getValue().getId().toString(), es.getValue().getCode());
        }
        return idCodeMap;
    }


    @Override
    public String getPermissionIds(final String[] codes) {
        if (ArrayUtils.isEmpty(codes)) {
            return StringUtils.EMPTY;
        }

        final List<String> permIds = new ArrayList<>(codes.length);
        for (final String code : codes) {
            final String key = code.trim().toLowerCase();
            if (cache.containsKey(key)) {
                permIds.add(String.valueOf(cache.get(key).getId()));
            }
        }

        return StringUtils.join(permIds, ',');
    }

    @Override
    public String getSysMenuIds(String permissionIds) {
        // 如果设置为所有权限
        if (permissionIds.contains("all")) {
            return "all";
        }

        final Map<String, Permission> idMap = new HashMap<>(cache.size());
        for (final Permission permission : cache.values()) {
            idMap.put(permission.getId().toString(), permission);
        }

        final String[] idList = StringUtils.split(permissionIds, ',');
        final List<String> modulePathList = new ArrayList<>();
        for (final String id : idList) {
            final Permission permission = idMap.get(id);
            if (permission != null) {
                modulePathList.add(permission.getPath());
            }
        }

        final String moduleIds = StringUtils.join(modulePathList, ',');
        return this.distinct(StringUtils.split(moduleIds, ','));
    }


    private String distinct(final String[] ids) {
        if (ArrayUtils.isEmpty(ids)) {
            return StringUtils.EMPTY;
        }
        final Set<String> idSet = new HashSet<>(ids.length);
        Collections.addAll(idSet, ids);
        return StringUtils.join(idSet, ',');
    }

}
