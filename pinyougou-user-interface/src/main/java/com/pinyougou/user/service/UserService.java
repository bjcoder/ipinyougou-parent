package com.pinyougou.user.service;

import com.pinyougou.pojo.TbUser;

/**
 * Created by a2363196581 on 2018/3/26.
 */
public interface UserService {
    public void add(TbUser user);

    public void createSmsCode(String phone);

    public boolean checkCode(String mobile, String code);
}
