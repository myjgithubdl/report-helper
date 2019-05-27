package com.reporthelper.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.EmptyWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.reporthelper.bo.EasyUITreeNode;
import com.reporthelper.entity.SysMenu;
import com.reporthelper.mapper.SysMenuMapper;
import com.reporthelper.service.SysMenuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author Myron
 * @since 2018-12-27
 */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    @Autowired
    SysMenuMapper menuMapper;

    @Override
    public List<EasyUITreeNode<SysMenu>> getSysMenuTree(final List<SysMenu> modules, final Predicate<SysMenu> predicate) {
        final List<EasyUITreeNode<SysMenu>> roots = new ArrayList<>();
        modules.stream()
                .filter(predicate)
                .filter(module -> Objects.equals(module.getPid(), 0))
                .sorted((x, y) -> x.getSequence() > y.getSequence() ? 1 : -1)
                .forEach((SysMenu module) -> this.addSysMenuTreeNode(roots, modules, module, predicate));
        return roots;
    }


    private void addSysMenuTreeNode(final List<EasyUITreeNode<SysMenu>> children, final List<SysMenu> modules,
                                 final SysMenu module,
                                 final Predicate<SysMenu> predicate) {
        final String cateId = Integer.toString(module.getId());
        final String pid = Integer.toString(module.getPid());
        final String text = module.getName();
        final String state = module.getHasChild() > 0 ? "closed" : "open";
        final EasyUITreeNode<SysMenu> parentNode = new EasyUITreeNode<>(cateId, pid, text, state, module.getIcon(),
                false, module);
        this.addChildSysMenuTreeNodes(modules, parentNode, predicate);
        children.add(parentNode);
    }

    private void addChildSysMenuTreeNodes(final List<SysMenu> modules, final EasyUITreeNode<SysMenu> parentNode,
                                       final Predicate<SysMenu> predicate) {
        final Integer id = Integer.valueOf(parentNode.getId());
        modules.stream()
                .filter(predicate)
                .filter(module -> Objects.equals(module.getPid(), id))
                .sorted((x, y) -> x.getSequence() > y.getSequence() ? 1 : -1)
                .forEach(module -> this.addSysMenuTreeNode(parentNode.getChildren(), modules, module, predicate));
    }


    @Override
    @Transactional
    public boolean remove(final int id, final int pid) {
        this.removeById(id);
        final boolean hasChild = this.hasChildren(pid);
        return this.updateHasChild(pid, hasChild) > 0;
    }

    @Override
    public boolean hasChildren(final int id) {
        QueryWrapper example = new QueryWrapper();
        example.eq("pid", id);
        return this.count(example) > 0;
    }


    private int updateHasChild(final int id, final boolean hasChild) {
        final SysMenu menu = this.getById(id);
        if(menu != null ){
            menu.setHasChild(hasChild ? 1 : 0);
            return this.updateById(menu) == true ? 1 : 0;
        }
        return 0;
    }


    @Override
    public List<SysMenu> getChildren(final int id) {
        QueryWrapper example = new QueryWrapper();
        example.eq("pid", id);
        return this.list(example);
    }

    private int updateParentId(final int sourceId, final int targetId) {
        final SysMenu module = SysMenu.builder().id(sourceId).pid(targetId).build();
        return this.updateById(module) == true ? 1 : 0;
    }

    @Override
    @Transactional
    public void move(final int sourceId, final int targetId, final int sourcePid, final String sourcePath) {
        // 修改source节点的pid与path，hasChild值
        this.updateParentId(sourceId, targetId);
        this.updateHasChild(targetId, true);
        this.menuMapper.updatePath(sourcePath, this.getPath(targetId, sourceId));
        // 修改source节点的父节点hasChild值
        this.updateHasChild(sourcePid, this.hasChildren(sourcePid));
    }


    private String getPath(final int pid, final int id) {
        if (pid <= 0) {
            return "" + id;
        }
        final SysMenu po = this.getById(pid);
        return po.getPath() + "," + id;
    }


    @Override
    public void rebuildAllPath() {
        final List<SysMenu> modules = this.list();
        final List<SysMenu> roots = modules.stream()
                .filter(module -> module.getId().equals(0))
                .collect(Collectors.toList());
        for (final SysMenu root : roots) {
            root.setPath(root.getId().toString());
            this.rebuildPath(modules, root);
        }

        this.updateBatchById(roots);
    }


    private void rebuildPath(final List<SysMenu> modules, final SysMenu parent) {
        final List<SysMenu> children = modules.stream()
                .filter(module -> module.getPid().equals(parent.getId()))
                .collect(Collectors.toList());
        final int hasChild = CollectionUtils.isEmpty(children) ? 0 : 1;
        parent.setHasChild(hasChild);
        for (final SysMenu child : children) {
            final String path = parent.getPath() + "," + child.getId().toString();
            child.setPath(path);
            this.rebuildPath(modules, child);
        }
    }

}
