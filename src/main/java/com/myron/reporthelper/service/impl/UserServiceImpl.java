package com.myron.reporthelper.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.EmptyWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.myron.reporthelper.entity.User;
import com.myron.reporthelper.mapper.UserMapper;
import com.myron.reporthelper.service.PasswordService;
import com.myron.reporthelper.service.UserService;
import com.sun.org.apache.xerces.internal.util.EntityResolverWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 系统用户表 服务实现类
 * </p>
 *
 * @author 缪应江
 * @since 2018-12-27
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    UserMapper userMapper;

    @Autowired
    private PasswordService passwordService;


    @Override
    public void encryptPassword(final User user) {
        user.setSalt(this.passwordService.genreateSalt(user.getPassword()));
        user.setEncryptPassword(this.passwordService.encode(user.getPassword(), user.getCredentialsSalt()));
    }


    @Override
    public User getUserByAccount(final String account) {

        User user=User.builder().account(account).build();
        final QueryWrapper<User> wrapper=new QueryWrapper<User>();
        //wrapper.setEntity(user);
        wrapper.eq("account",account);
        return userMapper.selectOne(wrapper);
    }

}
