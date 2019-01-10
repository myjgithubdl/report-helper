package com.myron.reporthelper.test;

import com.myron.reporthelper.entity.User;
import com.myron.reporthelper.service.impl.PasswordServiceImpl;
import com.myron.reporthelper.service.impl.UserServiceImpl;

public class Test {

    public static void main(String[] args) {

        User user=new User();
        user.setAccount("admin");
        user.setPassword("123456");

        UserServiceImpl userService=new UserServiceImpl();
      //  userService.encryptPassword(user);

        PasswordServiceImpl passwordService=new PasswordServiceImpl();


        String salt = passwordService.genreateSalt(user.getPassword());
        String encryptPassword = passwordService.encode(user.getPassword(), user.getCredentialsSalt());

        System.out.println(salt);
        System.out.println(encryptPassword);


    }

}
