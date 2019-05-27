package com.reporthelper.service.impl;

import com.reporthelper.service.PasswordService;
import org.apache.shiro.crypto.RandomNumberGenerator;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;
import org.springframework.stereotype.Service;

/**
 * 用户口令服务类
 */
@Service
public class PasswordServiceImpl implements PasswordService {
    private RandomNumberGenerator randomNumberGenerator = new SecureRandomNumberGenerator();
    private String algorithmName = "md5";
    private int hashIterations = 2;

    public void setRandomNumberGenerator(final RandomNumberGenerator randomNumberGenerator) {
        this.randomNumberGenerator = randomNumberGenerator;
    }

    public void setAlgorithmName(final String algorithmName) {
        this.algorithmName = algorithmName;
    }

    public void setHashIterations(final int hashIterations) {
        this.hashIterations = hashIterations;
    }

    @Override
    public String genreateSalt() {
        return this.randomNumberGenerator.nextBytes().toHex();
    }

    @Override
    public String genreateSalt(String password) {
        String salt = this.randomNumberGenerator.nextBytes().toHex();
        if (password == null || password.length() < 1) {
            return salt;
        }

        //密码长度
        int passwordLength = password.length();

        if (passwordLength > 30) {
            return salt;
        }

        //返回的盐
        StringBuilder returnSaltSb = new StringBuilder();
        if (passwordLength <= 15) {
            //如果密码长度小于15 偶数位为密码
            for (int i = 0; i < passwordLength; i++) {
                returnSaltSb.append(salt.substring(i, i + 1));
                returnSaltSb.append(password.substring(i, i + 1));
            }

            if (returnSaltSb.length() < 30) {
                returnSaltSb.append(salt.substring(0, (30 - returnSaltSb.length())));
            }
        } else {
            String passMoreStr = password.substring(15);
            //如果密码长度小于15 偶数位为密码
            for (int i = 0; i < passwordLength; i++) {
                returnSaltSb.append(salt.substring(i, i + 1));
                returnSaltSb.append(password.substring(i, i + 1));
            }
            //将多余的密码替换到基数为
            for (int j = 0; j < passMoreStr.length(); j++) {
                returnSaltSb.replace(2 * j, 2 * j + 1, passMoreStr.substring(j, j + 1));
            }
        }

        String retuenSalt = returnSaltSb.substring(0, 30);
        //转化为32位长度
        if (passwordLength < 10) {
            retuenSalt = retuenSalt + "0" + passwordLength;
        } else {
            retuenSalt = retuenSalt + passwordLength;
        }

        return retuenSalt;
    }

    @Override
    public String encode(final CharSequence rawPassword, final String credentialsSalt) {
        return new SimpleHash(
                this.algorithmName,
                rawPassword,
                ByteSource.Util.bytes(credentialsSalt),
                this.hashIterations).toHex();
    }

    @Override
    public String encode(final CharSequence rawPassword) {
        return this.encode(rawPassword, "shiro");
    }

    @Override
    public boolean matches(final CharSequence rawPassword, final String encodedPassword) {
        return this.matches(rawPassword, encodedPassword, "shiro");
    }

    @Override
    public boolean matches(final CharSequence rawPassword, final String encodedPassword, final String credentialsSalt) {
        final String encode = this.encode(rawPassword, credentialsSalt);
        return encode.equals(encodedPassword);
    }


}
