package com.reporthelper.service;

import com.reporthelper.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 系统用户表 服务类
 * </p>
 *
 * @author Myron
 * @since 2018-12-27
 */
public interface UserService extends IService<User> {

    /**
     * 加密用户密码
     *
     * @param user
     */
    void encryptPassword(User user);


    /**
     * @param account
     * @return
     */
    User getUserByAccount(String account);





}
