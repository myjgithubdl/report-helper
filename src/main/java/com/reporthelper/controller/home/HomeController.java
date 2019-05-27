package com.reporthelper.controller.home;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.reporthelper.annotation.CurrentUser;
import com.reporthelper.bo.EasyUITreeNode;
import com.reporthelper.entity.*;
import com.reporthelper.resp.ResponseResult;
import com.reporthelper.service.ReportCategoryService;
import com.reporthelper.service.ReportRoleService;
import com.reporthelper.service.ReportService;
import com.reporthelper.service.impl.MembershipFacadeServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 */
@Controller
@RequestMapping(value = {"", "/", "/home"})
public class HomeController {
    @Resource
    private MembershipFacadeServiceImpl membershipFacade;

    @Autowired
    ReportService reportService;

    @Autowired
    ReportRoleService reportRoleService;

    @Autowired
    ReportCategoryService reportCategoryService;

    @GetMapping(value = {"", "/", "/index"})
    public String index(@CurrentUser final User loginUser, final Model model) {
        model.addAttribute("roleNames", this.membershipFacade.getRoleNames(loginUser.getSysRoles()));
        model.addAttribute("user", loginUser);
        return "home/index";
    }

    @ResponseBody
    @GetMapping(value = "/getMenus")
    public List<EasyUITreeNode<SysMenu>> getMenus(@CurrentUser final User loginUser) {
        final List<SysMenu> menus = this.membershipFacade.getSysMenus(loginUser.getSysRoles());
        return this.membershipFacade.getSysMenuTree(menus, x -> x.getStatus() == 1);
    }


    /**
     * 查询用户的系统菜单
     *
     * @param loginUser
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getSysMenus")
    public ResponseResult getSysMenus(@CurrentUser final User loginUser) {
        List<SysMenu> menus = this.membershipFacade.getSysMenus(loginUser.getSysRoles());
        if (menus != null) {
            menus = menus.stream().filter(x -> x.getStatus() == 1).collect(Collectors.toList());
        }

        List<UserMenu> returnMenus = new ArrayList<>();
        menus.stream().forEach(m -> {
            UserMenu userMenu = UserMenu.builder().id(m.getId() + "")
                    .name(m.getName()).pid(m.getPid() == null ? "0" : m.getPid().toString())
                    .icon(m.getIcon()).sequence(m.getSequence()).url(m.getUrl()).target(m.getTarget()).build();

            returnMenus.add(userMenu);
        });
        UserMenu.resetMenuLevel(returnMenus);
        return ResponseResult.success(returnMenus);
    }


    /**
     * 查询用户的系统菜单、报表分类、报表
     *
     * @param loginUser
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getSysMenusAndReport")
    public ResponseResult getSysMenusAndReport(@CurrentUser final User loginUser) {
        List<UserMenu> returnMenus = new ArrayList<>();

        //系统菜单
        List<SysMenu> menus = this.membershipFacade.getSysMenus(loginUser.getSysRoles());
        if (menus != null) {
            menus = menus.stream().filter(x -> x.getStatus() == 1).collect(Collectors.toList());


            menus.stream().forEach(m -> {
                UserMenu userMenu = UserMenu.builder().id(m.getId() + "")
                        .name(m.getName()).pid(m.getPid() == null ? "0" : m.getPid().toString())
                        .icon(m.getIcon()).sequence(m.getSequence()).url(m.getUrl()).target(m.getTarget()).build();

                returnMenus.add(userMenu);
            });
        }


        //查询用户报表
        List<ReportRole> reportRoles = reportRoleService.getReportListByReportRoles(loginUser.getReportRoles());
        if (reportRoles != null) {
            List<String> reportIds = new ArrayList<>();
            reportRoles.stream().forEach(reportRole -> {
                if (StringUtils.isNotBlank(reportRole.getReportIds())) {
                    reportIds.addAll(Arrays.asList(reportRole.getReportIds().split(",")));
                }
            });
            if (reportIds.size() > 0) {
                QueryWrapper wrapperReport = new QueryWrapper();
                wrapperReport.in("id", reportIds);
                List<Report> userReportList = reportService.list(wrapperReport);

                List<Integer> categoryIds = userReportList.stream().map(Report::getCategoryId).distinct().collect(Collectors.toList());

                //所有的分类
                QueryWrapper wrapperCategory = new QueryWrapper();

                List<ReportCategory> reportCategoryList = reportCategoryService.list(wrapperCategory);

                //用户的报表对应的分类
                List<ReportCategory> userCategoryList = getCategoryList(reportCategoryList, categoryIds);

                userCategoryList.stream().forEach(c -> {
                    UserMenu userMenu = UserMenu.builder().id("category-" + c.getId() + "")
                            .name(c.getName()).pid(c.getPid() == null || c.getPid() == 0 ? "0" : "category-" + c.getPid().toString())
                            .sequence(c.getSequence()).url("").target("").build();

                    returnMenus.add(userMenu);
                });

                userReportList.stream().forEach(r -> {
                    UserMenu userMenu = UserMenu.builder().id("report-" + r.getId() + "")
                            .name(r.getName()).pid("category-" + r.getCategoryId())
                            .sequence(r.getSequence()).url("report/preview/uid/" + r.getUid()).target("iframe").build();

                    returnMenus.add(userMenu);
                });
            }
        }
        UserMenu.resetMenuLevel(returnMenus);
        UserMenu.resortMenu(returnMenus);
        return ResponseResult.success(returnMenus);
    }

    /**
     * 根据给定的所有分类和分类查询出所有的上级分类
     *
     * @param allCategoryList
     * @param categoryIds
     * @return
     */
    private List<ReportCategory> getCategoryList(List<ReportCategory> allCategoryList, List<Integer> categoryIds) {
        if (allCategoryList == null || allCategoryList.size() < 1 || categoryIds == null || categoryIds.size() < 1) {
            return null;
        }
        Map<String, ReportCategory> allCategoryMap = new HashMap<>();
        allCategoryList.stream().forEach(c -> allCategoryMap.put(c.getId().toString(), c));

        Map<String, ReportCategory> categoryMap = new HashMap<>();
        for (Integer categoryId : categoryIds) {
            this.getCategoryMap(allCategoryMap, categoryMap, allCategoryMap.get(categoryId.toString()));
        }
        List<ReportCategory> categories = categoryMap.values().stream().collect(Collectors.toList());
        return categories;
    }

    /**
     * 递归向上查找分类
     *
     * @param allCategoryMap
     * @param categoryMap
     * @param reportCategory
     */
    private void getCategoryMap(Map<String, ReportCategory> allCategoryMap, Map<String, ReportCategory> categoryMap, ReportCategory reportCategory) {
        if (!categoryMap.containsKey(reportCategory.getId().toString())) {
            categoryMap.put(reportCategory.getId().toString(), reportCategory);
        }

        if (reportCategory.getPid() != null && allCategoryMap.containsKey(reportCategory.getPid().toString())) {
            this.getCategoryMap(allCategoryMap, categoryMap, allCategoryMap.get(reportCategory.getPid().toString()));
        }
    }


}