package com.reporthelper.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.EmptyWrapper;
import com.reporthelper.entity.ReportCategory;
import com.reporthelper.mapper.ReportCategoryMapper;
import com.reporthelper.service.ReportCategoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reporthelper.service.ReportCategoryService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 报表类别表 服务实现类
 * </p>
 *
 * @author 缪应江
 * @since 2018-12-27
 */
@Service
public class ReportCategoryServiceImpl extends ServiceImpl<ReportCategoryMapper, ReportCategory> implements ReportCategoryService {

    @Autowired
    ReportCategoryMapper reportCategoryMapper;

    @Override
    @Transactional
    public boolean remove(final int id, final int pid) {
        this.removeById(id);
        return this.updateHasChild(pid, this.hasChildren(pid));
    }


    private boolean updateHasChild(final int id, final boolean hasChild) {
        final ReportCategory category = ReportCategory.builder().id(id).hasChild(hasChild ? true : false).build();
        return this.updateById(category);
    }


    @Override
    public boolean hasChildren(final int id) {
        ReportCategory reportCategory = new ReportCategory();
        reportCategory.setPid(id);

        EmptyWrapper example = new EmptyWrapper();
        example.setEntity(reportCategory);

        return this.count(example) > 0;
    }


    @Override
    @Transactional
    public ReportCategory paste(final int sourceId, final int targetId) {
        final ReportCategory category = this.getById(sourceId);
        final int count = this.count(targetId, category.getName());
        if (count > 0) {
            category.setName(String.format("%s_复件%s", category.getName(), count));
        }
        category.setPid(targetId);
        this.save(category);
        this.updateHasChild(targetId, true);
        return category;
    }


    private int count(final int parentId, final String name) {
        final ReportCategory category = new ReportCategory();
        category.setPid(parentId);
        category.setName(name);

        final EmptyWrapper example = new EmptyWrapper();
        example.setEntity(category);
        return this.count(example);
    }


    @Override
    @Transactional
    public void move(final int sourceId, final int targetId, final int sourcePid, final String sourcePath) {
        // 修改source节点的pid与path，hasChild值
        this.updateParentId(sourceId, targetId);
        this.updateHasChild(targetId, true);
        this.reportCategoryMapper.updatePath(sourcePath, this.getPath(targetId, sourceId));
        // 修改source节点的父节点hasChild值
        this.updateHasChild(sourcePid, this.hasChildren(sourcePid));
    }


    private boolean updateParentId(final int sourceId, final int targetId) {
        final ReportCategory category = ReportCategory.builder().id(sourceId).pid(targetId).build();
        return this.updateById(category);
    }


    private String getPath(final int pid, final int id) {
        if (pid <= 0) {
            return "" + id;
        }
        final ReportCategory po = this.getById(pid);
        return po.getPath() + "," + id;
    }


    @Override
    public void rebuildAllPath() {
        final List<ReportCategory> categories = this.list();
        final List<ReportCategory> roots = categories.stream()
                .filter(cate -> cate.getPid().equals(0))
                .collect(Collectors.toList());
        for (final ReportCategory root : roots) {
            root.setPath(root.getId().toString());
            this.rebuildPath(categories, root);
        }
        this.updateBatchById(categories);
    }


    private void rebuildPath(final List<ReportCategory> entities, final ReportCategory parent) {
        final List<ReportCategory> children = entities.stream()
                .filter(cate -> cate.getPid().equals(parent.getId()))
                .collect(Collectors.toList());
        final boolean hasChild = CollectionUtils.isEmpty(children) ? false : true;
        parent.setHasChild(hasChild);
        for (final ReportCategory child : children) {
            final String path = parent.getPath() + "," + child.getId().toString();
            child.setPath(path);
            this.rebuildPath(entities, child);
        }
    }

}
