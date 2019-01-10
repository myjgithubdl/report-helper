package com.myron.reporthelper.controller.home;

import com.myron.reporthelper.annotation.CurrentUser;
import com.myron.reporthelper.bo.EasyUITreeNode;
import com.myron.reporthelper.entity.SysMenu;
import com.myron.reporthelper.entity.User;
import com.myron.reporthelper.entity.UserMenu;
import com.myron.reporthelper.resp.ResponseResult;
import com.myron.reporthelper.service.impl.MembershipFacadeServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Home页控制器
 *
 * @author Tom Deng
 * @date 2017-03-25
 */
@Controller
@RequestMapping(value = {"", "/", "/home"})
public class HomeController {
    @Resource
    private MembershipFacadeServiceImpl membershipFacade;

    @GetMapping(value = {"", "/", "/index"})
    public String index(@CurrentUser final User loginUser, final Model model) {
        model.addAttribute("roleNames", this.membershipFacade.getRoleNames(loginUser.getRoles()));
        model.addAttribute("user", loginUser);
        return "home/index";
    }

    @ResponseBody
    @GetMapping(value = "/getMenus")
    public List<EasyUITreeNode<SysMenu>> getMenus(@CurrentUser final User loginUser) {
        final List<SysMenu> menus = this.membershipFacade.getSysMenus(loginUser.getRoles());


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
        List<SysMenu> menus = this.membershipFacade.getSysMenus(loginUser.getRoles());
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

}