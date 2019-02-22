package com.myron.reporthelper.controller.member;

import com.myron.reporthelper.resp.ResponseResult;
import com.myron.reporthelper.service.SysEventService;
import com.myron.reporthelper.util.CaptchaUtil;
import com.myron.reporthelper.util.LocaleUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 用户登录页控制器
 *
 * @author Tom Deng
 * @date 2017-03-25
 */
@Slf4j
@Controller
@RequestMapping(value = "/member")
public class LoginController {


    @Resource
    private SysEventService sysEventService;

    @GetMapping("/login/{lang}")
    public ModelAndView language(@PathVariable final String lang, final HttpServletRequest request,
                                 final HttpServletResponse response) {
        return new ModelAndView("redirect:/");
    }

    @GetMapping(value = {"/login"})
    public String login(final Model model, final HttpServletRequest request, final HttpServletResponse response) {
        log.info("跳转登录页面");
        return "member/login";
    }

    @GetMapping(value = "/logout")
    public String logout() {
        SecurityUtils.getSubject().logout();
        return "member/login";
    }

    @ResponseBody
    @PostMapping(value = "/authenticate")
    public ResponseResult authenticate(final String account, final String password, final boolean rememberMe,
                                       final String validationCode,
                                       final HttpServletRequest request) {
        ResponseResult result = null;

        if (!CaptchaUtil.validate(request, validationCode, true)) {
            return ResponseResult.error("验证码错误！");
        }

        try {
            final UsernamePasswordToken token = new UsernamePasswordToken(account, password);
            token.setRememberMe(rememberMe);
            final Subject subject = SecurityUtils.getSubject();
            subject.login(token);
            result = ResponseResult.success(true);
            log.info("账号[" + account + "]登录系统！");
        } catch (UnknownAccountException ex) {
            ex.printStackTrace();
            result = ResponseResult.error("账号不存在");
            log.error("登录账号" + account + "不存在！");
        } catch (IncorrectCredentialsException ex) {
            ex.printStackTrace();
            result = ResponseResult.error("密码错误！");
            log.error("登录账号" + account + "输入密码错误！");
        } catch (final Exception ex) {
            result = ResponseResult.error("用户名或密码不正确！");
            ex.printStackTrace();
            String exName = ex.getClass().getSimpleName();
            if ("LockedAccountException".equals(exName)) {
                result = ResponseResult.error("账号被锁定！");
            } else if ("ExcessiveAttemptsException".equals(exName)) {
                result = ResponseResult.error("登录失败次数过多！");
            } else if ("DisabledAccountException ".equals(exName)) {
                result = ResponseResult.error("帐号已经禁止登录！");
            }
        }
        this.sysEventService.add("authenticate", account, "用户登录", "INFO", request.getRequestURL().toString());
        return result;
    }


    /**
     * 生成验证码
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/pcrimg_V2")
    public void crimgV2(HttpServletRequest request, HttpServletResponse response) throws IOException {
        CaptchaUtil.generate(request, response);
    }


    /**
     * 校验验证码
     *
     * @param validationCode
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/checkValidationCodeV2", method = {RequestMethod.POST})
    @ResponseBody
    public ResponseResult checkValidationCodeV2(@RequestParam(value = "validationCode", required = true) String validationCode,
                                                HttpServletRequest request,
                                                HttpServletResponse response) {

        ResponseResult respBean = ResponseResult.error();
        if (validationCode == null || "".equals(validationCode.trim())) {
            respBean.setRespDesc("输入验证码为空");
        } else {
            if (CaptchaUtil.validate(request, validationCode, false)) {
                respBean = ResponseResult.success("验证成功！");
            } else {
                respBean.setRespDesc("验证码错误");
            }
        }
        return respBean;
    }


}